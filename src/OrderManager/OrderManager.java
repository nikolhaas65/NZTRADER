/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OrderManager;

import java.util.*;
import com.ib.client.*;

import IBLink.*;
import InstrumentManager.FinSecurity;
import OrderExecutor.*;
import AlgoTrader.Dispatcher;
import AlgoTrader.PortfolioMonitorTableModel;

/**
 *
 * @author nik
 */
public class OrderManager {

//  private static Dispatcher mDisp = Dispatcher.getDispatcher();

  // position History
  private List<Position> mPositionHistory = null;

  private synchronized List<Position> getPositionHistory() {
    if (mPositionHistory == null) {
      mPositionHistory = new ArrayList<Position>();
    }
    return mPositionHistory;
  }  // Order Map and List

  private FinSecurity mFinSec = null;

  public OrderManager(FinSecurity finsec) {
    mFinSec = finsec;
  }
  private Map<Integer, JOrder> mOrders;

  private synchronized Map<Integer, JOrder> getOrdersMap() {
//    synchronized(mOrders) {
    if (mOrders == null) {
      //      mOrders = new ConcurrentHashMap<Integer, JOrder>();
      mOrders = new HashMap<Integer, JOrder>();
    }
    return mOrders;
//    }
  }

  public synchronized int getNumOfOrders() {
    return getOrdersMap().size();
  }

  public synchronized void addOrder(JOrder order) {
    order.mContractID = mFinSec.getContractID();
    getOrdersMap().put(order.mOrderId, order);
    GlobalOrderRegister.registerOrder(order.mOrderId, mFinSec);
  }

  public synchronized void removeOrder(int orderID) {
//    System.out.println("PortfolioManager:removeOrder: " + orderID);
    System.out.flush();
    try {
      if (getOrdersMap().containsKey(orderID)) {
        int ordid = getOrdersMap().get(orderID).mOrderId;
        if (ordid == orderID) {
          GlobalOrderRegister.removeOrder(orderID);
          getOrdersMap().remove(orderID);
        } else {
          System.out.println("PortfolioManager:removeOrder : problem: " + orderID + " : " + ordid);
          System.out.flush();
        }
      } else {
        System.out.println("PortfolioManager:removeOrder: no such orderID: " + orderID);
      }
    } catch (Exception ex) {
      System.out.println("PortfolioManager:removeOrder : err: " + ex.getMessage());
      System.out.flush();
    }
    System.out.println("PortfolioManager:removeOrder: removed " + orderID);
    System.out.flush();
  }

  public synchronized Order getOrder(int orderID) {
    Order order = null;
    try {
      if (getOrdersMap().containsKey(orderID)) {
        order = getOrdersMap().get(orderID).getOrder();
        if (order != null) {
          int ordid = order.orderId();
          if (ordid == orderID) {
            return order;
          }
        }
      } else {
        System.out.println("PortfolioManager:getOrder: no such orderID: " + orderID);
      }
    } catch (Exception ex) {
      System.out.println("PortfolioManager:getOrder : err: " + ex.getMessage());
    }
    return order;
  }

  synchronized public JOrder getJOrder(int orderID) {
    JOrder order = null;
    try {
      if (getOrdersMap().containsKey(orderID)) {
        order = getOrdersMap().get(orderID);
      } else {
        System.out.println("PortfolioManager:getJOrder: no such orderID: " + orderID);
      }
    } catch (Exception ex) {
      System.out.println("PortfolioManager:getJOrder : err: " + ex.getMessage());
    }
    return order;
  }

  synchronized public Set<Integer> getOrdersList() {
    return getOrdersMap().keySet();
  }

  synchronized public boolean orderExist(int orderID) {
    if (getOrdersMap() == null) {
      return false;
    }
    return getOrdersMap().keySet().contains(orderID);
  }

  void cancelAll() {
    synchronized (mOrders) {
      Iterator<Integer> iter = getOrdersMap().keySet().iterator();
      while (iter.hasNext()) {
        int orderId = iter.next();
        iter.remove();
        IBLink.getIBLink().cancel(orderId);
      }
    }
  }

  public synchronized int getRemainedOrdersPosition() {
//    synchronized(mOrders) {
    int pos = 0;
    Iterator<Integer> iter = getOrdersList().iterator();
    while (iter.hasNext()) {
      Integer orderid = iter.next();
      if (orderid != null && orderid.intValue() >= 0) {
        pos += getOrdersMap().get(orderid.intValue()).remained();
      }
    }
    return pos;
//    }
  }

  double mPrevPrice= 0.d;
  double mCash = 0.d;
  int mOrd = 0;
  int mCumPos =0;

  public double getPrevPrice() {
    return mPrevPrice;
  }

  public long getLastExecTime() {
    List<Position> pHistory = getPositionHistory();
    long t=0;
    if(pHistory.size()>0) {
      Position p1 = pHistory.get(pHistory.size()-1);
      if(p1!=null) t=p1.time;
    }
    return t;
  }

  public double getRealisedPnL() {
    return mCash;
  }
  static Utils.NumberRenderer df = new Utils.NumberRenderer(3,3);
  public synchronized double updateRealisedPnl() {
    List<Position> pHistory = getPositionHistory();
//    Iterator<Position> iter = pHistory.iterator();
//    int cumPos = 0;
//    double prevPrice = 0.d;
//    double dCash = 0d;
//    Position pos=null;
//    int iOrd=0;
//    while(iter.hasNext()) {
//      pos = iter.next();
//      dCash += cumPos*(pos.price-prevPrice)*mFinSec.getMultiplier();
//      cumPos += pos.getQuantity();
//      prevPrice = pos.price;
//      iOrd++;
//    }

    Position p1 = pHistory.get(pHistory.size()-1);
    double qty = p1.getQuantity();
    if(mPrevPrice>0 && p1.price>0 && Math.abs(qty)>=0) {
      mCash += mCumPos*(p1.price-mPrevPrice)*mFinSec.getMultiplier();
      mCumPos += qty;
    }

    System.out.println("O1HIST: Pr: " + p1.price + " PrevPr: " + mPrevPrice + " Qty: " + p1.getQuantity());
    
    if(p1.price>0) mPrevPrice = p1.price;
    mOrd++;

    System.out.println("ORDERHIST " + p1.orderid + " " + mOrd + " " + p1.toString() + 
            " " + mCumPos + " " + df.format(mCash));
//    System.out.println("ORDERHIST: " + iOrd + " " + pos.toString() + " " + cumPos + " " + dCash);
    return mCash;
  }

  public synchronized int getUnRealisedPNL() {
//    scans all orders and returns total position
    int pos = 0;
    Iterator<Integer> iter = getOrdersList().iterator();
    int n = 0;
    while (iter.hasNext()) {
//      System.out.println("n:"+n);
      JOrder jord = getOrdersMap().get(iter.next());
      System.out.println("getTotalPos ID: " + jord.mOrderId
              + " dir: " + jord.mDirection + "Pr:  " + jord.mPrice + " filled: " + jord.mFilled
              + " total: " + jord.mTotal);
      pos += jord.mTotal * jord.mDirection;
      n++;
    }

    System.out.println("PortfolioManager.getTotalOutstandingPosition: currPos: " + currPosition.getQuantity()
            + " sumOrds: " + pos);
    return pos;
//    }
  }

  public synchronized int getTotalOutstandingPosition() {
//    scans all orders and returns total position
    int pos = 0;
    Iterator<Integer> iter = getOrdersList().iterator();
    int n = 0;
    while (iter.hasNext()) {
//      System.out.println("n:"+n);
      JOrder jord = getOrdersMap().get(iter.next());
      System.out.println("getTotalPos ID: " + jord.mOrderId
              + " dir: " + jord.mDirection + " filled: " + jord.mFilled
              + " total: " + jord.mTotal);
//      pos += jord.filled*jord.direction;
      pos += jord.mTotal * jord.mDirection;
      n++;
    }

//    synchronized(currPosition) {
    System.out.println("PortfolioManager.getTotalOutstandingPosition: currPos: " + currPosition.getQuantity()
            + " sumOrds: " + pos);
//    return currPosition.getQuantity() + pos;
    return pos;
//    }
  }

  public synchronized void rebookOrder(JOrder jorder,long time) {
    try {
      Position pos = new Position();
      System.out.println("PortfolioManager.rebookOrder: add history_order: " + jorder.toString());
      System.out.println("PortfolioManager.rebookOrder: add history_order: " + jorder.mAvePrice);
      pos = currPosition.update(pos, jorder);
      pos.time = time;
      pos.orderid = jorder.mOrderId;
      System.out.println("PortfolioManager.rebookOrder: add history_pos: " +
              mFinSec.getName() + " [" + mFinSec.getTableIndex() + "] " + pos.toString() );
      
      getPositionHistory().add(pos);
      double pnl = updateRealisedPnl();
      if(mFinSec.getTableIndex()>=0) {
        ((PortfolioMonitorTableModel)Dispatcher.getDispatcher().mGUI.
            getPortfolioMonitorTable().getModel()).setRealisedPNL(mFinSec.getTableIndex(), pnl);
        System.out.println("PortfolioManager.rebookOrder: add history_pos: pnl " + pnl);
      }
    } catch (Exception ex) {
      System.out.println("PortfolioManager.rebookOrder: failure: " + ex.getMessage());
    }
  }

  public String getAllOrdersMsg() {
    String msg = "";
    int n = 0;
    Iterator<Integer> iter = getOrdersList().iterator();
    while (iter.hasNext()) {
      msg = msg + n + ": ";
      msg = msg + getOrdersMap().get(iter.next()).toString() + "\n";
      n++;
    }
    return msg;
  }
  private Position currPosition = new Position();
  private Position tobePosition = new Position();

  public Position getCurrentPosition() {
    return currPosition;
  }

  public Position getTobePosition() {
    return tobePosition;
  }

// provides incremental trading
  public void TRADE_INCREMENTAL(double num, double price, MicroStrategy microStrat) {

    getTobePosition().setQuantity(num);

//    int pos2b = tobePosition.getQuantity();
    double pos2b = tobePosition.getQuantity();
//    int posCurr = currPosition.getQuantity();
    double posCurr = currPosition.getQuantity();
//    int posTotal = posCurr+getTotalOutstandingPosition(); // posCurr + posOpenOrders
    double posTotal = posCurr+getTotalOutstandingPosition(); // posCurr + posOpenOrders

//    int num2Trade = pos2b - posTotal;
    double num2Trade = pos2b - posTotal;
    if (Math.abs(num2Trade)>0) {
      System.out.println("PortfolioManager.TRADE2: To trade:"+
              " num: " + num2Trade +
              " tobe: " + pos2b +
              " total: " + posTotal);
    } else {
      System.out.println("PortfolioManager.TRADE2: NO TRADE");
    }
    System.out.flush();

    if (Math.abs((long) posTotal)<Math.abs((long) pos2b) && Math.abs(num2Trade)>0) {

      System.out.println("PortfolioManager.TRADE: TRIGGER enter ");
      System.out.flush();

      if (getRemainedOrdersPosition() == 0) {

        String action = "";

        if (num2Trade > 0) {
          action = "BUY";
        }
        if (num2Trade < 0) {
          action = "SELL";
        }

//        int totalQuantity = 0;
        double totalQuantity = 0;
        if (Math.abs(num2Trade)<1e-8) {
          totalQuantity = Math.abs(num);
        } else {
          totalQuantity = Math.abs(num2Trade);
        }

        microStrat.MICRO_TRADE(mFinSec, action, price, totalQuantity);

        if(mFinSec.getTableIndex()>=0 & !Dispatcher.getDispatcher().mNoGUI) {
          PortfolioMonitorTableModel model =
                  (PortfolioMonitorTableModel)Dispatcher.getDispatcher().mGUI.
                  getPortfolioMonitorTable().getModel();

          model.setPosition(mFinSec.getTableIndex(), pos2b);
          if(pos2b!=0) {
            model.setOrderBuySell(mFinSec.getTableIndex(), action);
            model.setPurchasePrice(mFinSec.getTableIndex(), price);
          } else {
            model.setOrderBuySell(mFinSec.getTableIndex(), "");
            model.setPurchasePrice(mFinSec.getTableIndex(), 0);
          }
        }

      }

    }

  }

// provides reversal trading
  public void TRADE(double num, double price, MicroStrategy microStrat) {
    
    getTobePosition().setQuantity(num);

//    int pos2b = tobePosition.getQuantity();
    double pos2b = tobePosition.getQuantity();
//    int posCurr = currPosition.getQuantity();
    double posCurr = currPosition.getQuantity();
//    int posTotal = posCurr+getTotalOutstandingPosition(); // posCurr + posOpenOrders
    double posTotal = posCurr+getTotalOutstandingPosition(); // posCurr + posOpenOrders

//    int num2Trade = pos2b - posTotal;
    double num2Trade = pos2b - posTotal;
    if (Math.abs(num2Trade)>1e-8) {
      System.out.println("PortfolioManager.TRADE: To trade: num: " + num2Trade + " tobe: "
            + pos2b + " total: " + posTotal);
    } else {
      System.out.println("PortfolioManager.TRADE: NO TRADE");
    }
    System.out.flush();

    if (((long) posTotal) * ((long) pos2b) <= 0 && num2Trade != 0) {

      System.out.println("PortfolioManager.TRADE: TRIGGER enter ");
      System.out.flush();

      if (getRemainedOrdersPosition() == 0) {

        String action = "";

        if (num2Trade > 0) {
          action = "BUY";
        }
        if (num2Trade < 0) {
          action = "SELL";
        }

//        int totalQuantity = 0;
        double totalQuantity = 0;
        if (Math.abs(num2Trade)<1e-8) {
          totalQuantity = Math.abs(num);
        } else {
          totalQuantity = Math.abs(num2Trade);
        }

        microStrat.MICRO_TRADE(mFinSec, action, price, totalQuantity);

        if(mFinSec.getTableIndex()>=0 & !Dispatcher.getDispatcher().mNoGUI) {

          PortfolioMonitorTableModel model =
                  (PortfolioMonitorTableModel)Dispatcher.getDispatcher().mGUI.
                  getPortfolioMonitorTable().getModel();

          model.setPosition(mFinSec.getTableIndex(), pos2b);
          if(pos2b!=0) {
            model.setOrderBuySell(mFinSec.getTableIndex(), action);
            model.setPurchasePrice(mFinSec.getTableIndex(), price);
          } else {
            model.setOrderBuySell(mFinSec.getTableIndex(), "");
            model.setPurchasePrice(mFinSec.getTableIndex(), 0);
          }
        }

      }

    }

  }

}
