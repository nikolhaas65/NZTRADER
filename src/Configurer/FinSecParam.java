/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configurer;

/**
 *
 * @author nik
 */
public class FinSecParam extends Object {
  String mName="";
  String mType = "";
  String mSymbol = "";
  String mCurrency = "";
  String mExchange = "";
  String mStrikes = "";
  String mExpiry = "";
  double mTick = 0d;
  String mFeed = "NONE";
  int mLimit = 0;
  String mInterval = "5 min";
  int mMultiplier = 1;
  String mArbitrage = "";

  public void setName(String name) {mName=name;}
  public void setType(String type) {mType=type;}
  public void setSymbol(String symb) {mSymbol=symb;}
  public void setCurrency(String currency) {mCurrency=currency;}
  public void setExchange(String exch) {mExchange=exch;}
  public void setStrikes(String exch) {mStrikes=exch;}
  public void setExpiry(String expiry) {mExpiry=expiry;}
  public void setFeed(String feed) {mFeed=feed;}
  public void setTick(String tick) {mTick=Double.parseDouble(tick);}
  public void setLimit(String limit) {mLimit=Integer.parseInt(limit);}
  public void setInterval(String interval) {mInterval=interval;}
  public void setMultiplier(String multiplier) {mMultiplier=Integer.parseInt(multiplier);}
  public void setArbitrage(String arb) {mArbitrage=arb;}

  public String getName() {return mName;}
  public String getType() {return mType;}
  public String getSymbol() {return mSymbol;}
  public String getCurrency() {return mCurrency;}
  public String getExchange() {return mExchange;}
  public String getStrikes() {return mStrikes;}
  public String getExpiry() {return mExpiry;}
  public String getFeed() {return mFeed;}
  public double getTick() {return mTick;}
  public int getLimit() {return mLimit;}
  public String getInterval() {return mInterval;}
  public int getMultiplier() {return mMultiplier;}
  public String getArbitrage() {return mArbitrage;}

  public String toString() {
    String msg="Name: " + mName + " : " + mFeed + " : " + mLimit + " : " + mType + " : " + mSymbol +
            " : " + mCurrency + " : " + mExchange + " : " + mInterval + " : " + mMultiplier +
            " : " + mTick;
    if(mType.equalsIgnoreCase("FUT")) {
      msg = msg + " : " + mExpiry;
    }
    if(mType.equalsIgnoreCase("OPT")) {
      msg = msg + " : " + mExpiry;
      msg = msg + " : " + mStrikes;
    }
    return msg;
  }

}
