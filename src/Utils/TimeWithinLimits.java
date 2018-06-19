/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

/**
 *
 * @author nik
 */
public class TimeWithinLimits {
  private long timeS;
  private long timeE;
  public TimeWithinLimits(long start, long end) {
    timeS = start;
    timeE = end;
  }
  public boolean isWithin(long time) {
    long tau = TimeUtils.getDayTimeL(time);
    return timeS<=tau && tau<=timeE;
  }
}
