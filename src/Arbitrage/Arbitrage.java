/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Arbitrage;

import java.util.*;
import InstrumentManager.*;

/**
 *
 * @author nik
 */
public class Arbitrage {

  static private Hashtable<String,List<FinSecurity>> mArbitrage = null;
  static private synchronized Hashtable<String,List<FinSecurity>> getArbitrageTable() {
    if (mArbitrage == null) {
      mArbitrage = new Hashtable<String,List<FinSecurity>>();
    }
    return mArbitrage;
  }  // Order Map and List

//  public static synchronized void setSecurities(String name,List<FinSecurity> fs_list) {
//    getArbitrageTable().put(name, fs_list);
//  }

  public static synchronized void setSecurities(String name,List<String> lst) {
    if(lst.size()>1) {
      List<FinSecurity> fsList = new ArrayList<FinSecurity>();
      Iterator<String> iter = lst.iterator();
      while(iter.hasNext()) {
        String fsName = iter.next();
        fsList.add(InstrumentManager.getFinSecurity(fsName));
      }
      getArbitrageTable().put(name, fsList);
    }
  }

  public static synchronized List<FinSecurity> getSecurities(String name) {
    return getArbitrageTable().get(name);
  }

  public static synchronized double getTotalPosition(String name) {
    List<FinSecurity> listFS = getSecurities(name);
//    System.out.println("Arbitrage: getTotal: #n " + listFS.size());
    Iterator<FinSecurity> iter = listFS.iterator();
    double pos = 0;
    while(iter.hasNext()) {
      OrderManager.OrderManager om = iter.next().getOrderManager();
      // orders position
      pos = pos + om.getTotalOutstandingPosition();
      // running position
      pos = pos + om.getCurrentPosition().getQuantity();
    }
    return pos;
  }

  static Utils.NumberRenderer df = new Utils.NumberRenderer(3,3);

  public static synchronized OrderManager.Position getTotalPNL(String name,double bid, double ask) {
    List<FinSecurity> listFS = getSecurities(name);
//    System.out.println("Arbitrage: getTotal: #n " + listFS.size());
    Iterator<FinSecurity> iter = listFS.iterator();
    double pos = 0;
    long t=0;
    double prpr=0;
    while(iter.hasNext()) {
      OrderManager.OrderManager om = iter.next().getOrderManager();
      pos = pos + om.getCurrentPosition().getQuantity();
      if(prpr<=0) prpr=om.getPrevPrice();
      if(om.getLastExecTime()>t) {
        t=om.getLastExecTime();
        prpr=om.getPrevPrice();
      }
    }

//    this print is taylored for AEX/EOE futures
//    it does not affect further calcs
    double price = pos>0?bid:pos<0?ask:0.5*(bid+ask);
//    AlgoTrader.Dispatcher.mGUI.setDebuggerText(df.format(pos) +
//            "/" + df.format(price) + "/" + df.format(prpr)+
//            " = " +df.format((price-prpr)*pos-Math.abs(pos)*3));

    OrderManager.Position current_pos = new OrderManager.Position();
    current_pos.price = price-prpr;
    current_pos.setQuantity(pos);

    return current_pos;
  }

}
