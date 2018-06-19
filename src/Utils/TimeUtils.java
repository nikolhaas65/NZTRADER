/*
 * Time.java
 *
 * Created on 27 June 2005, 20:35
 */

package Utils;
import java.text.*;
import java.util.Date;

/**
 *
 * @author nik
 */
public class TimeUtils {
  static SimpleDateFormat ddMMyyyy_HHmmss_SSS = new SimpleDateFormat("ddMMyyyy HHmmss.SSS");
  static SimpleDateFormat ddMMyyyy_HHmmss = new SimpleDateFormat("ddMMyyyy HHmmss");
  static SimpleDateFormat ddMMyyyy = new SimpleDateFormat("ddMMyyyy");
  static SimpleDateFormat dd_MM_yyyy_HH_mm_ss = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
  static SimpleDateFormat ddMMyyyy_HH_mm_ss = new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS");

  private static SimpleDateFormat HH_mm_ss_aa = new SimpleDateFormat("HH:mm:ss aa");
  private static SimpleDateFormat HHmmss = new SimpleDateFormat("HHmmss");
  private static SimpleDateFormat HH_mm_ss = new SimpleDateFormat("HH:mm:ss");

  private long currentDayStart = 0;

  private static final long oneHour = 3600000L;
  private static final long allDayLong = 24L*oneHour;


  /**
   * Creates a new instance of TimeUtils
   * @param time is a time to init this object
   */
  public TimeUtils(long time) {
//    time = time-time%allDayLong - allDayLong*(long)(Math.random()*100d);
//    currentDayStart = time-time%allDayLong - 1*oneHour;
    sameDay(time);
    currentDayStart = stickToDayStart(currentDayStart);
  }

 /**
  * Check if time comes from the same Day as TimeUtis
  * @param time {@link long} time
  * @return true if from the same Day
  */
  public boolean sameDay(long time) {
//    System.out.println("sameDay: " + time + " " + this.currentDayStart);
    boolean same = (time-currentDayStart)<allDayLong;
    if (!same) currentDayStart = stickToDayStart(time);
    return same;
  }

  public long stickToDayStart(long time) {
    long t = currentDayStart;
      try {
        t = (ddMMyyyy.parse(ddMMyyyy.format(time))).getTime();
      } catch(Exception ex) {System.out.println(ex);};
      return t;
  }

  public String getCurrentDayString() {
    return Time2String(2,currentDayStart);
  }

  public long getCurrentDay() {
    return currentDayStart;
  }

  public static double getDayFraction(double time) {
    double fr = time/(double)allDayLong;
    return fr-(double)((long)fr);
  }

  public static long getDayTimeL(long dt) {
    return getDayTime(new Date(dt));
  }

  public static long getDayTime(Date dt) {
    long ti = 0;
    try {
      ti = HHmmss.parse(HHmmss.format(dt)).getTime();
    } catch(Exception ex) {};
    return ti;
  }

  static public String Time2String(int format_type,long vtime) {
    switch(format_type) {
      case 1:
        return ddMMyyyy_HHmmss_SSS.format(vtime);
      case 2:
        return dd_MM_yyyy_HH_mm_ss.format(vtime);
      case 3:
        return HHmmss.format(vtime);
      case 4:
        return HH_mm_ss.format(vtime);
      case 5:
        return ddMMyyyy.format(vtime);
    }
    return ddMMyyyy_HHmmss_SSS.format(vtime);
  }

  static public Date String2Date(String time) {
    Date msg=null;
    try {
          msg = ddMMyyyy_HHmmss.parse(time);
    } catch(Exception ex) {};
    return msg;
  }
  static public Date String2Date2(String time) {
    Date msg=null;
    try {
          msg = ddMMyyyy_HHmmss_SSS.parse(time);
    } catch(Exception ex) {};
    return msg;
  }

  static public long String2Time(int format_type, String time) {
    long msg=0;
    try {
      switch(format_type) {
        case 1:
          msg = ddMMyyyy_HHmmss_SSS.parse(time).getTime();
          break;
        case 2:
          msg = dd_MM_yyyy_HH_mm_ss.parse(time).getTime();
          break;
        case 3:
          String sday = ddMMyyyy.format(System.currentTimeMillis());
          String inp = sday + " " + time + ".000";
          msg = ddMMyyyy_HHmmss_SSS.parse(inp).getTime();
          break;
        case 4:
          msg = HH_mm_ss.parse(time).getTime();
          break;
        case 5:
          msg = ddMMyyyy_HHmmss.parse(time).getTime();
          break;
        case 6:
          msg = HHmmss.parse(time).getTime();
          break;
      }
    } catch(Exception ex) {
      System.out.println("Exception: Utils.TimeUtils.String2Time: FType: " + format_type + " " + time);
    }
    return msg;
  }

  static public String getHMS(double fract) {
    int h = (int)Math.floor(fract*24);
    double f1 = (fract*24-h)*60;
    int m = (int)Math.floor(f1);
    int s = (int)Math.floor((f1-m)*60);
    return Integer.toString(h)+":"+Integer.toString(m)+":"+Integer.toString(s);
  }

  static public String getDayHMS(double fract) {
    return Integer.toString((int)Math.floor(fract))+" "+getHMS(fract-Math.floor(fract));
  }

  public static void main(String[] args) {
    System.out.println(TimeUtils.getDayHMS(2));
    System.out.println(Double.valueOf(System.currentTimeMillis()/1000/3600/24/365));
    System.out.println("formatted: "+ddMMyyyy_HHmmss_SSS.format(System.currentTimeMillis()));
    System.out.println(System.currentTimeMillis());
    try {
      System.out.println("value1: " + ddMMyyyy_HHmmss_SSS.parse("24012010 125517.042").getTime());
      System.out.flush();
      System.out.println("value2: " + ddMMyyyy_HHmmss_SSS.format(String2Time(6,"000001")));
      System.out.flush();
      System.out.println("value3: " + HH_mm_ss.format(HH_mm_ss.parse("02:00:00")));
      System.out.println("value4: " + dd_MM_yyyy_HH_mm_ss.format(String2Time(6,"000000")));

      String day = "01062011";
      Date dt = String2Date(day + " 090008");
      System.out.println("value5: " + Time2String(4,dt.getTime()) + " " + dt.getTime());
      long day_time = String2Time(6,HHmmss.format(dt));
      System.out.println("time: " + Time2String(4,day_time) + " " + day_time);

      dt = String2Date(day+ " 000000");
      System.out.println("value6: " + Time2String(2,dt.getTime()) + " " + dt.getTime());
      day_time = String2Time(6,HHmmss.format(dt));
      System.out.println("x  time: " + Time2String(4,day_time) + " day_time: " + day_time);
      System.out.println("xx time: " + Time2String(4,getDayTime(dt)));

      TimeUtils tu = new TimeUtils(dt.getTime());
      tu.sameDay(dt.getTime());
      System.out.println("tu " + Time2String(4,tu.getCurrentDay()));
      System.out.flush();

      System.out.println("Start ---- ");
      TimeUtils time = new TimeUtils(System.currentTimeMillis());
      System.out.println("--- " + Time2String(2,time.getCurrentDay()) + " " );
      if(time.sameDay(System.currentTimeMillis())) {
        System.out.println("same "+ Time2String(3,time.getCurrentDay()));
      }
      System.out.println("same2 "+ time.getCurrentDay());

    } catch(Exception ex) {System.out.println(ex);}
  }

}
