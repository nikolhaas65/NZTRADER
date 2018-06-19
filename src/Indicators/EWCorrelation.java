/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Indicators;

/**
 *
 * @author nik
 */
public class EWCorrelation extends Indicator {

  private EWMA stdev12=null;
  private EWMA stdev1=null;
  private EWMA stdev2=null;

  private boolean init = true;

  public EWCorrelation(double period) {
    stdev1=new EWMA(period);
    stdev2=new EWMA(period);
    stdev12=new EWMA(period);
  }

  public TSType getTSType() {
    return TSType.VALUE;
  };

  public void update(double open, double high, double low, double close) {

  }

  public void update(double value1,double value2) {
    stdev1.update(value1);
    stdev2.update(value2);
    stdev12.update(value1*value2);
  }

  public void update2(double value1,double value2) {
    if(init) {
      stdev1.update(0);
      stdev2.update(0);
      stdev12.update(0);
      init=false;
    } else {
      double d1=value1-stdev1.getMean();
      double d2=value2-stdev2.getMean();
      stdev1.update(d1);
      stdev2.update(d2);
      stdev12.update(d1*d2);
    }
  }

  public double getValue() {
    return stdev12.getMean()/stdev1.getStdev()/stdev2.getStdev();
  }

  public EWMA getEWStdev1() {
    return stdev1;
  }
  public EWMA getEWStdev2() {
    return stdev2;
  }

  public static void main(String[] args) {
    int trials = 1000;
    double[] x = new double[trials];
    double[] y = new double[trials];
    
    EWCorrelation ewc = new EWCorrelation(100);
    
    for(int i=0;i<trials;i++) {
      x[i] = Math.random();
      y[i] = Math.random();
      ewc.update2(x[i], y[i]);
      System.out.println(ewc.stdev1.getMean() + " " + ewc.stdev2.getMean() + " " + ewc.getValue());
    }
  }

}
