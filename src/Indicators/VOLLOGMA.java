/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Indicators;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import JNums.*;
import Utils.*;


/**
 *
 * @author nik
 */
public class VOLLOGMA extends Indicator {

  private double mPeriod=10;
  private double mNStdDev=1.8;
  private double wgt = 2d/(1d+mPeriod);
  private double mValue=0d;

  public VOLLOGMA(double nstdev, double period) {
    mPeriod = period;
    mNStdDev = nstdev;
    wgt = 2d/(1d+mPeriod);
  }

  private double volnorm_1=0d;
  private double std_1 = 0d;
  private double ave_1 = 0d;

  public void update(double open, double high, double low, double close, double sz) {

    double val=2*Math.abs(high-low)-Math.abs(open-close);
    double volnorm = 0.0;
    if (Math.abs(val)>0 && sz>0) {
      volnorm=Math.log10((sz)/val);
    } else {
      volnorm= volnorm_1;
    }

//    System.out.println("==" + mValue + " / "+volnorm + " / " + val + " / " + sz);

    double ave, std_x=0.0, std=0.0;

    if (Math.abs(mValue)>0d) {
      ave = wgt*volnorm + (1.0 - wgt) * ave_1;
      std_x = (ave-volnorm)*(ave-volnorm);
      std = Math.sqrt(wgt * std_x + (1.0 - wgt) * std_1*std_1);
    } else {
      ave = volnorm;
    }

//    System.out.println("++" + ave + " / "+ std_x + " / " + std);

    mValue = JNums.Stats.NormInvertedDirection(volnorm,ave,std);

    volnorm_1 = volnorm;
    std_1 = std;
    ave_1 = ave;

  }

  public TSType getTSType() {
    return TSType.VALUE;
  };

  public double getValue() {
    return mValue;
  };
  public static void main(String[] args) {
    int p = 0;

    String format1 = "###0.0000";

    DecimalFormat fm1 = new DecimalFormat(format1,
            new DecimalFormatSymbols(Locale.US));

    VOLLOGMA fc = new VOLLOGMA(1.4d,10);

    long ct = System.currentTimeMillis();

    HistoryLogger hlog = new HistoryLogger();

    String fs_name = "RDSA NA Equity";

    ReaderLink.TradesSimulator tf = new ReaderLink.TradesSimulator();
    int id = InstrumentManager.InstrumentManager.registerID(fs_name);
    InstrumentManager.DataFeed df = InstrumentManager.InstrumentManager.getFinSecurity(id).getDataFeed();
    ReaderLink.FeedReader fr = new ReaderLink.FeedReader("D:\\data\\" + fs_name + ".txt",id,tf);
    long irec=0;
    while(fr.readRecord()>-2 && irec<10000){
//        System.out.println("q: irec: " + irec + " / " + TimeUtils.Time2String(4, df.getLastUpdateTime()) + " / "+ df.bid_sz + " / " +
//                df.bid + " / " + df.ask + " / " + df.ask_sz);System.out.flush();

      fc.update((df.bid+df.ask)/2, df.ask, df.bid, (df.bid+df.ask)/2, df.ask_sz+df.bid_sz);
 
//      System.out.println((df.bid+df.ask)/2 + " / " +  df.ask + "  /" + df.bid + " / " + (df.bid+df.ask)/2
//               + " / " + (double)(df.ask_sz+df.bid_sz) + " / " + fc.getValue());
      hlog.printRecord(101, "", (df.bid+df.ask)/2, df.ask, df.bid, (df.bid+df.ask)/2, (double)(df.ask_sz+df.bid_sz),
              fc.getValue(),0d,0d);

        irec++;

    };
    fr.close();

    System.out.println(System.currentTimeMillis() - ct);

  }

}
