/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package InstrumentManager;

/**
 *
 * @author nik
 */
public class DataFeed extends Object {

  public enum QuoteType {bid, ask, mid, trade, spread};

  public long lastUpdateTimeAsk = 0;
  public long prevUpdateTimeAsk = 0;
  public long lastUpdateTimeBid = 0;
  public long prevUpdateTimeBid = 0;
  public long lastUpdateTimeLast = 0;
  public long prevUpdateTimeLast = 0;
  public long count_priceUpdate = 0;
  public long count_sizeUpdate = 0;

  // current
  public double ask = -1;
  public long ask_sz = -1;
  public double bid = -1;
  public long bid_sz = -1;
  public double last_tm = -1;
  public double last = -1;
  public long last_sz = -1;
  public double trade_dir = 0;

  // previous
  public double prev_ask=-1;
  public long prev_ask_sz=-1;
  public double prev_bid=-1;
  public long prev_bid_sz=-1;
  public double prev_last=-1;
  public long prev_last_sz=-1;

  public double midA=0;
  public double sprA=0;
  public double wgt=0.9;
  public int cnt = 0;
  public double tick_size = 0.01d;

  // change management
  public final static int idx_BID_SZ = 1;
  public final static int idx_BID = 2;
  public final static int idx_ASK = 4;
  public final static int idx_ASK_SZ = 8;
  public final static int idx_LAST = 16;
  public final static int idx_LAST_SZ = 32;

  private int change=0;
  public synchronized int setBits(long bid_sz, double bid, double ask, long ask_sz,
          double last, long last_sz) {
    int change = 0;
    if (bid_sz > 0) {
      change += idx_BID_SZ;
    }
    if (ask_sz > 0) {
      change += idx_ASK_SZ;
    }
    if (last_sz > 0) {
      change += idx_LAST_SZ;
    }
    if (bid > 0) {
      change += idx_BID;
    }
    if (ask > 0) {
      change += idx_ASK;
    }
    if (last > 0) {
      change += idx_LAST;
    }
    return change;
  }

  public synchronized void clearChange() {
    change=0;
  }

  public synchronized int getChange() {
    return change;
  }

  public long getLastUpdateTime() {
    return Math.max(Math.max(lastUpdateTimeAsk, lastUpdateTimeBid),lastUpdateTimeLast);
  }

  @Override
  public String toString() {
    // [bsz,b/a,asz] [lst,lstsz]
    String msg = "[" + bid_sz + "|" + bid + " : " + ask + "|" + ask_sz + "] [" + last + "|" + last_sz + "]";
    return msg;
  }

  public void setLastTime(long time) {
      this.prevUpdateTimeLast = this.lastUpdateTimeLast;
      this.lastUpdateTimeLast = time;
  }
  public void setLast(double val) {
    prev_last=last;
    last=val;
    change+=idx_LAST;
  }
  public void setLastSize(long val) {
    prev_last_sz=last_sz;
    last_sz=val;
    change+=idx_LAST_SZ;
  }
  public void setLast(double val,long time) {
    prev_last=last;
    last=val;
    change+=idx_LAST;
    setLastTime(time);
  }
  public void setLastSize(long val, long time) {
    prev_last_sz=last_sz;
    last_sz=val;
    change+=idx_LAST_SZ;
    setLastTime(time);
  }

  public void setBidTime(long time) {
      this.prevUpdateTimeBid = this.lastUpdateTimeBid;
      this.lastUpdateTimeBid = time;
  }
  public void setBid(double val) {
    prev_bid=bid;
    bid=val;
    change+=idx_BID;
  }
  public void setBidSz(long val) {
    prev_bid_sz=bid_sz;
    bid_sz=val;
    change+=idx_BID_SZ;
  }
  public void setBid(double val,long time) {
    prev_bid=bid;
    bid=val;
    change+=idx_BID;
    setBidTime(time);
  }
  public void setBidSz(long val,long time) {
    prev_bid_sz=bid_sz;
    bid_sz=val;
    change+=idx_BID_SZ;
    setBidTime(time);
  }

  public void setAskTime(long time) {
      this.prevUpdateTimeAsk = this.lastUpdateTimeAsk;
      this.lastUpdateTimeAsk = time;
  }
  public void setAsk(double val) {
    prev_ask=ask;
    ask=val;
    change+=idx_ASK;
  }
  public void setAskSz(long val) {
    prev_ask_sz=ask_sz;
    ask_sz=val;
    change+=idx_ASK_SZ;
  }
  public void setAsk(double val,long time) {
    prev_ask=ask;
    ask=val;
    change+=idx_ASK;
    setAskTime(time);
  }
  public void setAskSz(long val,long time) {
    prev_ask_sz=ask_sz;
    ask_sz=val;
    change+=idx_ASK_SZ;
    setAskTime(time);
  }

  public boolean changeBid() {
    return bid>0 && prev_bid!=bid;
  }
  public boolean changeAsk() {
    return ask>0 && prev_ask!=ask;
  }
  public boolean changeBidAsk() {
    return bid>0 && ask>0 && (prev_bid!=bid || prev_ask!=ask);
  }

  public boolean changeTrade() {
    return last>0 && prev_last!=last;
  }

  public void updateSprMid() {
      if(changeBidAsk()) {
      if(cnt==0) {
        sprA=(ask-bid);
        midA=(ask+bid)*0.5d;
      } else {
        sprA=sprA*wgt+(ask-bid)*(1-wgt);
        midA=midA*wgt+(ask+bid)*(1-wgt)*0.5d;
      }
      cnt++;
    }
  }

  public FinSecurity getFinSecurity() {
    return mFinSecurity;
  }

  FinSecurity mFinSecurity = null;
  public DataFeed(FinSecurity finsec) {
    ask=-1;
    bid=-1;
    ask_sz=-1;
    bid_sz=-1;
    prev_ask=-1;
    prev_bid=-1;
    midA=0;
    sprA=0;
    wgt=0.9;
    cnt = 0;
    mFinSecurity = finsec;
  }

}
