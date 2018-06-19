package Utils;

/**
 * <p>Title: BridgeTest</p>
 * <p>Description: Test of Bridge examples</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: fortis</p>
 * @author nikzaitsev
 * @version 1.0
 */

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.io.*;
import java.text.*;
import java.util.*;

public class Utilities {

  static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aa");
  static SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aa");
  static SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
  static SimpleDateFormat df4 = new SimpleDateFormat("ddMMyyyy HHmmss");

  static public Timestamp DateString2Timestamp(String str_date) {

    long time_mlsec = 0;
    try {
      time_mlsec = df.parse(str_date).getTime();
    }
    catch (Exception ex) {
      System.out.println("Utilities:DateString2Timestamp:warning");
      ex.printStackTrace();
    }
    return new java.sql.Timestamp(time_mlsec);
  }

  static public Timestamp TimeString2Timestamp(String str_date, String str_time) {

//    long time_mlsec= System.currentTimeMillis() - 7200000;
    long time_mlsec= System.currentTimeMillis();

    try {
      if (str_time != null) {
        if (str_time.length()>0 && str_date.length()>0) {
          time_mlsec = df.parse(str_date + " " + str_time).getTime();
        }
        else {
          time_mlsec = System.currentTimeMillis();
        }
      }
      else {
        time_mlsec = System.currentTimeMillis();
      }
    }
    catch (Exception ex) {
//      System.out.println("Utilities:TimeString2Timestamp:warning: date: " +
//        str_date + " : "+str_time);
//      ex.printStackTrace();
        time_mlsec = System.currentTimeMillis();
    }

    Timestamp tm = new Timestamp(time_mlsec);
//    System.out.println("Input time" + str_date +
//                       " " + str_time +
//                       "  Time:" + tm.toString());
    return tm;

  }

  static public Timestamp TimeString2Timestamp(String str_date, String str_time, int df_format) {

//    long time_mlsec= System.currentTimeMillis() - 7200000;
    long time_mlsec= System.currentTimeMillis();

    try {
      if (str_time != null) {
        if (str_time.length()>0 && str_date.length()>0) {
            switch (df_format) {
                case 1:
                  time_mlsec = df.parse(str_date + " " + str_time).getTime(); break;
                case 2:
                  time_mlsec = df2.parse(str_date + " " + str_time).getTime(); break;
                case 3:
                  time_mlsec = df3.parse(str_date + " " + str_time).getTime(); break;
                case 4:
                  time_mlsec = df4.parse(str_date + " " + str_time).getTime(); break;
                default:
            }
        }
        else {
          time_mlsec = System.currentTimeMillis();
        }
      }
      else {
        time_mlsec = System.currentTimeMillis();
      }
    }
    catch (Exception ex) {
//      System.out.println("Utilities:TimeString2Timestamp:warning: date: " +
//        str_date + " : "+str_time);
//      ex.printStackTrace();
        time_mlsec = System.currentTimeMillis();
    }

    Timestamp tm = new Timestamp(time_mlsec);
//    System.out.println("Input time" + str_date +
//                       " " + str_time +
//                       "  Time:" + tm.toString());
    return tm;

  }

  static public Timestamp TimeString2Timestamp2(String str_date, String str_time) {

//    long time_mlsec= System.currentTimeMillis()-7200000;
    long time_mlsec= System.currentTimeMillis();

    try {
      if (str_time != null) {
        time_mlsec = df3.parse(str_date + " " + str_time).getTime();
      }
      else {
//        time_mlsec = System.currentTimeMillis()-7200000;
        time_mlsec = System.currentTimeMillis();
      }
    }
    catch (Exception ex) {
      System.out.println("Utilities:TimeString2Timestamp2:warning");
      ex.printStackTrace();
    }

    Timestamp tm = new Timestamp(time_mlsec);

    return tm;

  }

  static public void wait_me(long tt) {
    try {Thread.sleep(tt);} catch (Exception ex) {};
  }

  static public String pad(String strText, int iMinimumLength) {
    StringBuffer strBuffer = new StringBuffer(strText);
    for (int i = strText.length(); i < iMinimumLength; ++i) {
      strBuffer.append(' ');
    }
    return strBuffer.toString();
  }

  static public Vector splitRecord(String str) {
    Vector rec = new Vector(0);
    StringTokenizer stok = new StringTokenizer(str,"\t");
    while (stok.hasMoreTokens()) {
        rec.add(stok.nextToken());
    }
    return rec;
  }

  static public Vector splitRecord(String str,String tok) {
    Vector rec = new Vector(0);
    StringTokenizer stok = new StringTokenizer(str,tok);
    while (stok.hasMoreTokens()) {
        rec.add(stok.nextToken());
    }
    return rec;
  }

  static public Vector splitWords(String strRow) {

    Vector vWords = new Vector(0);

    String[] vals = strRow.split(" ");
    for (int i = 0; i < vals.length; i++)
      if (vals[i].length() > 0) {
        vWords.add(vals[i].toLowerCase());
//        System.out.println(vals[i]);
      }

    return vWords;
  }

  public Utilities() {

  }

  public static void main(String[] args) {

      System.out.println(TimeString2Timestamp("18122006", "123000",4));
      String str = "330,335,340,345,350,355,360,365,370,375,380";
      Vector recs = splitRecord(str,",");
      System.out.println(recs.toString());
      System.out.println(Double.parseDouble(recs.get(3).toString()));
  }

}
