/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimeSeries;

/**
 *
 * @author nik
 */
public abstract class Bar extends Object {
  abstract void update(double price, long sz);
}
