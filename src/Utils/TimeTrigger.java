package Utils;

/**
 * <p>Title: BridgeTest</p>
 * <p>Description: Test of Bridge examples</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: fortis</p>
 * @author NikZaitsev
 * @version 1.0
 */

import java.text.*;

public class TimeTrigger {
  protected long prevTime;
  protected long elapsedTime;
  protected long waitingTime;
  protected boolean fired = false;
  protected boolean started = false;
  protected SimpleDateFormat df_time = new SimpleDateFormat("hh:mm:ss aa");

// Check status
  public String Status() {

    String start;

    if (started) start = "Started";
    else start = "Not Started";

    return
        start +
        " Elapsed: " +
        df_time.format(new java.util.Date(System.currentTimeMillis() - prevTime));

  } // Status()

  public long getElapsed() {
    return elapsedTime;
  }

  public TimeTrigger() {};

  // System-time
  public TimeTrigger(long waiting_time) {
    elapsedTime = System.currentTimeMillis();
    waitingTime = waiting_time;
    started = false;
  } // TimeTrigger

// Trigger FIRED
// Can be called only once
  public boolean isFired() {
    if(started) {
      elapsedTime = System.currentTimeMillis() - prevTime;
      fired = elapsedTime > waitingTime;
      if (fired) {
        prevTime = System.currentTimeMillis();
        fired = false;
        return true;
      }
    }
    return false;
  } // isFired()

  public void start() {
    fired = false;
    started = true;
    prevTime = System.currentTimeMillis();
    elapsedTime = 0;
  }

} // end of class
