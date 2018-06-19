/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package InstrumentManager;

import java.util.*;

import OrderManager.*;
import Strategies.MacroStrategy;
import Utils.HistoryLogger;

/**
 *
 * @author nik
 */
public class FinSecurity extends Object {

  private String mName = "";
  private int mContractID = 0;
  private boolean mTradeActive = false;
  private int mTableIndex = -1;
  private int mMultiplier = 1;
  private boolean mOption = false;

  private double mStrike = -1;
  private double mCPflag = 0;

  private HistoryLogger hlog = null;

  public void setTableIndex(int ind) {
    mTableIndex = ind;
  }
  public int getTableIndex() {
    return mTableIndex;
  }
  public boolean isOption() {
    return mOption;
  }

  public double getStrike() {
    return isOption() ? mStrike : -1;
  }
  public double getCPflag() {
    return isOption() ? mCPflag : 0;
  }

  private List<MacroStrategy> mStrategies=null;
  private synchronized List<MacroStrategy> getStrategiesList() {
      if (mStrategies == null) {
        mStrategies = new ArrayList<MacroStrategy>();
      }
      return mStrategies;
  }  // Order Map and List

  public void setStrategy(MacroStrategy runner) {
    getStrategiesList().add(runner);
    mTradeActive=false;
  }

  public void setMultiplier(int mult) {
    mMultiplier = mult;
  }
  public int getMultiplier() {
    return mMultiplier;
  }

  public void ActivateTrading() {
    mTradeActive = true;
    System.out.println(this.mName + " trade Activated");
  }
  public void DisActivateTrading() {
    mTradeActive = false;
    System.out.println(this.mName + " trade DisActivated");
  }
  public boolean canTrade() {
    return mTradeActive;
  }

  public void FireStrategies() {
    Iterator<MacroStrategy> iter = getStrategiesList().iterator();
    while(iter.hasNext()) {
      iter.next().onUpdate();
    }
  }

  public String ListStrategies() {
    String msg = "";
    Iterator<MacroStrategy> iter = getStrategiesList().iterator();
    while(iter.hasNext()) {
      msg=msg+" "+iter.next().toString();
    }
    return msg;
  }
  
  public FinSecurity(String name, int id) {
    mName = name;
    mContractID = id;
    mFeed = new DataFeed(this);
    mPM = new OrderManager(this);
    hlog = new HistoryLogger();
    if(name.contains("OPT") && !name.contains("CHAIN")) {
      mOption=true;
      System.out.println("FS: OPT: "+name);
      Vector v = Utils.Utilities.splitRecord(name, " ");
      mStrike = Double.parseDouble(v.get(4).toString());
      mCPflag = v.get(3).toString().compareToIgnoreCase("C")==0 ? 1.0 :
        v.get(3).toString().compareToIgnoreCase("P")==0 ? -1.0 : 0.0 ;
    }
  }

  public String getName() {
    return mName;
  }
  
  public int getContractID() {
    return mContractID;
  }

  public void recordAsk(int fld) {
//    if(fld==0 || fld==3 || fld==5)
    if(this.mFeed.ask>0 && this.mFeed.ask_sz>0)
      hlog.printFeed(this.mName, this.mFeed.getLastUpdateTime(),  1, this.mFeed.ask, this.mFeed.ask_sz,fld);
  }
  public void recordBid(int fld) {
//    if(fld==0 || fld==3 || fld==5)
    if(this.mFeed.bid>0 && this.mFeed.bid_sz>0)
    hlog.printFeed(this.mName, this.mFeed.getLastUpdateTime(), -1, this.mFeed.bid, this.mFeed.bid_sz,fld);
  }
  public void recordLast(int fld) {
    if(this.mFeed.last>0 && this.mFeed.last_sz>0)
    hlog.printFeed(this.mName, this.mFeed.getLastUpdateTime(), 0, this.mFeed.last, this.mFeed.last_sz,fld);
  }

  private DataFeed mFeed = null;
  public synchronized DataFeed getDataFeed() {
    return mFeed;
  }
  
  private OrderManager mPM = null;
  public synchronized OrderManager getOrderManager() {
    return mPM;
  }
  
}
