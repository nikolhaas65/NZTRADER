/*
 * LogPars.java
 *
 * Created on April 1, 2005, 4:29 PM
 */

package Utils;

import java.util.*;
import java.io.*;

/**
 *
 * @author uzaits
 */
public class DataLogger {
    
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
    
    static TreeMap<String,Integer> tm=null;
    static PrintWriter mLogOut = null;
    static String mFileName = "";

    private DataLogger mLogger = null;
    
    public DataLogger getLogger() {
      if(mLogger==null) {
        mLogger = new DataLogger("none");
      }
      return mLogger;
    }
    
    public void setLogLevel(String level) {
      debugLevel = getDebugLevel(level);
    }
    
    /**
     * Defines the level for Logger
     * @param level {@link String} value. One of these values is possible:
     * <ul>
     * <li> "none"
     * <li> "brief"
     * <li> "average"
     * <li> "detail"
     * </ul>
     */
    public DataLogger(String level) {
        outputFormat = true;
        tm = new TreeMap<String,Integer>();
        tm.put("none",new Integer(0));
        tm.put("brief",new Integer(1));
        tm.put("average",new Integer(2));
        tm.put("detail",new Integer(3));
        debugLevel = getDebugLevel(level);
    }

    static public int getDebugLevel(String level) {
        return (tm.get(level)).intValue();
    }
    
    static public boolean debug_none() {
        return debugLevel>=0;
    }
    
    static public boolean debug_brief() {
        return debugLevel>=1;
    }
    
    static public boolean debug_average() {
        return debugLevel>=2;
    }
    
    static public boolean debug_detail() {
        return debugLevel>=3;
    }
    
   public static PrintWriter openFile(String fname) {
       PrintWriter a = null;
       try{
           a = new PrintWriter(new BufferedWriter(new FileWriter(fname)));
       } catch(Exception ex) {
           ex.printStackTrace();
       }
       return a;
   }
   
   public static void closeLog() {
       if(mLogOut!=null) mLogOut.close();
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

    public static void LogLn(String str) {
        try {
            if(mLogOut==null) mLogOut=openFile("log.txt");
        } catch (Exception ex) {
            System.out.println("ERROR: Opening file log.txt");
        }
        mLogOut.println(str);
    }
   
   public static void LogStr(String str) {
       try {
           if(mLogOut==null) mLogOut=openFile("log.txt");
       } catch (Exception ex) {
           System.out.println("ERROR: Opening file log.txt");
       }
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
   
   public static void Stdout(String str) {
       System.out.println(str);
   }
    
    public static void main(String[] args) {
        System.out.println("none="+debug_none());
        System.out.println("brief="+debug_brief());
        System.out.println("average="+debug_average());
        System.out.println("detail="+debug_detail());
    }

    
}
