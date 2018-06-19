/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Indicators;

/**
 *
 * @author nik
 */
public class SARIndicator extends Indicator {

  double nextsar;
  double prevsar;
  double sar;
  boolean mLongPosition;
  double sip;
  double af;
  int    cnt;

  double low_1, low_2;
  double high_1,high_2;

  int counter = 0;
  int minCnt = 16;
  double ave_close = 0;

  double mStartAccelerator; // starting smoothener, def: 0.02
  double mAcceleratorStep; // increment smoothener, def : 0.02
  double mMaxAccelerator; // max smoothener, def: 0.2

  public TSType getTSType() {
    return TSType.OHLC;
  };
  
  public SARIndicator(double accelerator,double step_accelerator,double max_accelerator) {
    mLongPosition=true;
    sar=sip=prevsar=nextsar=0;
    af=accelerator*4d;
    mStartAccelerator=accelerator;
    mAcceleratorStep=step_accelerator;
    mMaxAccelerator=max_accelerator;
    cnt=1;
    low_1=low_2=high_1=high_2=0;
  }

  public void update(double value) {
    update(value,value,value,value);
  }


  public void update(double open, double high, double low, double close) {

    prevsar=sar;
    
    if(counter>minCnt) {

      if( (mLongPosition && low<prevsar) || (!mLongPosition && (high>prevsar)) ) {
        mLongPosition=!mLongPosition;
        prevsar = sar;
        sar = sip;
        af = mStartAccelerator;
        sip = mLongPosition ? high : low;
      }
      else if(mLongPosition && high>sip) {
        sip = high;
        af = Math.min(mMaxAccelerator, af+mAcceleratorStep);
      }
      else if(!mLongPosition && low<sip) {
        sip = low;
        af = Math.min(mMaxAccelerator, af+mAcceleratorStep);
      }

    double todaySAR = sar + af*(sip-sar);
    sar = mLongPosition ? Math.min(Math.min(todaySAR,low_2), low_1):
                         Math.max(Math.max(todaySAR,high_2), high_1);

    } else if(counter==minCnt) {
      double aveCl = ave_close/minCnt;
      mLongPosition = close>aveCl;
      sip = Math.max(mLongPosition?high:low,close);
      if(mLongPosition) {
//        sip = high;
        sar = low;
      } else {
//        sip = low;
        sar = high;
      }

      double todaySAR = sar + af*(sip-sar);
      sar = mLongPosition ? Math.min(Math.min(todaySAR,low_2), low_1):
                           Math.max(Math.max(todaySAR,high_2), high_1);

    } else { // <minCnt
      sar=close;
      ave_close+=close;
    }

    low_2=low_1;
    low_1=low;
    high_2=high_1;
    high_1=high;

    counter++;

  }

  public double getValue() {
    return sar;
  }
  
}
