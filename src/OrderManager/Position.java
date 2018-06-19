/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderManager;

import java.util.*;
import InstrumentManager.*;

/**
 *
 * @author nik
 */
public class Position {
// private int quantity;
 private double quantity;
 public double price;
 public long time;
 public int orderid;
 public List<Integer> bookedOrders=null;
 
// public int getQuantity() {
 public double getQuantity() {
   return quantity;
 }
 public void setQuantity(double num) {
   quantity=num;
 }
 
 public Position() {
   quantity = 0;
   price = 0;
   time = 0;
   orderid = 0;
   bookedOrders = new ArrayList<Integer>();
 }
 public Position(int num,double prc) {
   quantity = num;
   price = prc;
   time = 0;
 }

 public synchronized Position update(Position pos, JOrder jorder) {
   pos.quantity = jorder.mFilled*jorder.mDirection;
   pos.price  = jorder.mAvePrice;
   System.out.println("New position: " + jorder.toString() + " avePr:" + jorder.mAvePrice);
   if(!bookedOrders.contains(jorder.mOrderId)) {
     bookedOrders.add(jorder.mOrderId);
     this.quantity += pos.quantity;
     this.price = pos.price;
     System.out.println("Update " + this.quantity + " " + this.price);
   }
   return pos;
 }
 
 public String toString() {
   return Utils.TimeUtils.Time2String(2,time) + " " + quantity + " " + price;
 }

}
