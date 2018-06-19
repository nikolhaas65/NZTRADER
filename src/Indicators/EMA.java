/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Indicators;

/**
 *
 * @author nik
 */
public class EMA extends Indicator {
  private double mValue=0;
  private double mPeriod=10d;
  private double w = 2d/(1d+mPeriod);

  public EMA(double period) {
    mPeriod = period;
    w = 2d/(1d+mPeriod);
//    System.out.println("period: " + period);
  }

  public void update(double value) {
    if(mValue<=0) {
      mValue=value;
    } else {
      mValue = mValue*(1d-w) + value*w;
    }
//    System.out.println("period: " + mPeriod + " " + mValue);
  }

  public TSType getTSType() {
    return TSType.VALUE;
  };

  public double getValue() {
    return mValue;
  };

}
