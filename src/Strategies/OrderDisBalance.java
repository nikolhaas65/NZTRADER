/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Strategies;

import Arbitrage.Arbitrage;
import OrderExecutor.BA_Simple_Follower;
import java.util.*;

import com.ib.client.*;

import InstrumentManager.*;
import OrderManager.OrderManager;
import OrderManager.JOrder;
import OrderManager.Position;
import JNums.*;
import OrderExecutor.*;
import Utils.*;
import TimeSeries.*;
import Graphs.*;
import Indicators.*;
import AlgoTrader.*;


/**
 *
 * @author nik
 */
public class OrderDisBalance extends MacroStrategy {

  int mDirection = 1;
  int mIncrement = 1;
  
  public OrderDisBalance() {
    mStartupTime = System.currentTimeMillis();
  }

  public OrderDisBalance(int dir) {
    mStartupTime = System.currentTimeMillis();
    mDirection = (int)Math.signum(dir);
  }

  public OrderDisBalance(BarSeries.FeedType feedtype) {
    mStartupTime = System.currentTimeMillis();
    setFeedType(feedtype);
  }

//  Definition of Bars and Indicators
  BarSeries barTS = null;
  EWMA tradeEWMA = null;
  EWMA pnlEWMA = null;

  public void Init() {
    Dispatcher.mGUI.setDebuggerText("INIT");
    setTradingTime( TimeUtils.String2Time(6,"080500"),TimeUtils.String2Time(6,"172500"));
    setDataFeedTime(TimeUtils.String2Time(6,"080000"),TimeUtils.String2Time(6,"173000"));
//    setTradingTime( TimeUtils.String2Time(6,"000100"),TimeUtils.String2Time(6,"235900"));
//    setDataFeedTime(TimeUtils.String2Time(6,"000100"),TimeUtils.String2Time(6,"235900"));

    int buffSZ=50;
    long vtime = Utils.TimeUtils.String2Time(6, "000500");
    barTS = new BarSeries(mSecName,BarSeries.FeedType.RealTime,vtime,5,BarSeries.PeriodType.minutes,buffSZ);
    TSManager.setTS(barTS,mFinSec);
    LinkedOHLCDataset ohlc_ds =barTS.getLinkedOHLCDataset(buffSZ);
    TSManager.setLinkedOHLC(barTS, ohlc_ds);

    tradeEWMA = new EWMA(10);
    pnlEWMA = new EWMA(10);

  }

  private void updateIndicators(BarOHLC bar) {

  }

  // fab = 0 
  double fAB = 2.;
  double thr = fAB;
  double mPNL=0;

//  BA_Simple_Follower ba_estim = new BA_Simple_Follower(10);
  BA_Follower ba_estim = new BA_Follower(5);
  MA_FollowBidAsk microStrat_LMT = new MA_FollowBidAsk("LMT",ba_estim);
  NumberRenderer df = new NumberRenderer(3,3);

//  called on every tick
  public synchronized void onUpdate() {

// overhead
    FinSecurity finsec = InstrumentManager.getFinSecurity(mContractID);
    DataFeed currData = finsec.getDataFeed();
    if (currData.cnt<10) return;
    if (!currData.changeTrade()) {
      tradeEWMA.update(currData.trade_dir);
      return;
    }

    long stime = TimeUtils.getDayTimeL(currData.getLastUpdateTime());

    if (!algoCalcAllowed(stime) || !finsec.canTrade()) return;

    barTS.update(System.currentTimeMillis(), currData.last, currData.last_sz);

    if(barTS.isIncremented()) {
      updateIndicators(barTS.getOneButLast());
    }

    ba_estim.update(currData.bid, currData.ask, currData.bid_sz, currData.ask_sz);

    // TRIGGER
    // look if mid prediction 20%-corridor gets outside bid-ask corridor:
    //      Mid + fAB*Ave_Spread gets above ASK 
    //  or  Mid - fAB*Ave_Spread gets below BID

//    if (currData.midA + fAB * currData.sprA > currData.ask ||
//            currData.midA - fAB * currData.sprA < currData.bid) {

      OrderManager om = finsec.getOrderManager();
      
      System.out.println("MA_Strategy.onUpdate: Prepair order");
      System.out.println("MA_Strategy.onUpdate: Pos:" +
              " tobe: " + om.getTobePosition().getQuantity() +
              " curr: " + om.getCurrentPosition().getQuantity());
      System.out.flush();

      double lmtPrice = 0;
//      int target = 0;
      double target = 0;
      // long / short switch
//      if (pm.getCurrentPosition().getQuantity()>=0) {
//      int ArbPos = Arbitrage.getTotalPosition("EURUSD");

      double ArbPos = Arbitrage.getTotalPosition("AEX_NOV");
      double curr_quantity = om.getCurrentPosition().getQuantity();
      System.out.println("Before Increment Quantity: " + curr_quantity +
              " + " + mLimit*mDirection + " ArbPos: " + ArbPos);
      if(Math.abs(curr_quantity-mLimit*mDirection)<1e-8 && ArbPos*mDirection<1) {
        System.out.println("Increment Quantity: " + om.getCurrentPosition().getQuantity() +
                " + " + mLimit*mDirection + " ArbPos: " + ArbPos);
        mLimit=Math.abs(om.getCurrentPosition().getQuantity())+(double)1.0;
      }
        target = mLimit*mDirection;
        System.out.println("MA_Strategy.onUpdate: TRIGGER SELL: target: " +
                target + " tick: " +
                jMath.round(currData.sprA,currData.tick_size) + " ask: " +
                currData.ask);
//      } else if (pm.getCurrentPosition().getQuantity()<=0) {
//        target =  mLimit;
//        System.out.println("MA_Strategy.onUpdate: TRIGGER BUY: midA: " +
//                df.format(currData.midA) + " tick: " +
//                jMath.round(currData.sprA,currData.tick_size) + " bid: " +
//                currData.bid);
//      }

      if(Math.abs(target)>0 && tradeAllowed(stime)) {

//        lmtPrice = BAPriceLevel.getEstimPriceLevel(
//          Math.signum(target),
//          currData.bid, currData.ask,currData.midA, currData.sprA, 1);
        lmtPrice = BAPriceLevel.getPriceLevel(
          Math.signum(target),currData.bid, currData.ask,1);
        lmtPrice = target>0?
          ba_estim.getBidAsk(BA_Indicator.BidAsk.bid) :
          target<0? ba_estim.getBidAsk(BA_Indicator.BidAsk.ask) : 0;

        lmtPrice=jMath.round(lmtPrice,currData.tick_size);

        om.TRADE_INCREMENTAL(target,lmtPrice,microStrat_LMT);
      }

//    } // trigger

    Position cpos = Arbitrage.getTotalPNL("AEX_NOV",currData.bid,currData.ask);
    double pnl = cpos.price*cpos.getQuantity()*finsec.getMultiplier();
    double dir_pos = cpos.getQuantity();

    System.out.println("POS: " + dir_pos + " PNL: " + pnl ); System.out.flush();
    ba_estim.setDirectionAndPnL(dir_pos,pnl);

    checkOrders();

  } // update

  // - FOR TEST PURPOSES
  static public void main(String[] args) {
    
    System.out.println("START IB_Test2.StrategyRunner.main");
    
    String stk_name = "GOOG.STK";
    final OrderDisBalance runner = new OrderDisBalance(BarSeries.FeedType.RealTime);
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
