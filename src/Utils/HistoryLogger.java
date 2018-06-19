/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.text.*;
import java.io.*;


/**
 *
 * @author nik
 */
public class HistoryLogger {
// recID / recNAME / Day / Time / bid_IB / ask_IB / signal / prel / psig / fut_filt / corr / tcorr 
  static SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy HHmmss.SSS");
  static SimpleDateFormat df_date = new SimpleDateFormat("ddMMyyyy");
  static NumberRenderer nf = new NumberRenderer(6);
  static NumberRenderer nf0 = new NumberRenderer(0);
  static NumberRenderer nf2 = new NumberRenderer(2);
  PrintWriter mLogOut = null;
  String mName = "";

  PrintWriter openFile(String fname) {
    PrintWriter a = null;
    try {
      a = FileManager.getFileManager().getWFileAppended(fname);
      mName = fname;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return a;
  }
  
  @SuppressWarnings("static-access")
  public void close() {
    try {
      FileManager.getFileManager().close(mName);
    } catch(Exception ex) {
      System.out.println("HistoryLogger: Close Problem");
      System.out.println(ex);
    }
  }

  public void printRecord(int recId, String Name,
          double val1,double val2,double val3,double val4,
          double val5,double val6,double val7,double val8) {
    if(mLogOut==null) {
      mLogOut = openFile("tool_hist" + 
              df_date.format(System.currentTimeMillis()) + ".dat");
    }
    String str = recId + " " + Name;
    str = str + " " + df.format(System.currentTimeMillis());
    str = str + " " + nf.format(val1) + " " + nf.format(val2)
              + " " + nf.format(val3) + " " + nf.format(val4)
              + " " + nf.format(val5) + " " + nf.format(val6)
              + " " + nf.format(val7) + " " + nf.format(val8);
//    System.out.println(str);
    mLogOut.println(str);
    mLogOut.flush();
  }
  
  public void printRecord(int recId, String Name,long ctime,
          double val1,double val2,double val3,double val4,
          double val5,double val6,double val7,double val8) {
    if(mLogOut==null) {
      String fdir = Configurer.AtXMLReader.getTickDataDir() + "\\";
      mLogOut = openFile(fdir + "tool_hist" +
              df_date.format(System.currentTimeMillis()) + ".dat");
    }
    String str = recId + " " + Name;
    str = str + " " + df.format(ctime);
    str = str + " " + nf.format(val1) + " " + nf.format(val2)
              + " " + nf.format(val3) + " " + nf.format(val4)
              + " " + nf.format(val5) + " " + nf.format(val6)
              + " " + nf.format(val7) + " " + nf.format(val8);
//    System.out.println(str);
    mLogOut.println(str);
    mLogOut.flush();
  }
  public void printRecord(int recId, int valI,long ctime,
          double val1,double val2,double val3,double val4,
          double val5,double val6,double val7,double val8) {
    if(mLogOut==null) {
      String fdir = Configurer.AtXMLReader.getTickDataDir() + "\\";
      mLogOut = openFile(fdir + "tool_hist" +
              df_date.format(System.currentTimeMillis()) + ".dat");
    }
    String str = recId + " " + valI;
    str = str + " " + df.format(ctime);
    str = str + " " + nf.format(val1) + " " + nf.format(val2)
              + " " + nf.format(val3) + " " + nf.format(val4)
              + " " + nf.format(val5) + " " + nf.format(val6)
              + " " + nf.format(val7) + " " + nf.format(val8);
//    System.out.println(str);
    mLogOut.println(str);
    mLogOut.flush();
  }

  public void printFeed(String Name,long ctime,
          double val1,double val2, double val3, int msg) {
    if(mLogOut==null) {
      String fdir = Configurer.AtXMLReader.getTickDataDir() + "\\";
      mLogOut = openFile(fdir + Name + "_" +
              df_date.format(System.currentTimeMillis()) + ".dat");
    }

//    String str = msg + " ";
    String str = df.format(ctime);
    str = str
            + " " + nf0.format(val1)
            + " " + nf.format(val2)
            + " " + nf0.format(val3);
//    System.out.println(str);
    mLogOut.println(str);
    mLogOut.flush();
  }

  
  static public void main(String[] args) {
//    System.out.println(HistoryLogger.df.format(System.currentTimeMillis()));
//    HistoryLogger hlog = new HistoryLogger();
//    hlog.printRecord(20,"TEST", 1.1234567, 2.45556777, 3, 4, 5, 6, 7, 8);
//    try {hlog.close();} catch(Exception ex) {};
    System.out.println(nf0.format(1.23445));
    System.out.println(nf2.format(1.23445));
  }
}
