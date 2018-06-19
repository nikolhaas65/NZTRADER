/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrderManager;

import com.ib.client.*;
import IBLink.*;
import Utils.*;

/**
 *
 * @author nik
 * 
 * Wrapper around IB Order 
 * 
 */
public class JOrder {

  public enum Status {SentOut,PendingSubmit,PendingCancel,
  PreSubmitted,Submitted,Cancelled,Filled,Inactive};

  private static NumberRenderer fd = new NumberRenderer(5);
  //---- active order part
  public int mOrderId;
  public int mContractID;
//  public int mTotal;
  public double mTotal;
  public int mDirection; // buy,sell = 1,-1
  public String mAction;
  public double mPrice;
  //---- position part
//  public int mFilled;
  public double mFilled;
  public double mAvePrice;
  public double mTotalCash;
  public Order mOrder;
  public long mDate;
  public Status mStatus;
  public String mOrderType;

  public JOrder(int orderId) {
    this.mOrderId = orderId;
    this.mContractID = 0;
    this.mTotal = 0;
    this.mDirection = 0;
    this.mAction = "";
    this.mPrice = -1;
    this.mFilled = -1;
    this.mAvePrice = -1;
    this.mTotalCash = -1;
    this.mOrder = null;
    this.mDate = System.currentTimeMillis();
    this.mStatus = Status.SentOut;
    this.mOrderType="";
  }

  public JOrder(Order order) {
    this.mOrderId = order.orderId();
    this.mContractID = 0;
    this.mTotal = order.totalQuantity();
    this.mDirection =
            (order.getAction().equalsIgnoreCase("SELL")) ? -1 : 
            (order.getAction().equalsIgnoreCase("BUY")) ? 1 : 0;
    this.mAction = order.getAction();
    this.mPrice = order.lmtPrice();
    this.mFilled = 0;
    this.mAvePrice = -1;
    this.mTotalCash = 0;
    this.mOrder = order;
    this.mDate = System.currentTimeMillis();
    this.mStatus = Status.SentOut;
    this.mOrderType=order.orderType().toString();
  }

  public void setStatus(Status status) {
    mStatus=status;
  }

  public boolean isActive() {
    boolean act=true;
    switch(mStatus) {
//      SentOut,PendingSubmit,PendingCancel,
//  PreSubmitted,Submitted,Cancelled,Filled,Inactive
      case SentOut:
              act=false;
    }
    return act;
  }

//  public int remained() {
  public double remained() {
    return (mTotal - mFilled) * mDirection;
  }
  
//  public int remainedQty() {
  public double remainedQty() {
    return mTotal - mFilled;
  }

  @Override
  public String toString() {
    Contract cnt = ContractRegister.getContract(this.mContractID);
    return "ID: " + this.mOrderId + " (" + this.mAction + " x " + this.mTotal +
            " @ " + fd.format(this.mPrice) + " -> " + fd.format(this.mAvePrice) +
            " " + mStatus +")" + " contract: " + this.mContractID + " " +
            cnt.symbol();
  }

  public synchronized void setPrice(double price) {
    this.mPrice = price;
    mOrder.lmtPrice(price);
  }

  public Order getOrder() {
    return mOrder;
  }
// this comes from Status
  public void update(double filled, double avePrc) {
    this.mFilled = filled;
    this.mAvePrice = avePrc;
  }

//  public void add(int shares,double price) {
//    this.mFilled += shares;
//    this.mTotalCash += price * shares;
//    if (mFilled == mOrder.m_totalQuantity) {
//      this.mAvePrice = this.mTotalCash / this.mFilled;
//      System.out.println("JOrder.add #" + this.mOrderId + " " + this.mAvePrice);
//      mDate = System.currentTimeMillis();
//    }
//  }

// this comes from execDetails
  public void add(Execution execution) {
    if(Math.abs(execution.shares())>0) {
      this.mFilled += execution.shares();
      this.mTotalCash += execution.price() * execution.shares();
//      potential problem here due to change of INT to DOUBLE
      if (Math.abs(mFilled-mOrder.totalQuantity())<1e-8) {
        this.mAvePrice = this.mTotalCash / this.mFilled;
        System.out.println("JOrder.add #" + this.mOrderId + " " + this.mAvePrice);
        mDate = System.currentTimeMillis();
      }
    }
  }

  public boolean isFilled(String status) {
    boolean fd = (mFilled == mTotal || status.equalsIgnoreCase("Filled"));
    System.out.println("JOrder.isFilled: order# " + mOrderId + " x " + mTotal +
            " : " + status + " " + fd);
    return fd;
  }
}
