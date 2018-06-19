/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package JNums;
import JNums.*;

/**
 *
 * @author nik
 */
public class TSeriesRandomGenerator {

  public double curVal=100.0d;
  public double getNext() {
    curVal+=(2.0d*Math.random()-1.0d);
    return jMath.round(curVal,0.01d);
  }
  
}
