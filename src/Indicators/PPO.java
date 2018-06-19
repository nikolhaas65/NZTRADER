/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Indicators;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import JNums.*;
import Utils.*;

/**
 *
 * @author nik
 */
public class PPO extends Indicator {
//  Percentage Price Oscillator

  private double mValue = 0;
  private double mFast = 5;
  private double mSlow = 10;
  private double mSmooth = 9;
  public EMA fastEMA = null;
  public EMA slowEMA = null;
  public EMA smoothEMA = null;

  public PPO(double fast, double slow, double smooth) {
    mFast = fast;
    mSlow = slow;
    mSmooth = smooth;
    fastEMA = new EMA(mFast);
    slowEMA = new EMA(mSlow);
    smoothEMA = new EMA(mSmooth);
  }

  public void update(double value) {
    fastEMA.update(value);
    slowEMA.update(value);
    double rat;
    if (slowEMA.getValue() != 0) {
      rat = 100d * (fastEMA.getValue() - slowEMA.getValue()) / slowEMA.getValue();
    } else {
      rat = 100d * (fastEMA.getValue() - slowEMA.getValue());
    }
    smoothEMA.update(rat);
    mValue = smoothEMA.getValue();
  }

  public TSType getTSType() {
    return TSType.VALUE;
  }

  public double getValue() {
    return mValue;
  }

  public static void main(String[] args) {
    int p = 0;

    String format1 = "###0.0000";

    DecimalFormat fm1 = new DecimalFormat(format1,
            new DecimalFormatSymbols(Locale.US));

    PPO fc = new PPO(5d,10d,2d);

    long ct = System.currentTimeMillis();

    HistoryLogger hlog = new HistoryLogger();

    double cum = 100;
    for (int k = 0; k < 10000; k++) {

      double rnd = Stats.nextRandNorm();
      if (Math.random() < 0.1) {
        double rnd2 = 5 * Stats.nextRandNorm();
        cum += rnd2;
      } else {
        cum += rnd;
      }

      fc.update(cum);
//      double prob = Stats.NormInvertedDirection(fc.getValue(), fc.getMiddle(), fc.getSigma());
      hlog.printRecord(101, "", cum, fc.getValue(),fc.fastEMA.getValue(),fc.slowEMA.getValue(),fc.smoothEMA.getValue(),
              0d,0d,0d);

    }

    System.out.println(System.currentTimeMillis() - ct);

  }
}
