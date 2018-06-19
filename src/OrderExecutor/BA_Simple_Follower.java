/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderExecutor;

import Indicators.EWCorrelation;
import Indicators.EWMA;
import Indicators.Indicator;

/**
 *
 * @author nik
 */
public class BA_Simple_Follower extends BA_Indicator {

  private double mValue=0;
  private double mPeriod=10;
  private double wgt = 2d/(1d+mPeriod);

  private double mBID = -1;
  private double mASK = -1;
  private double mBIDSZ = -1;
  private double mASKSZ = -1;
  private double mSpread = -1;
  private double mMiddle = -1;

  public BA_Simple_Follower(double period) {
    mPeriod = period;
    wgt = 2d/(1d+mPeriod);
  }

  public void update(double bid, double ask, double bid_sz, double ask_sz) {

    double mid = Math.abs(ask+bid)/2d;
    double spr = Math.abs(ask-bid);

    // init
    if(mSpread<0) mSpread = spr;
    if(mMiddle<0) mMiddle = mid;

    if(mBID<0) mBID = bid;
    if(mASK<0) mASK = ask;

    if(mBIDSZ<0) mBIDSZ = bid_sz;
    if(mASKSZ<0) mASKSZ = ask_sz;

    mSpread = mSpread*(1d-wgt)+spr*wgt;
    mMiddle = mMiddle*(1d-wgt)+mid*wgt;

    mBID = bid;
    mASK = ask;

  }

  public void setDirectionAndPnL(double dir, double pnl) {
  }
  public void setTimeInMarket(long time) {
  }
  
  public double getBidAsk(BidAsk ba) {
    switch(ba) {
      case bid:
        return Math.max(mBID,mASK + mSpread/2d);
      case ask:
        return Math.min(mASK,mBID - mSpread/2d);
    }
    return 0;
  }

  public TSType getTSType() {
    return TSType.VALUE;
  };

  public double getValue() {
    return mValue;
  };

  public static void main(String[] args) {
    JNums.TSeriesRandomGenerator tsgen = new JNums.TSeriesRandomGenerator();
    BA_Simple_Follower baf = new BA_Simple_Follower(10);
    java.io.PrintWriter file = Utils.FileManager.openFile2Write("D:\\dev\\IBTesting\\dump.txt");
    for(int i=0;i<1000;i++) {
      double v1 = tsgen.getNext();
      double v2 = JNums.jMath.round(JNums.Stats.nextRandNorm()*0.05d,0.01d);
      double ask=JNums.jMath.round(Math.max(v1+v2,v1-v2),0.01d);
      double bid=JNums.jMath.round(Math.min(v1+v2,v1-v2),0.01d);
      baf.update(bid, ask, 10, 10);
      String msg = bid + " " + ask + " " + baf.getBidAsk(BidAsk.bid) + " " + baf.getBidAsk(BidAsk.ask);
      System.out.println(msg);
      file.println(msg + " " + baf.mASK + " " + baf.mBID );
    }
    file.close();
  }

}
