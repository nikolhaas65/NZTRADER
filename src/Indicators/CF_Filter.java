/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Indicators;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import Utils.*;

import JNums.*;

//import org.apache.commons.math.random.*;

/**
 *
 * @author nik
 */
public class CF_Filter extends Indicator {

  public class cfParams {
      public double m1 = 0;
      public double m2 = 0;
      public double m3 = 0;
      public double m4 = 0;
      public double currVal = 0;
      public long cnt = 0;

      public void update(double val) {
          m1 = val;
          m2 = m1 * val;
          m3 = m2 * val;
          m4 = m3 * val;
      }

      public void reset(double val) {
          update(val);
          currVal = val;
      }

      public void reset() {
          reset(0);
          cnt = 0;
      }

  }

  public TSType getTSType() {
    return TSType.VALUE;
  };

  public double getValue() {
    return mParams.currVal;
  }

//  public double[] mOutParams = null;;
  public cfParams mParams = null;
  private double mDays = 40;
  private double mDecay = 2.0d/(1.0d+mDays);
  private double mNSigmas = 1.23;
  private double mPrecisionUP = 1e5d;
  private double mPrecisionDOWN = 1e-5;

  double vMiddle; public double getMiddle() {return vMiddle;}
  double vSigma; public double getSigma() {return vSigma;}
  double vSkew; public double getSkew() {return vSkew;}
  double vKurtosis; public double getKurtosis() {return vKurtosis;}
  double vLowerValue; public double getLower() {return vLowerValue;}
  double vUpperValue; public double getUpper() {return vUpperValue;}
  double vCount; public double getCount() {return vCount;}
  double vCurrentValue; public double getCurrentValue() {return vCurrentValue;}

  public CF_Filter(double days, double za,
      double precDOWN, double precUP) {

    mParams=new cfParams();
    mDays = days;
    mDecay = 2.0d/(1.0d+mDays);
    mNSigmas = za;
    mPrecisionUP = precUP;
    mPrecisionDOWN = precDOWN;

  }

  public void reset(double val) {
      mParams.reset(val);
  }

  public void update(double val) {
      mParams.update(val);
  }

  public void calcParams(double val) {

    double m1, m2, m3, m4;
    double m1b, m2b, m3b, m4b;

    if (mParams.cnt == 0) {
      mParams.reset(val);
      mParams.cnt = 0;
    } else {

      if (Math.abs(mParams.currVal - val) >= mPrecisionDOWN
              & Math.abs(mParams.currVal - val) <= mPrecisionUP & Math.abs(val) > 1e-8) {
        // origin moments
        m1 = val;
        m2 = m1 * val;
        m3 = m2 * val;
        m4 = m3 * val;

        mParams.m1 = (1 - mDecay) * mParams.m1 + mDecay * m1;
        mParams.m2 = (1 - mDecay) * mParams.m2 + mDecay * m2;
        mParams.m3 = (1 - mDecay) * mParams.m3 + mDecay * m3;
        mParams.m4 = (1 - mDecay) * mParams.m4 + mDecay * m4;
        mParams.currVal = val;
      }

      // central moments
      m1b = mParams.m1;
      m2b = mParams.m2 - m1b * m1b;
      m3b = mParams.m3 - 3.0 * m1b * mParams.m2 + 2.0 * Math.pow(m1b, 3);
        m4b = mParams.m4 - 4.0 * m1b * mParams.m3 +
                6.0 * Math.pow(m1b, 2) * mParams.m2 - 3.0 * Math.pow(m1b, 4);

        double sig = Math.sqrt(Math.abs(m2b));
        double skew = m3b / Math.pow(sig, 3);
        double kurt = m4b / Math.pow(sig, 4) - 3.0;
        double dda = mNSigmas + (Math.pow(mNSigmas, 3) - 3.0 * mNSigmas) * kurt / 24.0 -
                (2.0 * Math.pow(mNSigmas, 3) - 5.0 * mNSigmas) * skew * skew / 36.0;
        double cfa = (mNSigmas * mNSigmas - 1.0) * skew / 6.0;

        // due to CF-expantion canbe cfUP<cfDW. fix is rough.
        double cfUP = m1b + (dda + cfa) * sig;
        double cfDW = m1b - (dda - cfa) * sig;

        if(cfUP<=cfDW) {
          cfUP = m1b + mNSigmas*sig/2;
          cfDW = m1b - mNSigmas*sig/2;
        }

        vMiddle = m1b;
        vSigma = sig;
        vSkew = skew;
        vKurtosis = kurt;
        vLowerValue = cfDW;
        vUpperValue = cfUP;
        vCount = mParams.cnt;
        vCurrentValue = mParams.currVal;

    }

    mParams.cnt = mParams.cnt + 1;

  }
  
  public static void main(String[] args) {
      int p=0;
      
      String format1 = "###0.0000" ;  

      DecimalFormat fm1 = new DecimalFormat( format1,
                                   new DecimalFormatSymbols(Locale.US));

      CF_Filter fc = new CF_Filter(0.05, 1.23,1e-5,1e5);

      long ct=System.currentTimeMillis();

      HistoryLogger hlog = new HistoryLogger();

      double cum=100;
      for (int k=0;k<10000;k++) {

          double rnd=Stats.nextRandNorm();
          if(Math.random()<0.1) {
            double rnd2=5*Stats.nextRandNorm();
            cum+=rnd2;
          } else {
            cum+=rnd;
          }

          fc.calcParams(cum);
          double prob=Stats.NormInvertedDirection(fc.getCurrentValue(), fc.getMiddle(), fc.getSigma());
          hlog.printRecord(101, "", cum, prob, fc.getMiddle(), fc.getSigma(), fc.getSkew(),
                  fc.getKurtosis(), fc.getLower(), fc.getUpper());

      }

      System.out.println(System.currentTimeMillis()-ct);

  }
}
