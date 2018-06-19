/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderExecutor;

import OrderManager.GlobalOrderRegister;
import InstrumentManager.*;
import IBLink.*;
import com.ib.client.*;
import JNums.*;
import OrderManager.*;
import Indicators.*;

/**
 *
 * @author nik
 */
public class MA_FollowBidAsk extends MicroStrategy {
  private String mOrderType="MKT";
  private BA_Indicator mBidAskEstim = null;

  public MA_FollowBidAsk(String orderType, BA_Indicator ba_estim) {
    mOrderType = orderType;
    mBidAskEstim = ba_estim;
  }
  
  public String getOrderType() {
    return mOrderType;
  }
  
  public synchronized void checkOrder(FinSecurity finsec,OrderManager pm,JOrder jorder) {

    DataFeed currData = finsec.getDataFeed();
    int cntID = finsec.getContractID();

//    double price = BAPriceLevel.getEstimPriceLevel(
//            Math.signum(pm.getTobePosition().getNumber()),
//            currData.bid, currData.ask,
//            currData.midA, currData.sprA, 1);
//    price = jMath.round(price,currData.tick_size);

    int dir = jorder.mDirection;
    double price = 0;

    long dtime = System.currentTimeMillis()-jorder.mDate;
    mBidAskEstim.setTimeInMarket(dtime);

    if(dir>0) {
//      buy
      price = mBidAskEstim.getBidAsk(BA_Indicator.BidAsk.bid);
    } else if(dir<0) {
//      sell
      price = mBidAskEstim.getBidAsk(BA_Indicator.BidAsk.ask);
    }

    price = jMath.round(price,finsec.getDataFeed().tick_size);
    double dist2ba = Math.abs(price - 0.5d*currData.tick_size*dir - jorder.mPrice);
    if (dist2ba>0.5d*currData.tick_size && jorder.isActive()) {
      modify(cntID,jorder,price);
    }
    
  }

  public void MICRO_TRADE(FinSecurity mFinSec,
        String action,double price, double totalQuantity) {
//        String action,double price,int totalQuantity) {
    
          Order order = new Order();
          order.action(action);
          order.lmtPrice(jMath.round(price,mFinSec.getDataFeed().tick_size));
          order.totalQuantity(totalQuantity);
          order.orderType(mOrderType);
          order.orderId(IBLink.mNextOrderId);
          JOrder jord = new JOrder(order);

//          JOrder jord = new JOrder(IBLink.mNextOrderId);
//          jord.mAction=action;
//          jord.mPrice=jMath.round(price,mFinSec.getDataFeed().tick_size);
//          jord.mTotal=totalQuantity;
//          jord.mOrderType=mOrderType;
          
          OrderManager ordMan = mFinSec.getOrderManager();
          ordMan.addOrder(jord);

          System.out.println("MA_FollowBidAsk.TRADE: Order: " + jord.toString());
          System.out.println("MA_FollowBidAsk.TRADE: OM: " + ordMan.getNumOfOrders());System.out.flush();

          System.out.println("MA_FollowBidAsk.TRADE: Registered SUBMIT: " +
                  ordMan.getJOrder(order.orderId()).toString()); System.out.flush();
          System.out.println("MA_FollowBidAsk.TRADE: Total #orders: " + ordMan.getNumOfOrders() +
                  " Rem: " + ordMan.getRemainedOrdersPosition()); System.out.flush();

          GlobalOrderRegister.registerMicroStrategy(order.orderId(), this);

          int contactID = mFinSec.getContractID();
          Contract contract = ContractRegister.getContract(contactID);
          System.out.println("MA_FollowBidAsk.TRADE: contract " + contactID +" " + contract.symbol() +
                  " ordID: " + order.orderId());
          System.out.flush();

          IBLink.getClient().placeOrder(order.orderId(), contract, order);

          IBLink.mNextOrderId++;
          
          System.out.println("MA_FollowBidAsk.TRADE: After placeOrder NextOrderId: " + IBLink.mNextOrderId);
          System.out.flush();
          
  };

}
