/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Strategies;

import OrderExecutor.BA_Follower;
import java.util.*;

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
public class FutBarredNeg_Strategy extends MacroStrategy {

  public FutBarredNeg_Strategy() {
    mStartupTime = System.currentTimeMillis();
  }

  public FutBarredNeg_Strategy(BarSeries.FeedType feedtype) {
    mStartupTime = System.currentTimeMillis();
    setFeedType(feedtype);
  }

//  Definition of Bars and Indicators
  SARIndicator sar06 = null;
  SARIndicator sar = null;
  double vSAR_1 = 0;
  double vSAR = 0;
  double vSAR06_1 = 0;
  double vSAR06 = 0;
  CF_Filter cfHigh_10 = null;
  CF_Filter cfHigh_40 = null;
  CF_Filter cfLow_10 = null;
  CF_Filter cfLow_40 = null;
  PPO ppo = null;
  double vPPO = 0;
  double vPPO_1 = 0;
  double vPPO_2 = 0;
  VOLLOGMA vollogma = null;
  double sigVol = 0;

  BarSeries barTS = null;
  HistoryLogger hlog = null;

  public void Init() {

    setTradingTime( TimeUtils.String2Time(6,"090500"),TimeUtils.String2Time(6,"172500"));
    setDataFeedTime(TimeUtils.String2Time(6,"090000"),TimeUtils.String2Time(6,"173000"));
//    setTradingTime( TimeUtils.String2Time(6,"090500"),TimeUtils.String2Time(6,"224500"));
//    setDataFeedTime(TimeUtils.String2Time(6,"090000"),TimeUtils.String2Time(6,"224500"));

    System.out.println("Init strategy indicators");

    hlog = new HistoryLogger();

    int buffSZ=50;
    long vtime = Utils.TimeUtils.String2Time(6, "000500");

//    barTS = new BarSeries(mSecName,mFeedType,vtime,1,BarSeries.PeriodType.minutes,buffSZ);
    barTS = new BarSeries(mSecName,mFeedType,vtime,2,BarSeries.PeriodType.minutes,buffSZ);
    TSManager.setTS(barTS,mFinSec);
    System.out.println("SecName: " + mSecName);

    LinkedOHLCDataset ohlc_ds = barTS.getLinkedOHLCDataset(buffSZ);
    TSManager.setLinkedOHLC(barTS, ohlc_ds);

    // SAR Indicator
    sar = new SARIndicator(0.02,0.2,0.02);
    String sar_name = "SAR";
    LinkedXYDataset tsSAR = new LinkedXYDataset(mSecName + "_" + sar_name,buffSZ);
    TSManager.setLinkedXY(barTS,sar_name, tsSAR);
    // SAR Indicator
    sar06 = new SARIndicator(0.02,0.2,0.05);
    String sar06_name = "SAR06";
    LinkedXYDataset tsSAR06 = new LinkedXYDataset(mSecName + "_" + sar06_name,buffSZ);
    TSManager.setLinkedXY(barTS,sar06_name, tsSAR06);

    double stdev=1.;
//    CF Filter-10 High
    cfHigh_10 = new CF_Filter(10d,stdev,1e-5,1e5);
    String cfHigh_10_name = "CF";
    LinkedXYDataset tsCFHigh_10 = new LinkedXYDataset(mSecName + "_" + cfHigh_10_name,buffSZ);
    TSManager.setLinkedXY(barTS,cfHigh_10_name, tsCFHigh_10);

//    CF Filter-40 High
    cfHigh_40 = new CF_Filter(40d,stdev,1e-5,1e5);
    String cfHigh_40_name = "CF";
    LinkedXYDataset tsCFHigh_40 = new LinkedXYDataset(mSecName + "_" + cfHigh_40_name,buffSZ);
    TSManager.setLinkedXY(barTS,cfHigh_40_name, tsCFHigh_40);

//    CF Filter-10 Low
    cfLow_10 = new CF_Filter(10d,stdev,1e-5,1e5);
    String cfLow_10_name = "CF";
    LinkedXYDataset tsCFLow_10 = new LinkedXYDataset(mSecName + "_" + cfLow_10_name,buffSZ);
    TSManager.setLinkedXY(barTS,cfLow_10_name, tsCFLow_10);

//    CF Filter-40 Low
    cfLow_40 = new CF_Filter(40d,stdev,1e-5,1e5);
    String cfLow_40_name = "CF";
    LinkedXYDataset tsCFLow_40 = new LinkedXYDataset(mSecName + "_" + cfLow_40_name,buffSZ);
    TSManager.setLinkedXY(barTS,cfLow_40_name, tsCFLow_40);

//    PPO
    ppo = new PPO(5d,10d,1d);
    String ppo_name = "PPO";
    LinkedXYDataset tsPPO = new LinkedXYDataset(mSecName + "_" + ppo_name,buffSZ);
    TSManager.setLinkedXY(barTS,ppo_name, tsPPO);

//    VOLLOGMA
    vollogma = new VOLLOGMA(1.8d,40d);
    String vollogma_name = "VOLLOGMA";
    LinkedXYDataset tsVOLLOGMA = new LinkedXYDataset(mSecName + "_" + vollogma_name,buffSZ);
    TSManager.setLinkedXY(barTS,vollogma_name, tsVOLLOGMA);

  }

  private void updateIndicators(BarOHLC bar) {

    System.out.println("Update indicators");

    vSAR_1 = vSAR;
    sar.update(bar.getOpen(), bar.getHigh(),bar.getLow(),bar.getClose());
    vSAR=sar.getValue();

    vSAR06_1 = vSAR06;
    sar06.update(bar.getOpen(), bar.getHigh(),bar.getLow(),bar.getClose());
    vSAR06=sar06.getValue();

    vPPO_2 = vPPO_1;
    vPPO_1 = ppo.getValue();
    ppo.update(bar.getClose());
    vPPO = ppo.getValue();

    cfHigh_10.calcParams(bar.getHigh());
    cfHigh_40.calcParams(bar.getHigh());
    cfLow_10.calcParams(bar.getLow());
    cfLow_40.calcParams(bar.getLow());

    vollogma.update(bar.getOpen(), bar.getHigh(),bar.getLow(),bar.getClose(), bar.getVolume());
    sigVol = vollogma.getValue();

    String cntr = Integer.toString(this.mContractID);
//    hlog.printRecord(1,cntr, bar.getTime(),bar.getOpen(), bar.getHigh(),bar.getLow(),bar.getClose(),
//            vSAR, vSAR06, vPPO, sigVol);
//
//    hlog.printRecord(2,cntr, bar.getTime(),bar.getOpen(), bar.getHigh(),bar.getLow(),bar.getClose(),
//            cfHigh_10.getMiddle(),cfHigh_10.getSigma(),cfHigh_40.getMiddle(),cfHigh_40.getSigma());
//
//    hlog.printRecord(3,cntr, bar.getTime(),bar.getOpen(), bar.getHigh(),bar.getLow(),bar.getClose(),
//            cfLow_10.getMiddle(),cfLow_10.getSigma(),cfLow_40.getMiddle(),cfLow_40.getSigma());

  }

  BA_Simple_Follower ba_estim = new BA_Simple_Follower(50);
  MA_FollowBidAsk microStrat_LMT = new MA_FollowBidAsk("LMT",ba_estim);

  NumberRenderer df = new NumberRenderer(3,3);
  
//  strategy parameters
  private double thrOsc = 0.1; // Default setting for thrOsc
  private double thrProbDwns = -0.3; // Default setting for ThrProbDwns
  private double thrProbUps = 0.35; // Default setting for ThrProbUps
  private double thrProbVolume = 0.05; // Default setting for ThrProbVolume
  private double trailStopLevel = 180.0;
  private int strategy_direction = 1;

  public synchronized void onUpdate() {

//    System.out.println("strat");
    
// overhead
    FinSecurity finsec = InstrumentManager.getFinSecurity(mContractID);

    DataFeed currData = finsec.getDataFeed();
//    if (currData.cnt<10) return;
    long stime = TimeUtils.getDayTimeL(currData.getLastUpdateTime());
//    if (!algoCalcAllowed(stime)) return;

//    System.out.println("Strat// FinSecName: " + finsec.getName() + " ContractID: " + mContractID + " dfeed:" + currData);
//    barTS.update(currData.getLastUpdateTime(), currData.last, currData.last_sz);
    barTS.update(currData.getLastUpdateTime(), (currData.ask+currData.bid)/2, currData.ask_sz+currData.bid_sz);

    if(barTS.isIncremented()) {
//    System.out.println("strat " + TimeUtils.Time2String(3, currData.getLastUpdateTime()));
//    System.out.println("Name " + TimeUtils.Time2String(1, currData.getLastUpdateTime()) + " " + finsec.getName());

      BarOHLC prevBar = barTS.getOneButLast();
      updateIndicators(prevBar);

      double prob10up = JNums.Stats.NormInvertedDirection(prevBar.getHigh(),
              cfHigh_10.getMiddle(),cfHigh_10.getSigma());

      double prob10lo = JNums.Stats.NormInvertedDirection(prevBar.getLow(),
              cfLow_10.getMiddle(),cfLow_10.getSigma());

      double prob40up = JNums.Stats.NormInvertedDirection(prevBar.getHigh(),
              cfHigh_40.getMiddle(),cfHigh_40.getSigma());

      double prob40lo = JNums.Stats.NormInvertedDirection(prevBar.getLow(),
              cfLow_40.getMiddle(),cfLow_40.getSigma());

      System.out.println("prevBar: " + prevBar.toString());
      System.out.println(finsec.getName() + " [" + df.format(prob10up) + "," + df.format(prob40up) +
              "] [" + df.format(prob10lo) + "," + df.format(prob40lo) + "] " +
              df.format(sigVol));

      System.out.print(">> hi10 " + df.format(cfHigh_10.getMiddle()) + " "+ df.format(cfHigh_10.getSigma()));
      System.out.print(" / hi40 " + df.format(cfHigh_40.getMiddle()) + " "+ df.format(cfHigh_40.getSigma()));
      System.out.print(" / lo10 " + df.format(cfLow_10.getMiddle()) + " "+ df.format(cfLow_10.getSigma()));
      System.out.println(" / lo40 " + df.format(cfLow_40.getMiddle()) + " "+ df.format(cfLow_40.getSigma()));

    // TRIGGER

      hlog.printRecord(1,mContractID, prevBar.getTime(),prevBar.getOpen(), prevBar.getHigh(),prevBar.getLow(),prevBar.getClose(),
        sigVol,vPPO_1,prevBar.getVolume(),vPPO);

      if (sigVol > thrProbVolume && Math.abs(vPPO_1) < thrOsc && prevBar.getVolume()>0) {

        int trade_direction = 0;

        OrderManager pm = finsec.getOrderManager();

//        System.out.println("MA_Strategy.onUpdate: Prepair order");
        System.out.println("MA_Strategy.onUpdate: Pos: tobe: " + pm.getTobePosition().getQuantity() +
                " curr: " + pm.getCurrentPosition().getQuantity());
        System.out.flush();

        double lmtPrice = 0;
//        int target = 0;
        double target = 0.0;

//       hlog.printRecord(1,2, prevBar.getTime(),prevBar.getOpen(), prevBar.getHigh(),prevBar.getLow(),prevBar.getClose(),
//        vSAR,vSAR_1,prob10lo,prob40lo);

        // repetition of the above with stronger condition
        if (vSAR>prevBar.getHigh() && vSAR_1>barTS.getReverseIndex(2).getHigh()
							&& prob10lo <= -thrProbDwns	&& prob40lo <= -thrProbDwns
              && vPPO<vPPO_1
              && pm.getCurrentPosition().getQuantity()>=0) {

          target = -mLimit*strategy_direction;
          trade_direction = -1*strategy_direction;
          System.out.println("MA_Strategy.onUpdate: TRIGGER SELL: midA: " +
                  df.format(currData.midA) + " tick: " +
                  jMath.round(currData.sprA,currData.tick_size) + " ask: " +
                  currData.ask);
        } else 
          if (vSAR<prevBar.getLow() && vSAR_1<barTS.getReverseIndex(2).getLow()
							&& prob10up >= thrProbUps && prob40up >= thrProbUps
              && vPPO>vPPO_1
              && pm.getCurrentPosition().getQuantity()<=0) {

          target =  mLimit*strategy_direction;
          trade_direction = 1*strategy_direction;
          System.out.println("MA_Strategy.onUpdate: TRIGGER BUY: midA: " +
                  df.format(currData.midA) + " tick: " +
                  jMath.round(currData.sprA,currData.tick_size) + " bid: " +
                  currData.bid);

        }

        // exist strategy
        boolean exit_trend = false;
        if(pm.getCurrentPosition().getQuantity()*strategy_direction>0
                && vSAR06 > prevBar.getLow()) {
          //exit Long
          exit_trend=true;
          trade_direction = -1*strategy_direction;
          target = 0;
        } else
        if(pm.getCurrentPosition().getQuantity()*strategy_direction<0
                && vSAR06 < prevBar.getHigh()) {
          //exit Short
          exit_trend=true;
          trade_direction = 1*strategy_direction;
          target = 0;
        }

        System.out.println("MA_Strategy.onUpdate: TRADE: #cntr: " + finsec.getContractID() + " dir: " + trade_direction +
                " TrdAllw: " + tradeAllowed(stime) + " canTrd: " + finsec.canTrade());
        lmtPrice = BAPriceLevel.getEstimPriceLevel(
          trade_direction,
          currData.bid, currData.ask,currData.midA, currData.sprA, 1);
        lmtPrice=jMath.round(lmtPrice,currData.tick_size);
        System.out.println("MA_Strategy.onUpdate: TRADE: lmtPrice: " + lmtPrice);
        // normal trade
        if(trade_direction!=0 && tradeAllowed(stime) && finsec.canTrade()) {
          lmtPrice = BAPriceLevel.getEstimPriceLevel(
            trade_direction,
            currData.bid, currData.ask,currData.midA, currData.sprA, 1);
          lmtPrice=jMath.round(lmtPrice,currData.tick_size);
          pm.TRADE(target,lmtPrice,microStrat_LMT);
        }

        // END OF DAY LIQUIDATE
        if(!tradeAllowed(stime) || !finsec.canTrade()) {
          if(pm.getCurrentPosition().getQuantity()!=0) {

            if(pm.getCurrentPosition().getQuantity()>0) {
              //exit Long
              trade_direction = -1;
              target = 0;
            } else
            if(pm.getCurrentPosition().getQuantity()<0) {
              //exit Short
              trade_direction = 1;
              target = 0;
            }

            System.out.println("Liquidate positions");
            lmtPrice = BAPriceLevel.getEstimPriceLevel(
              trade_direction,
              currData.bid, currData.ask,currData.midA, currData.sprA, 1);
            lmtPrice=jMath.round(lmtPrice,currData.tick_size);
            pm.TRADE(target,lmtPrice,microStrat_LMT);

          }
        }

      } // trigger

    } // bar is incremented

    ba_estim.update(currData.bid, currData.ask, currData.bid_sz, currData.ask_sz);
    
    checkOrders();

  } // update
  
  // - FOR TEST PURPOSES
  static public void main(String[] args) {
    
    System.out.println("START IB_Test2.StrategyRunner.main");
    
    String stk_name = "GOOG.STK";
    final FutBarredNeg_Strategy runner = new FutBarredNeg_Strategy(BarSeries.FeedType.RealTime);
    int contractID = InstrumentManager.registerID(stk_name);
    System.out.println("id" + contractID);
    final FinSecurity finsec = InstrumentManager.getFinSecurity(contractID);
    final OrderManager pm = finsec.getOrderManager();
    
    Runnable rr = new Runnable() {
      public void run() {
        int id=1;
        while(true) {
          System.out.println("-----Adder-------- at "  + runner.getTimeElapsed());
          System.out.println("Pos: tobe: " + pm.getTobePosition().getQuantity() +
                  " curr: " + pm.getTotalOutstandingPosition());
          System.out.flush();
          Order order = new Order();
          order.totalQuantity(50000);
          if(Math.random()>0.5) {
            order.action("BUY");
          } else {
            order.action("SELL");
          }
          order.lmtPrice(1.34566);
          order.orderType("LMT");
          order.orderId(id);
          JOrder jord = new JOrder(order);
          pm.addOrder(jord);
          System.out.println("Added " + jord.toString());
          System.out.println("-----Adder END-----");
          System.out.flush();
          id++;
          try{Thread.sleep(5000);} 
          catch(Exception ex){}
        }
      }
    };
    
    Runnable rr2 = new Runnable() {
      public void run() {
        while(true) {
          System.out.println("-----Remover-------- at " + runner.getTimeElapsed());
          int k=pm.getNumOfOrders();
          if(k>0) {
            int k_del = (int)Math.round(Math.random()*k);
            if(k_del>k) k_del=k;
            if(k_del<0) k_del=0;
            pm.removeOrder(k_del);
            System.out.println("removed " + k_del);
          }
          System.out.println(pm.getNumOfOrders());
          System.out.print(pm.getAllOrdersMsg());
          System.out.println("Tot1: " + pm.getTotalOutstandingPosition());
          System.out.println("Tot2: " + pm.getOrdersList().size());
          System.out.println("Tot2: " + pm.getNumOfOrders());
          System.out.println("Rem: " + pm.getRemainedOrdersPosition());
          System.out.println("///////////////////////");
          System.out.flush();
          try{Thread.sleep(3000);} 
          catch(Exception ex){}
        }
      }
    };

    Thread thr1=new Thread(rr,"Adder");
    Thread thr2=new Thread(rr2,"Remove");
    thr1.start();
    thr2.start();
    
  }
  
}
