/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Indicators;

/**
 *
 * @author nik
 */
public abstract class Indicator {
//  public abstract void update(double open,double high,double low,double close);
  public enum TSType {NONE,OHLC,VALUE};
  public abstract TSType getTSType();
  public abstract double getValue();
  public static void main(String[] args) {
    System.out.println(TSType.NONE.ordinal());
  }
}
