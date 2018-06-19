/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ReaderLink;

import java.util.*;
import com.ib.client.*;
import IBLink.*;
import OrderManager.JOrder;

/**
 *
 * @author nik
 */
public class ContractBook {

  Map<Integer,JOrder> mJOrderMap = null;
  int mContractID;
  int mOrderSize;

  public ContractBook(int id, int ordersz) {
    this.mContractID = id;
    this.mOrderSize = ordersz;
  }

  private synchronized Map<Integer,JOrder> getJOrderMap() {
    if(this.mJOrderMap==null) {
      this.mJOrderMap = new HashMap<Integer,JOrder>();
    }
    return this.mJOrderMap;
  }

  public Order getOrder(int orderID) {
    return getJOrder(orderID).getOrder();
  }

  public JOrder getJOrder(int orderID) {
    return this.getJOrderMap().get(orderID);
  }

//orderStatus(int orderId, String status,
//          int filled, int remaining, double avgFillPrice,
//          int permId, int parentId, double lastFillPrice,
//          int clientId, String whyHeld)
  public void putOrder(int orderID, Order order) {
    JOrder jorder = new JOrder(order);
    this.getJOrderMap().put(orderID,jorder);
//    eWrapper().orderStatus( id, status,
//                filled, remaining, avgFillPrice,
//                permId, parentId, lastFillPrice, clientId, whyHeld);
    IBLink.getIBLink().orderStatus(orderID, "Submitted", 
            0, order.totalQuantity(), jorder.mPrice,
            0, 0, jorder.mPrice, 0,null,0.0);
  }

  public void removeOrder(int orderID) {
    this.getJOrderMap().remove(orderID);
  }

  double mTradedPrice;
  int mTradedSize;
  int mTradedSide; // buy=+1, sell=-1

  public void setTrade(double price, int sz, int side) {
    mTradedPrice = price;
    mTradedSize = sz;
    mTradedSide = side;
  }

  public void execTrades() {
//    scan through current set of orders and check for execution.
//    if ok, execute
    Iterator<Integer> iords = getJOrderMap().keySet().iterator();
    while(iords.hasNext()) {
      int orderID = iords.next();
      JOrder jorder = getJOrderMap().get(orderID);
      double remained = jorder.mTotal-jorder.mFilled;
      if ((mTradedSide > 0 && jorder.mPrice <= mTradedPrice) ||
              (mTradedSide < 0 && jorder.mPrice >= mTradedPrice) ) {
        double remained2 = Math.max(0, remained - mTradedSize);
        Execution exec = new Execution();
        exec.price(mTradedPrice);
        exec.shares(remained - remained2);
        jorder.add(exec);
        IBLink.getIBLink().execDetails(jorder.mOrderId, ContractRegister.getContract(mContractID), exec);
        if(remained2<=0) {
          IBLink.getIBLink().orderStatus(jorder.mOrderId, "Filled", 
                  jorder.mTotal, 0, jorder.mAvePrice,
                  0, 0, mTradedPrice, 0, null, 0.0);
          getJOrderMap().remove(orderID);
        }
      }
    }
  } //end

}//end class
