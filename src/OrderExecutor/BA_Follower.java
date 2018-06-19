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
public class BA_Follower extends BA_Indicator {

  private double mValue=0;
  private double mPeriod=10;
  private double wgt = 2d/(1d+mPeriod);
  private long mTimeInMarket = 0;

  private double mBID = -1;
  private double mASK = -1;
  private double mBIDSZ = -1;
  private double mASKSZ = -1;
  private double mEWSpread = -1;
  private double mEWMiddle = -1;
  private double mCorrBid = 0;
  private double mCorrAsk = 0;

  private EWCorrelation ewCorrA = null;
  private EWCorrelation ewCorrB = null;
  private EWCorrelation ewCorrBA = null;
  private EWMA stdSPREAD = null;
  private EWMA stdMID = null;

  public BA_Follower(double period) {
    mPeriod = period;
    wgt = 2d/(1d+mPeriod);
    ewCorrA = new EWCorrelation(period);
    ewCorrB = new EWCorrelation(period);
    ewCorrBA = new EWCorrelation(period);
    stdSPREAD = new EWMA(period);
    stdMID = new EWMA(period*2);
  }

  public synchronized void update(double bid, double ask, double bid_sz, double ask_sz) {

    // [bid_sz/bid:ask/ask_sz]
    System.out.println("BAFollower.update: [" + bid_sz +"/"+ bid + ":" + ask + "/"+ ask_sz +"] ");

    double mid = Math.abs(ask+bid)/2d;
    double spr = Math.abs(ask-bid);

    // init
    if(mEWSpread<0) mEWSpread = spr;
    if(mEWMiddle<0) mEWMiddle = mid;

    if(mBID<0) mBID = bid;
    if(mASK<0) mASK = ask;

    if(mBIDSZ<0) mBIDSZ = bid_sz;
    if(mASKSZ<0) mASKSZ = ask_sz;

    double shBID = bid-mBID;
    double shASK = ask-mASK;
    double shSPREAD = spr-mEWSpread;

//    System.out.println(">> " + shBID + " " + shASK);
    ewCorrA.update(shASK,shSPREAD);
    ewCorrB.update(shBID,shSPREAD);
    ewCorrBA.update(shBID,shASK);
    stdSPREAD.update(shSPREAD);
    stdMID.update(mid);

    mCorrBid = ewCorrB.getValue();
    mCorrAsk = ewCorrA.getValue();

    mEWSpread = mEWSpread*(1d-wgt)+spr*wgt;
    mEWMiddle = mEWMiddle*(1d-wgt)+mid*wgt;

    mBID = bid;
    mASK = ask;

  }

  double mPNL=0;
//  int mDIR=0;
  double mDIR=0;
//  public synchronized void setDirectionAndPnL(int dir, double pnl) {
  public synchronized void setDirectionAndPnL(double dir, double pnl) {
    mPNL=pnl;
    mDIR=dir;
  }

  public synchronized void setTimeInMarket(long time) {
    mTimeInMarket=time;
  }

  static Utils.NumberRenderer df = new Utils.NumberRenderer(3,3);

  int method =7;

  public synchronized double getBidAsk(BidAsk ba) {

    double a_price=mASK;
    double b_price=mBID;
    double price = (mBID+mASK)*0.5;
    double corr = 0.0d;

    try {

    switch(method) {
      case 0:
        b_price = mBID-0.1;
        a_price = mASK+0.1;
        break;
      case 1:
        b_price = Math.min(mBID + mEWSpread/2d,mASK - mEWSpread/2d + mCorrBid*ewCorrB.getEWStdev1().getStdev());
        a_price = Math.max(mASK - mEWSpread/2d,mBID + mEWSpread/2d + mCorrAsk*ewCorrA.getEWStdev1().getStdev());
        break;
      case 2:
        b_price = Math.min(mBID+mEWSpread*0.5,mBID - stdMID.getStdev());
        a_price = Math.max(mASK-mEWSpread*0.5,mASK + stdMID.getStdev());
        break;
      case 3:
        b_price = mBID - Math.signum(mPNL)*stdMID.getStdev();
        a_price = mASK + Math.signum(mPNL)*stdMID.getStdev();
        break;
      case 4:
        corr =  Math.signum(mPNL)*0.5;
        b_price = Math.min(mBID,mBID - corr*stdMID.getStdev());
        a_price = Math.max(mASK,mASK + corr*stdMID.getStdev());
        break;
      case 5:
        corr = Math.max(0.0d,JNums.Funcs.sigmoid(mPNL,0d,20d)*0.2d);
        b_price = mBID - corr*stdMID.getStdev();
        a_price = mASK + corr*stdMID.getStdev();
        break;
      case 6:

        double v = mPNL>0? JNums.Funcs.sigmoid(mPNL,0.0d, 40.0d)*0.2d:
          mPNL<0? JNums.Funcs.sigmoid(mPNL,0.0d, 50.0d)*0.05d : 0;

        double dir = Math.signum(mDIR);
        a_price = mEWMiddle - dir*v + 0.5*mEWSpread;
        b_price = mEWMiddle - dir*v - 0.5*mEWSpread;
    // [bid:ask] / [high:low] spread sigmoid(pnl) = price
        System.out.println("getBidAsk: "+ ba +
                " [" + df.format(mBID) + ":" + df.format(mASK) + "] " +
                df.format(mEWSpread) + " std:" + df.format(stdMID.getStdev()) +
                " sig: " + df.format(mDIR) + "("+ df.format(mPNL) + ")");
        break;
      case 7:

        double dir2 = Math.signum(mDIR);

        // positively defined for negative PNL.
        double f = -JNums.Funcs.ba_band(0.0001d,0.0005d, mPNL+40.0d);
        f = JNums.Funcs.limitMiMa(f, -1.0d, 1.0d)*0.05d/1000;

//        if(ba==BidAsk.bid) {
          a_price = mASK-f*dir2+0.25*mEWSpread;
          b_price = mBID-f*dir2-0.25*mEWSpread;
//        } else if(ba==BidAsk.ask) {
//           a_price = mASK-f*dir2+0.25*mEWSpread;
//           b_price = mBID-f*dir2-0.25*mEWSpread;
//        }

        System.out.println("getBidAsk7: "+ ba + " " + dir2 + " " +
                " [" + df.format(mBID) + ":" + df.format(mASK) + "] vs " +
                " [" + df.format(b_price) + ":" + df.format(a_price) + "] " +
                df.format(mEWSpread) + " std: " + df.format(stdMID.getStdev()) +
                " f: " + df.format(f*dir2) + "("+ df.format(mPNL) + ")");
//        System.out.println(stdMID.getStdev());
        break;
    }

    if(ba==BidAsk.bid) price= b_price;
    else if(ba==BidAsk.ask) price = a_price;

    System.out.println("getBIdAsk [" +
            df.format(b_price) +":" +
            df.format(a_price) + "]" +
            " -> " + ba + ": " + price);

    System.out.flush();

    } catch (Exception ex) {System.out.println("getBidAsk:error: " + ex);}

    return price;
  }

  public TSType getTSType() {
    return TSType.VALUE;
  };

  public double getValue() {
    return mValue;
  };

  public static void main(String[] args) {
    JNums.TSeriesRandomGenerator tsgen = new JNums.TSeriesRandomGenerator();
    BA_Follower baf = new BA_Follower(10);
//    java.io.PrintWriter file = Utils.FileManager.openFile2Write("D:\\dev\\IBTesting\\dump.txt");
    for(int i=0;i<2;i++) {
      double v1 = tsgen.getNext();
      double v2 = JNums.jMath.round(JNums.Stats.nextRandNorm()*0.05d,0.01d);
      double ask=JNums.jMath.round(Math.max(v1+v2,v1-v2),0.01d);
      double bid=JNums.jMath.round(Math.min(v1+v2,v1-v2),0.01d);
      baf.update(bid, ask, 0, 0);
      String msg = bid + " " + ask + " " + baf.getBidAsk(BidAsk.bid) + " " + baf.getBidAsk(BidAsk.ask);
      System.out.println(msg);
//      file.println(msg + " " + baf.mASK + " " + baf.mBID );
    }
//    file.close();
  }

}
