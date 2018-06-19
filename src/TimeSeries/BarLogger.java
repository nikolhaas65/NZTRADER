/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TimeSeries;

import Utils.*;
import java.text.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author nik
 */
public class BarLogger {
// recID / recNAME / Day / Time / bid_IB / ask_IB / signal / prel / psig / fut_filt / corr / tcorr 
  static SimpleDateFormat dateformat = new SimpleDateFormat("ddMMyyyy HHmmss.SSS");
  static SimpleDateFormat dayformat = new SimpleDateFormat("ddMMyyyy");
  static NumberRenderer floatformat = new NumberRenderer(6,6);
  static NumberRenderer decimalformat = new NumberRenderer(0);
  PrintWriter mLogOut = null;
  String mName = "";
  static String mDB_Directory = "D:\\data\\BarData\\";
  
  public static void setDB_Directory(String name) {
    mDB_Directory = name;
  }

  public static String getBarFileName(long time, String name, String period) {
    String fname = mDB_Directory + "bar_hist_" + name.replace(" ", "_") + "_"+ period +"_" + dayformat.format(time) + ".dat";
//    System.out.println("getBarFileName: " + fname);
    return fname;
  }
  
  public BarLogger(String name,String period) {
    mLogOut = openFile2Write(getBarFileName(System.currentTimeMillis(),name,period));
  }
  
  public static boolean checkBarDataFile(long time, String name,String period) {
    return (new File(getBarFileName(time,name,period))).exists();
  }
  
  public void openFile2Read(String name) {
    BufferedReader reader = FileManager.getFileManager().getFile2Read(name);
    
  }
  
  PrintWriter openFile2Write(String fname) {
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
  
  public void printRecord(String Name, long time,
          double open, double high,double low,double close,
          double sz) {
    if(mLogOut==null) {
      mLogOut = openFile2Write("bar_hist_" + Name + "_" +
              dayformat.format(System.currentTimeMillis()) + ".dat");
    }
    String str = " " + dateformat.format(time);
    str = str + " " + floatformat.format(open) + " " + floatformat.format(high)
              + " " + floatformat.format(low) + " " + floatformat.format(close)
              + " " + decimalformat.format(sz);
//    System.out.println(str);
    mLogOut.println(str);
    mLogOut.flush();
  }

  static public void main(String[] args) {
//    System.out.println(HistoryLogger.df.format(System.currentTimeMillis()));
    BarLogger hlog = new BarLogger("XYZ","1min");
    hlog.printRecord("XYZ", System.currentTimeMillis(),1.1234567, 2.45556777, 3, 4, 5);
    try {hlog.close();} catch(Exception ex) {};
  }

}
