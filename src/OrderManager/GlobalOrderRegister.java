/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderManager;

import InstrumentManager.FinSecurity;
import java.util.*;
import OrderExecutor.*;

/**
 *
 * @author nik
 */
public class GlobalOrderRegister {
  static private Hashtable<Integer,FinSecurity> mRegister = null;
  static private synchronized Hashtable<Integer,FinSecurity> getFinSecurityTable() {
    if (mRegister == null) {
      mRegister = new Hashtable<Integer,FinSecurity>();
    }
    return mRegister;
  }  // Order Map and List
  
  static private Hashtable<Integer,MicroStrategy> mRegisterMicroStrategy = null;
  static private synchronized Hashtable<Integer,MicroStrategy> getMicroStrategiesTable() {
    if (mRegisterMicroStrategy == null) {
      mRegisterMicroStrategy = new Hashtable<Integer,MicroStrategy>();
    }
    return mRegisterMicroStrategy;
  }  // Order Map and List
  
  static public void registerMicroStrategy(int orderID,MicroStrategy mStrat) {
    getMicroStrategiesTable().put(orderID, mStrat);
  }
  
  static public MicroStrategy getMicroStrategy(int orderID) {
    return getMicroStrategiesTable().get(orderID);
  }
  
  static public Set<Integer> getAllOrderIDs() {
    Hashtable<Integer,FinSecurity> fst = getFinSecurityTable();
    if(fst!=null) {
      return fst.keySet();
    } else {
      System.out.println("GlobalOrderManager:getAllOrderIDs: table is null");
    }
    return null;
  }
  static public synchronized FinSecurity getFinSecurity(int orderId) {
    Hashtable<Integer,FinSecurity> fst = getFinSecurityTable();
    if(fst!=null) {
      FinSecurity fs = fst.get(orderId);
      if(fs!=null) {
        return fs;
      } else {
          System.out.println("GlobalOrderManager:getFinSecurity: finsec is null");
      }
    } else {
      System.out.println("GlobalOrderManager:getFinSecurity: table is null");
    }
    return null;
  }
  
  static public synchronized void registerOrder(int orderId, FinSecurity finsec) {
    Hashtable<Integer,FinSecurity> fst = getFinSecurityTable();
    if(fst!=null && !fst.containsKey(orderId)) {
       fst.put(orderId, finsec);
    } else {
      System.out.println("GlobalOrderManager:registerOrder: manager is null");
    }
    
  }

  static private List<Integer> mRemoved = null;
  static private synchronized List<Integer> getRemovedOrdersTable() {
    if (mRemoved == null) {
      mRemoved = new ArrayList<Integer>();
    }
    return mRemoved;
  }  // Order Map and List
  static public boolean orderIsRemoved(int orderId) {
    return getRemovedOrdersTable().contains(orderId);
  }
  
  static public synchronized void removeOrder(int orderId) {
    Hashtable<Integer,FinSecurity> fst = getFinSecurityTable();
    if(fst!=null) {
       fst.remove(orderId);
       getRemovedOrdersTable().add(orderId);
    } else {
      System.out.println("GlobalOrderManager:registerOrder: manager is null");
    }
    
  }
  
}
