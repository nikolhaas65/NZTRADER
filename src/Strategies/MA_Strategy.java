/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Strategies;

import OrderExecutor.BA_Simple_Follower;
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
public class MA_Strategy extends MacroStrategy {

  public MA_Strategy() {
    mStartupTime = System.currentTimeMillis();
  }

  public MA_Strategy(BarSeries.FeedType feedtype) {
    mStartupTime = System.currentTimeMillis();
    setFeedType(feedtype);
  }

//  Definition of Bars and Indicators
  BarSeries barTS = null;

  public void Init() {

//    setTradingTime( TimeUtils.String2Time(6,"090500"),TimeUtils.String2Time(6,"172500"));
//    setDataFeedTime(TimeUtils.String2Time(6,"090000"),TimeUtils.String2Time(6,"173000"));
    setTradingTime( TimeUtils.String2Time(6,"090500"),TimeUtils.String2Time(6,"224500"));
    setDataFeedTime(TimeUtils.String2Time(6,"090000"),TimeUtils.String2Time(6,"224500"));

    int buffSZ=50;
    long vtime = Utils.TimeUtils.String2Time(6, "000500");
    barTS = new BarSeries(mSecName,BarSeries.FeedType.RealTime,vtime,5,BarSeries.PeriodType.minutes,buffSZ);
    TSManager.setTS(barTS,mFinSec);
    LinkedOHLCDataset ohlc_ds =barTS.getLinkedOHLCDataset(buffSZ);
    TSManager.setLinkedOHLC(barTS, ohlc_ds);

  }

  private void updateIndicators(BarOHLC bar) {

  }

  // fab = 0 
  double fAB = 0.2;  
  double thr = 0.2;

  BA_Simple_Follower ba_estim = new BA_Simple_Follower(10);
  MA_FollowBidAsk microStrat_LMT = new MA_FollowBidAsk("LMT",ba_estim);
  NumberRenderer df = new NumberRenderer(3,3);
  
  public synchronized void onUpdate() {

// overhead
    FinSecurity finsec = InstrumentManager.getFinSecurity(mContractID);

    DataFeed currData = finsec.getDataFeed();
    if (currData.cnt<10) return;
    long stime = TimeUtils.getDayTimeL(currData.getLastUpdateTime());
    if (!algoCalcAllowed(stime)) return;

    barTS.update(System.currentTimeMillis(), currData.last, currData.last_sz);

    if(barTS.isIncremented()) {
      updateIndicators(barTS.getOneButLast());
    }

    // TRIGGER
    // look if mid prediction 20%-corridor gets outside bid-ask corridor:
    //      Mid + fAB*Ave_Spread gets above ASK 
    //  or  Mid - fAB*Ave_Spread gets below BID

    if (currData.midA + fAB * currData.sprA > currData.ask ||
            currData.midA - fAB * currData.sprA < currData.bid) {
      int direction = 0;

      OrderManager pm = finsec.getOrderManager();
      
      System.out.println("MA_Strategy.onUpdate: Prepair order");
      System.out.println("MA_Strategy.onUpdate: Pos: tobe: " + pm.getTobePosition().getQuantity() +
              " curr: " + pm.getCurrentPosition().getQuantity());
      System.out.flush();

      double lmtPrice = 0;
      double target = 0;
      // repetition of the above with stronger condition
      if (currData.midA + currData.sprA * thr > currData.ask
              && pm.getCurrentPosition().getQuantity()>=0) {
        target = -mLimit;
        System.out.println("MA_Strategy.onUpdate: TRIGGER SELL: midA: " +
                df.format(currData.midA) + " tick: " +
                jMath.round(currData.sprA,currData.tick_size) + " ask: " +
                currData.ask);
      } else if (currData.midA - currData.sprA * thr < currData.bid
              && pm.getCurrentPosition().getQuantity()<=0) {
        target =  mLimit;
        System.out.println("MA_Strategy.onUpdate: TRIGGER BUY: midA: " +
                df.format(currData.midA) + " tick: " +
                jMath.round(currData.sprA,currData.tick_size) + " bid: " +
                currData.bid);
      }

      if(Math.abs(target)>0 && tradeAllowed(stime)) {
        lmtPrice = BAPriceLevel.getEstimPriceLevel(
          Math.signum(target),
          currData.bid, currData.ask,currData.midA, currData.sprA, 1);
        lmtPrice=jMath.round(lmtPrice,currData.tick_size);
        pm.TRADE(target,lmtPrice,microStrat_LMT);
      }
      
    } // trigger

    ba_estim.update(currData.bid, currData.ask, currData.bid_sz, currData.ask_sz);
    
    checkOrders();

  } // update
  
  // - FOR TEST PURPOSES
  static public void main(String[] args) {
    
    System.out.println("START IB_Test2.StrategyRunner.main");
    
    String stk_name = "GOOG.STK";
    final MA_Strategy runner = new MA_Strategy(BarSeries.FeedType.RealTime);
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
