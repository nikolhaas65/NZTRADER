/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Indicators;

/**
 *
 * @author nik
 */
public class EWMA  extends Indicator {
  private double m1;
  private double m2;
  private double mPeriod=10.0d;
  private double wgt=2.0d/(1.0d+mPeriod);
  private boolean init=true;

  public TSType getTSType() {
    return TSType.VALUE;
  };

  public EWMA(double period) {
    mPeriod = period;
    wgt = 2.0d/(1.0d+mPeriod);
  }

  public void update(double open, double high, double low, double close) {

  }

  public void update(double value) {
    if(init) {
      m1=value;
      m2=m1*m1;
      init=false;
    }
    m1 = wgt*value + (1.0d-wgt)*m1;
    m2 = wgt*value*value + (1.0d-wgt)*m2;
  }

  public double getValue() {
    return m1;
  }
  public double getMean() {
    return m1;
  }
  public double getStdev() {
    double v=m2-m1*m1;
    if(v>=0.0) {v=Math.sqrt(v);}
    else {v=0.0;};
    return v;
  }

}
