/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderExecutor;

import Indicators.*;

/**
 *
 * @author nik
 */
public abstract class BA_Indicator {
//  public abstract void update(double open,double high,double low,double close);
  public enum TSType {NONE,OHLC,VALUE};
  public enum BidAsk {bid,ask};
  public abstract TSType getTSType();
  public abstract double getValue();
  public abstract double getBidAsk(BidAsk ba);
  public abstract void setTimeInMarket(long time);
//  public abstract void setDirectionAndPnL(int dir,double pnl);
  public abstract void setDirectionAndPnL(double dir,double pnl);
  public static void main(String[] args) {
    System.out.println(TSType.NONE.ordinal());
  }
}
