/*
 * LogPars.java
 *
 * Created on April 1, 2005, 4:29 PM
 */
package Utils;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 *
 * @author uzaits
 */
public class Logger {

  public boolean outputFormat = true;
  /**
  * we assume 4 levels of debug output: 
  * <ul>
  * <li>   0 - "none"
  * <li>   1 - "brief"
  * <li>   2 - "average"
  * <li>   3 - "detail"    
  * </ul>
  */
  static public int debugLevel = 0;
  static TreeMap<String,Integer> tm = null;
  private static TreeMap<String,Integer> getMapDebugLevel() {
    if(tm==null) {
      tm = new TreeMap<String,Integer>();
      tm.put("none", new Integer(0));
      tm.put("brief", new Integer(1));
      tm.put("average", new Integer(2));
      tm.put("detail", new Integer(3));
    }
    return tm;
  }
  static PrintWriter mLogOut = null;

  /**
   * Creates Logger object with {@link String} level of debugging
   * @param level {@link String} value of debugging
   * 
   */
  public Logger(String level) {
    outputFormat = true;
    getMapDebugLevel();
    debugLevel = getDebugLevel(level);
  }

  static public int getDebugLevel(String level) {
    return ((Integer) getMapDebugLevel().get(level)).intValue();
  }

  static public boolean debug_none() {
    return debugLevel >= 0;
  }

  static public boolean debug_brief() {
    return debugLevel >= 1;
  }

  static public boolean debug_average() {
    return debugLevel >= 2;
  }

  static public boolean debug_detail() {
    return debugLevel >= 3;
  }

  public static PrintWriter openFile(String fname) {
    PrintWriter a = null;
    try {
      a = FileManager.getWFile(fname);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return a;
  }

  public static void closeLog() {
    if (mLogOut != null) {
      mLogOut.close();
    }
  }

  public static void LogBunner() {
    LogLnOut("+++++++++++++++++++++++++++++");
    LogLnOut("+                           +");
    LogLnOut("+    BB2G1 - program        +");
    LogLnOut("+                           +");
    LogLnOut("+    Version  1.1           +");
    LogLnOut("+    Author: N.Zaitsev      +");
    LogLnOut("+                           +");
    LogLnOut("+++++++++++++++++++++++++++++");
  }

  static SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy HHmmss.SSS");
  
  private static void openLogFile() {
    try {
      if (mLogOut == null) {
        mLogOut = openFile("log.txt");
      }
    } catch (Exception ex) {
      System.out.println("ERROR: Opening file log.txt");
    }
  }

  public static void LogLn(String str) {
    openLogFile();
    str = df.format(System.currentTimeMillis()) + " " + str;
    mLogOut.println(str);
  }

  public static void LogStr(String str) {
    openLogFile();
    str = df.format(System.currentTimeMillis()) + " " + str;
    mLogOut.print(str);
  }

  public static void LogLnOut(String str) {
    LogLn(str);
    System.out.println(str);
  }

  public static void LogStrOut(String str) {
    LogStr(str);
    System.out.print(str);
  }

  public static void main(String[] args) {
    Logger log = new Logger("brief");
    System.out.println("none=" + debug_none());
    System.out.println("brief=" + debug_brief());
    System.out.println("average=" + debug_average());
    System.out.println("detail=" + debug_detail());
  }
}
