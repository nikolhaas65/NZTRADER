package Utils;

/**
 */

public class TimeTriggerReading extends TimeTrigger {

  // System-time
  public TimeTriggerReading(long waiting_time, long time) {
    elapsedTime = time;
    waitingTime = waiting_time;
    started = false;
  } // TimeTrigger

// Trigger FIRED
// Can be called only once
  public boolean isFired(long time) {
    if(started) {
      elapsedTime = time - prevTime;
      fired = elapsedTime > waitingTime;
      if (fired) {
        prevTime = time;
        fired = false;
        return true;
      }
    }
    return false;
  } // isFired()

  public void start(long time) {
    fired = false;
    started = true;
    prevTime = time;
    elapsedTime = 0;
  }

} // end of class
