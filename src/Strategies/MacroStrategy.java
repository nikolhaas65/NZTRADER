/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Strategies;

import OrderManager.GlobalOrderRegister;
import OrderManager.*;
import InstrumentManager.*;
import java.util.*;
import com.ib.client.*;
import IBLink.*;
import JNums.*;
import OrderExecutor.*;
import TimeSeries.*;
import Utils.*;

/**
 *
 * @author nik
 */
public abstract class MacroStrategy {
  
  public abstract void Init();
  public abstract void onUpdate();

  public boolean notInited=true;

  private long startTradeTime = 0;
  private long endTradeTime = 0;
  private long startDataFeedTime = 0;
  private long endDataFeedTime = 0;
  long mStartupTime = 0;

  BarSeries mTimeSeries = null;
  String mSecName = "";

  int mContractID = 0;
  FinSecurity mFinSec = null;
//  int mLimit = 0;
  double mLimit = 0;

  public void setSecName(String name) {
    mSecName=name;
  }

  public void setTradingTime(long start, long end) {
    startTradeTime = start;
    endTradeTime = end;
//    System.out.println("setTradingTime: " + start + " : " + end);
  }

  public void setDataFeedTime(long start, long end) {
    startDataFeedTime = start;
    endDataFeedTime = end;
  }

  public boolean algoCalcAllowed(long stime) {
//    long stime = TimeUtils.getDayTimeL(time);
    return stime>=startDataFeedTime && stime<=endDataFeedTime;
  }

  public boolean tradeAllowed(long stime) {
    return stime>=startTradeTime && stime<=endTradeTime;
  }

  public long getStartTradeTime() {
    return startTradeTime;
  }
  public long getEndTradeTime() {
    return endTradeTime;
  }

  public double getTimeElapsed() {
    // for test purposes
    return (double) ((System.currentTimeMillis() - mStartupTime));
  }

  public void setContractID(int secid) {
    mContractID = secid;
  }

  public void setFinSecurity(FinSecurity finsec) {
    mFinSec = finsec;
  }

  public void setLimit(int limt) {
    mLimit = limt;
  }

  BarSeries.FeedType mFeedType = BarSeries.FeedType.FeedReader;
  public void setFeedType(BarSeries.FeedType feedtype) {
    mFeedType = feedtype;
  }


  // shared stuff
  public synchronized void checkOrders() {
    Iterator<Integer> iterAllOrders = GlobalOrderRegister.getAllOrderIDs().iterator();
    while (iterAllOrders.hasNext()) {
      int orderID = iterAllOrders.next();
      FinSecurity finsec = GlobalOrderRegister.getFinSecurity(orderID);
      OrderManager pm = finsec.getOrderManager();
      JOrder jorder = pm.getJOrder(orderID);
      MicroStrategy ms = GlobalOrderRegister.getMicroStrategy(jorder.mOrderId);
      if (jorder != null) {
        ms.checkOrder(finsec,pm,jorder);
      }
    }
  }
  
}
