/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Strategies;

import OrderExecutor.BA_Follower;
import java.util.*;
import java.io.*;

import com.ib.client.*;

import InstrumentManager.*;
import OrderManager.OrderManager;
import OrderManager.JOrder;
import JNums.*;
import OrderExecutor.*;
import Utils.*;
import TimeSeries.*;
import Graphs.*;
import Indicators.*;

/**
 *
 * @author nik
 */
public class FutDirect_Opt extends MacroStrategy {



  public FutDirect_Opt() {
    mStartupTime = System.currentTimeMillis();
  }

  public FutDirect_Opt(BarSeries.FeedType feedtype) {
    mStartupTime = System.currentTimeMillis();
    setFeedType(feedtype);
  }

  HistoryLogger hlog = null;

  static  Arbitrage.FuturesOptionsArb arbFUTOPT = null;
  static double[] call_bids=null;
  static double[] call_asks=null;
  static double[] put_bids=null;
  static double[] put_asks=null;

  static double fut_bid = 0;
  static double fut_ask = 0;

  static double[] strikes=null;
  static double[] cpflags=null;

  static double[] parCall_A = null;
  static double[] parCall_B = null;
  static double[] parPut_A = null;
  static double[] parPut_B = null;
  
  static double dCall_A=0, dPut_A=0,dCall_B=0, dPut_B=0;
  static double strikeATM=-1;

  static int nPoly=2;

//  indicators
  static  EWMA ewmaATM = null;

//      triggering stocks

  static TimeTriggerReading timer_30s = null;
  static double[] pStx30 = null;
  static double[] pStxPrev30 = null;
  static double stxPosMoves30 = 0;
  static double stxNegMoves30 = 0;
  static double stxTotMoves30 = 0;
  static double stxVariability30 = 0;

  static TimeTriggerReading timer_5s = null;
  static double[] pStx5 = null;
  static double[] pStxPrev5 = null;
  static double stxPosMoves5 = 0;
  static double stxNegMoves5 = 0;
  static double stxTotMoves5 = 0;
  static double stxVariability5 = 0;

  PrintWriter flog = null;

  public void Init() {

//    TIMES
    setTradingTime( TimeUtils.String2Time(6,"090500"),TimeUtils.String2Time(6,"172500"));
//    setDataFeedTime(TimeUtils.String2Time(6,"090000"),TimeUtils.String2Time(6,"173000"));
//    setTradingTime( TimeUtils.String2Time(6,"090500"),TimeUtils.String2Time(6,"224500"));
    setDataFeedTime(TimeUtils.String2Time(6,"075000"),TimeUtils.String2Time(6,"224500"));

//    hlog = new HistoryLogger();
    String day2read = TimeUtils.Time2String(5,System.currentTimeMillis());
    String out_dir = "D:\\dev\\analysis\\";
    flog = FileManager.openFile2Write(out_dir + "dump_stratIB_"+day2read + ".txt");

    long LTime = System.currentTimeMillis();
    timer_30s = new TimeTriggerReading(30000,LTime);
    timer_30s.start(LTime);
    timer_5s = new TimeTriggerReading(5000,LTime);
    timer_5s.start(LTime);

    System.out.println("Init strategy indicators");

    ewmaATM = new EWMA(100);

//    interval time
//    long vtime = Utils.TimeUtils.String2Time(6, "000500");

    if(arbFUTOPT==null) {
      arbFUTOPT = new Arbitrage.FuturesOptionsArb();
      arbFUTOPT.sortArbitrage("FUTOPT");
      strikes = arbFUTOPT.mStrikes;
      cpflags = arbFUTOPT.mCPflags;
//      System.out.println(arbFUTOPT.printStrikesNCPs());
      System.out.println("arbFUTOPT inited"); System.out.flush();

    }

  }

  private void updateIndicators(BarOHLC bar) {

//    System.out.println("Update indicators");

  }

  BA_Simple_Follower ba_estim = new BA_Simple_Follower(50);
  MA_FollowBidAsk microStrat_LMT = new MA_FollowBidAsk("LMT",ba_estim);

  NumberRenderer df = new NumberRenderer(3,3);
  
//  strategy parameters

  public synchronized void onUpdate() {

    if(notInited) {
      Init();
      notInited=false;
    }

//    System.out.println("strat");
    
// overhead
    FinSecurity finsec = InstrumentManager.getFinSecurity(mContractID);

    DataFeed currData = finsec.getDataFeed();
    long stime = TimeUtils.getDayTimeL(currData.getLastUpdateTime());

    double sCall=0;
    double sPut =0;

    if(finsec.getName().contains("OPT")) {
// OPTIONS
      int contract = finsec.getContractID();

      if(currData.changeBidAsk() && finsec.isOption()) {
        try {
          if(finsec.getCPflag()==1) {
            call_asks=arbFUTOPT.getCallAsks(contract, currData.ask);
            call_bids=arbFUTOPT.getCallBids(contract, currData.bid);
            parCall_A = SmileBuilder.fitPolyN(nPoly,strikes, call_asks);
            parCall_B = SmileBuilder.fitPolyN(nPoly,strikes, call_bids);
          } else if(finsec.getCPflag()==-1) {
            put_asks=arbFUTOPT.getPutAsks(contract, currData.ask);
            put_bids=arbFUTOPT.getPutBids(contract, currData.bid);
            parPut_A = SmileBuilder.fitPolyN(nPoly,strikes, put_asks);
            parPut_B = SmileBuilder.fitPolyN(nPoly,strikes, put_bids);
          }
        } catch(Exception ex) {
          System.out.println("FutDirect: FIT " + ex);
        }

        if(call_asks!=null && call_bids!=null && put_asks!=null && put_bids!=null) {
          for(int k3=0;k3<strikes.length;k3++) {
            sCall = sCall + call_asks[k3] - call_bids[k3];
            sPut  = sPut + put_asks[k3]  - put_bids[k3];
          }
        }

        try {
          if(parCall_A!=null && parPut_A!=null && parCall_B!=null && parPut_B!=null) {
            switch(nPoly) {
              case 2:
                dCall_A = 2*parCall_A[2]*strikeATM + parCall_A[1];
                dPut_A  = 2*parPut_A[2] *strikeATM + parPut_A[1];
                dCall_B = 2*parCall_B[2]*strikeATM + parCall_B[1];
                dPut_B  = 2*parPut_B[2] *strikeATM + parPut_B[1];
                break;
              case 3:
                double K2= strikeATM*strikeATM;
                dCall_A = 3*parCall_A[3]*K2 + 2*parCall_A[2]*strikeATM + parCall_A[1];
                dPut_A  = 3*parPut_A[3] *K2 + 2*parPut_A[2] *strikeATM + parPut_A[1];
                dCall_B = 3*parCall_B[3]*K2 + 2*parCall_B[2]*strikeATM + parCall_B[1];
                dPut_B  = 3*parPut_B[3] *K2 + 2*parPut_B[2] *strikeATM + parPut_B[1];
                break;
            }
          }
        } catch(Exception ex) {
          System.out.println("FutDirect Switch: " + ex);
        }
      }

    } else if(finsec.getName().contains("FUT")) {
// FUTURES
      fut_ask = currData.ask;
      fut_bid = currData.bid;
      if(strikeATM<=0 && fut_ask>0 && fut_bid>0) {
        strikeATM=(fut_ask+fut_bid)*0.5;
//        System.out.println("Strategy: StrikeATM: " + strikeATM + " fA: "+ fut_ask + " fB: "+ fut_bid);
        ewmaATM.update(strikeATM);
      }
    }

    if(finsec.getName().contains("STK")) {
// STOCKS
      try{

      int contract = finsec.getContractID();
      double[] pStx = arbFUTOPT.getPricesStx(contract,(currData.ask+currData.bid)*0.5);

      long time = System.currentTimeMillis();

  // ============== STOCKS CALCULATIONS
      if(timer_5s.isFired(time)) {
//              System.out.println("here5 " + time);
        pStx5 = pStx;
        double[] rets = new double[pStx5.length];
        if(pStxPrev5!=null) {
          stxPosMoves5 = 0;
          stxNegMoves5 = 0;
          stxTotMoves5 = 0;
          for(int r=0; r<pStxPrev5.length; r++) {
            stxPosMoves5 = stxPosMoves5 + Math.max(0.0, Math.signum(pStx5[r]-pStxPrev5[r]));
            stxNegMoves5 = stxNegMoves5 + Math.min(0.0, Math.signum(pStx5[r]-pStxPrev5[r]));
            stxTotMoves5 = stxTotMoves5 + Math.signum(pStx5[r]-pStxPrev5[r]);
            rets[r]=pStx5[r]-pStxPrev5[r];
          }
          try{
          stxVariability5=JNums.Stats.stdn(rets);
          }catch(Exception ex) {System.out.println("futdirect/stdn5: " +ex);}
        }
      }
      pStxPrev5 = pStx5;

      if(timer_30s.isFired(time)) {
//              System.out.println("here30 " + time);
        pStx30 = pStx;
        double[] rets = new double[pStx30.length];
        if(pStxPrev30!=null) {
          stxPosMoves30 = 0;
          stxNegMoves30 = 0;
          stxTotMoves30 = 0;
          for(int r=0; r<pStxPrev30.length; r++) {
            stxPosMoves30 = stxPosMoves30 + Math.max(0.0, Math.signum(pStx30[r]-pStxPrev30[r]));
            stxNegMoves30 = stxNegMoves30 + Math.min(0.0, Math.signum(pStx30[r]-pStxPrev30[r]));
            stxTotMoves30 = stxTotMoves30 + Math.signum(pStx30[r]-pStxPrev30[r]);
            rets[r]=pStx30[r]-pStxPrev30[r];
          }
          try{
          stxVariability30=JNums.Stats.stdn(rets);
          }catch(Exception ex) {System.out.println("futdirect/stdn30: " +ex);}
        }
      }
      pStxPrev30 = pStx30;
      } catch(Exception ex) {
        System.out.println("FutDirect Stocks: " + ex);
      }
    }

    double fr = TimeUtils.getDayFraction((double)System.currentTimeMillis());
    String log_msg = fr + " "+ fut_ask + " " + fut_bid +
            " " + dCall_A + " " + dCall_B + " " + dPut_A + " " + dPut_B +
            " " + stxPosMoves5 + " " + stxNegMoves5 +
            " " + stxVariability5 + " " + stxVariability30 +
            " " + sCall + " " + sPut;
    flog.println(log_msg);
    flog.flush();

    // TRIGGER

      if (false) {

        int direction = 0;

        OrderManager pm = finsec.getOrderManager();

//        System.out.println("MA_Strategy.onUpdate: Prepair order");
        System.out.println("MA_Strategy.onUpdate: Pos: tobe: " + pm.getTobePosition().getQuantity() +
                " curr: " + pm.getCurrentPosition().getQuantity());
        System.out.flush();

        double lmtPrice = 0;
        double target = 0;

//       hlog.printRecord(1,2, prevBar.getTime(),prevBar.getOpen(), prevBar.getHigh(),prevBar.getLow(),prevBar.getClose(),
//        vSAR,vSAR_1,prob10lo,prob40lo);

        // repetition of the above with stronger condition
        if (true && pm.getCurrentPosition().getQuantity()>=0) {

          target = -mLimit;
          direction = -1;
          System.out.println("MA_Strategy.onUpdate: TRIGGER SELL: midA: " +
                  df.format(currData.midA) + " tick: " +
                  jMath.round(currData.sprA,currData.tick_size) + " ask: " +
                  currData.ask);
        } else 
          if (true
              && pm.getCurrentPosition().getQuantity()<=0) {

          target =  mLimit;
          direction = 1;
          System.out.println("MA_Strategy.onUpdate: TRIGGER BUY: midA: " +
                  df.format(currData.midA) + " tick: " +
                  jMath.round(currData.sprA,currData.tick_size) + " bid: " +
                  currData.bid);
        }

        // exist strategy
        boolean exit_trend = false;
        if(pm.getCurrentPosition().getQuantity()>0) {
          //exit Long
          exit_trend=true;
          direction = -1;
          target = 0;
        } else
        if(pm.getCurrentPosition().getQuantity()<0) {
          //exit Short
          exit_trend=true;
          direction = 1;
          target = 0;
        }

        System.out.println("FutDirect.onUpdate: TRADE: #cntr: " + finsec.getContractID() + " dir: " + direction +
                " TrdAllw: " + tradeAllowed(stime) + " canTrd: " + finsec.canTrade());
        lmtPrice = BAPriceLevel.getEstimPriceLevel(
          direction,
          currData.bid, currData.ask,currData.midA, currData.sprA, 1);
        lmtPrice=jMath.round(lmtPrice,currData.tick_size);
        System.out.println("FutDirect.onUpdate: TRADE: lmptPrice: " + lmtPrice);
        // normal trade
        if(direction!=0 && tradeAllowed(stime) && finsec.canTrade()) {
          lmtPrice = BAPriceLevel.getEstimPriceLevel(
            direction,
            currData.bid, currData.ask,currData.midA, currData.sprA, 1);
          lmtPrice=jMath.round(lmtPrice,currData.tick_size);
          pm.TRADE(target,lmtPrice,microStrat_LMT);
        }

        // END OF DAY LIQUIDATE
        if(!tradeAllowed(stime) || !finsec.canTrade()) {
          if(pm.getCurrentPosition().getQuantity()!=0) {

            if(pm.getCurrentPosition().getQuantity()>0) {
              //exit Long
              direction = -1;
              target = 0;
            } else
            if(pm.getCurrentPosition().getQuantity()<0) {
              //exit Short
              direction = 1;
              target = 0;
            }

            System.out.println("Liquidate positions");
            lmtPrice = BAPriceLevel.getEstimPriceLevel(
              direction,
              currData.bid, currData.ask,currData.midA, currData.sprA, 1);
            lmtPrice=jMath.round(lmtPrice,currData.tick_size);
            pm.TRADE(target,lmtPrice,microStrat_LMT);

          }
        }

      } // trigger

  } // update
  
  // - FOR TEST PURPOSES
  static public void main(String[] args) {
    
    System.out.println("START IB_Test2.StrategyRunner.main");
    
    
  }
  
}
