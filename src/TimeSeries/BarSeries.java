/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimeSeries;

import java.util.*;

import JNums.*;
import Utils.*;
import Graphs.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * @author nik
 */
public class BarSeries {

  public static enum PeriodType {
    seconds, minutes, hours, days
  }

  public static final long time_second = 1000;
  public static final long time_minute = 60*time_second;
  public static final long time_hour = 60*time_minute;
  public static final long time_day = 24*time_hour;

  public static long getPeriodTimeLength(int periods,PeriodType type) {
    long time=time_second;
    switch(type) {
      case seconds: time=periods*time_second; break;
      case minutes: time=periods*time_minute; break;
      case hours: time=periods*time_hour; break;
      case days: time=periods*time_day; break;
    }
    return time;
  }

  public long getPeriodTimeMilliseconds() {
    return mTimePeriodMilliseconds;
  }

  public static String getPeriodString(int periods, PeriodType type) {
    String time=periods+"";
    switch(type) {
      case seconds: time+="s"; break;
      case minutes: time+="m"; break;
      case hours: time+="h"; break;
      case days: time+="d"; break;
    }
    return time;
  }

  public enum FeedType {RealTime,FeedReader};

  private LatestIntervals<BarOHLC> mList=null;
  private int mTimePeriod = 5; // default = 5 min bar
  private PeriodType mPeriodType = PeriodType.minutes;
  private long mTimePeriodMilliseconds = getPeriodTimeLength(mTimePeriod,mPeriodType); // default = 5 min bar

  private long mStart = 0;
  private int mMaxSize = 0;
  private long currTime = 0;
  private boolean incremented=false;
  private String mSecName = "";
  private String mBarSeriesName = "";
  private String mBarLoggerName = "";
  BarLogger mBarLogger = null;
  private boolean logIt = false;

  public String getSeriesName() {
    return mBarSeriesName;
  }

  public void LogIt(boolean flag) {
      logIt = flag;
      if(logIt) mBarLogger = new BarLogger(mBarLoggerName,mBarSeriesName);
  }

  // it is a in-memory buffer
//  reader: flag which tells if the data are real-time (=false) or taken from file (=true)
//  bar_start : time when BarSeries started. Start time of first bar
//  time_period : number of time periods
//  type : PeriodType type = seconds/minutes/hourse/days
//  max_length : max length of the buffer. ie. maximum bars visible currently in series
  public BarSeries(String Name, FeedType reader, long bar_start, int time_period, PeriodType type, int max_length) {

    mTimePeriodMilliseconds = getPeriodTimeLength(time_period,type);
    mTimePeriod = time_period;
    mStart = bar_start;
    mPeriodType = type;

    mList = new LatestIntervals<BarOHLC>(max_length);
    mMaxSize = max_length;

    long bar_start_time=0;
    if(reader == FeedType.RealTime)
        bar_start_time = current_past_nearest_barTime(System.currentTimeMillis(),this.mStart,this.mTimePeriodMilliseconds);

    currTime = bar_start_time;
    incremented=false;
    mBarSeriesName = getPeriodString(time_period, type);
    mBarLoggerName = Name;
    mSecName = Name;
    BarOHLC bar = new BarOHLC(bar_start_time,-1, 0);
    mList.put(bar);



  }

  public synchronized LinkedOHLCDataset getLinkedOHLCDataset() {
    // to be used for visualization
    LinkedOHLCDataset ts = new LinkedOHLCDataset(this.mBarSeriesName,this.mMaxSize);
    Iterator<BarOHLC> iter = mList.getList().iterator();
    while(iter.hasNext()) {
      BarOHLC bar = iter.next();
      if(bar.getOpen()>0 && bar.getHigh()>0 && bar.getLow()>0) {
      ts.addPoint(new Date(bar.getTime()),
              bar.getOpen(),bar.getHigh(),bar.getLow(),
              bar.getClose(),bar.getVolume());
      }
    }
    return ts;
  }

  public synchronized LinkedOHLCDataset getLinkedOHLCDataset(int size) {
    // to be used for visualization
    LinkedOHLCDataset ts = new LinkedOHLCDataset(this.mBarSeriesName,size);
    Iterator<BarOHLC> iter = mList.getList().iterator();
    while(iter.hasNext()) {
      BarOHLC bar = iter.next();
      if(bar.getOpen()>0 && bar.getHigh()>0 && bar.getLow()>0) {
      ts.addPoint(new Date(bar.getTime()),
              bar.getOpen(),bar.getHigh(),bar.getLow(),
              bar.getClose(),bar.getVolume());
      }
    }
    return ts;
  }

  private long current_past_nearest_barTime(long currentTime, long start,long period) {
    return (long)Math.floor((currentTime-start)/period)*period + start;
  }

  public boolean isIncremented() {
    return incremented;
  }

  public BarOHLC get(int index) {
    return mList.getList().get(index);
  }
  public BarOHLC getReverseIndex(int index) {
    int ind = Math.max(mList.getList().size()-1-index,0);
    return mList.getList().get(ind);
  }

  public BarOHLC getLast() {
    return mList.getList().get(mList.getList().size()-1);
  }
  public BarOHLC getOneButLast() {
    return mList.getList().get(mList.getList().size()-2);
  }

  public synchronized void addNew(long currentTime, double price, long sz) {
    // floor((T-start)/PeriodType)*PeriodType
    long bar_start = Math.round(
            Math.floor(
              (currentTime-this.mStart)/this.mTimePeriodMilliseconds))*
              this.mTimePeriodMilliseconds+this.mStart;
    BarOHLC bar = new BarOHLC(bar_start,price, sz);
    mList.put(bar);
    this.currTime = currentTime;
    this.incremented=true;
  }

  private void storeRecord(BarOHLC bar) {
    if(bar.getTime()>0)
    mBarLogger.printRecord(mSecName, bar.getTime(),
            bar.getOpen(), bar.getHigh(), bar.getLow(),
            bar.getClose(), bar.getVolume());
  }

  private void readRecords(int nDaysBack) {
    readRecords(mTimePeriod, mPeriodType,nDaysBack);
  }

  private void readRecords(int time_period, PeriodType type, int nDaysBack) {
    long now = System.currentTimeMillis();
    for(int d=nDaysBack;d>=0;d--) {
      if(BarLogger.checkBarDataFile(now-d*time_day, this.mSecName,getPeriodString(time_period,type))) {
        String fileName = BarLogger.getBarFileName(now - d*time_day ,this.mSecName,getPeriodString(time_period,type));
        // TO ADD: reding from file
      }
    }
  }

  public void end_update() {
    if(logIt) this.storeRecord(get(mList.getList().size()-1));
  }

  public void end_update(int close) {
    if(logIt) {
      end_update();
      mBarLogger.close();
    }
  }

  public synchronized int update(long currentTime, double price, long sz) {
    //System.out.println("Val: " + price + " : "+ sz + " : "+ (mList.getList().size()-1) );
    //System.out.flush();
    long ct = currentTime-this.currTime;
    this.incremented=false;
//    System.out.println("ct: " + ct + " currT: " + this.currTime + " ("+
//            Utils.TimeUtils.Time2String(1, this.currTime)+ ") [" + price + "," + sz + "]");
    if(ct>0 && ct<this.mTimePeriodMilliseconds) {
//        update BAR
      if(price>0 && sz>0) this.get(mList.getList().size()-1).update(price, sz);
//      System.out.println("--->>> update "+ ct + " " + this.mTimePeriod);System.out.flush();
    } else if(ct>=this.mTimePeriodMilliseconds) {
//        store the previous Bar add new BAR
      if(logIt) storeRecord(get(mList.getList().size()-1));
      this.addNew(currentTime, price, sz);
      this.currTime = current_past_nearest_barTime(currentTime, this.mStart,this.mTimePeriodMilliseconds);

      final BarOHLC bar = this.getOneButLast();
//      System.out.println("Before update:name: " + mSecName);
      TSManager.updateAll(this);

//      System.out.println("--->>> new "+ ct + " " + TimeUtils.Time2String(1,this.currTime));System.out.flush();
    } else {
//      System.out.println("--->>> another " + ct + " " + this.mTimePeriod);System.out.flush();
    }
    return mList.getList().size();
  }

  public static void main(String[] args) {

    long iters=20;
    TSeriesRandomGenerator tsgen = new TSeriesRandomGenerator();

    long vtime = Utils.TimeUtils.String2Time(3, "000500");

//    BarSeries ts_bars = new BarSeries("XYZ",System.currentTimeMillis(),1,PeriodType.seconds,15);
    BarSeries ts_bars = new BarSeries("XYZ",FeedType.FeedReader,vtime,5,PeriodType.seconds,15);
    ts_bars.LogIt(true);
    System.out.println("Init sz:" + ts_bars.mList.getList().size());

    for(int it=0;it<iters;it++) {
        double val = tsgen.getNext();
        int idx = ts_bars.update(System.currentTimeMillis(),val, 100);
        System.out.println("it: " + it + " tssz: " + idx + " " +
                Utils.TimeUtils.Time2String(2,System.currentTimeMillis()));System.out.flush();
        System.out.println(ts_bars.getLast().toString() + "\n");
        Utilities.wait_me(Math.round(Math.random()*900+100));
    }
    ts_bars.end_update(0);

    java.util.Iterator<BarOHLC> iter = ts_bars.mList.getList().iterator();
    while(iter.hasNext()) {
      System.out.println(iter.next().toString());
    }

  }

}
