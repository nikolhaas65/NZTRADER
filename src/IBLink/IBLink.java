/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package IBLink;

import OrderManager.GlobalOrderRegister;
import AlgoTrader.Dispatcher;
import AlgoTrader.PortfolioMonitorTableModel;
import AlgoTrader.OptionsMonitorTableModel;
import Strategies.*;
import InstrumentManager.*;
import OrderManager.OrderManager;
import OrderManager.JOrder;
import com.ib.client.*;
import Utils.*;
import ReaderLink.*;
import TimeSeries.BarSeries.FeedType;
import java.util.Date;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author nik
 */
public class IBLink extends EWrapperAdapter {

  private static Dispatcher mDisp = Dispatcher.getDispatcher();

  private static NumberRenderer fd = new NumberRenderer(5,5);
  private static boolean isConnectedStatus = false;

  private static long lastConnStatusTime = System.currentTimeMillis();
  private static long startUpTime = System.currentTimeMillis();;

  private static String mAccountName = "simtr";

  public synchronized static void setConnectionStatus(boolean status) {
    isConnectedStatus = status;
    if(status) {
      mDisp.mGUI.setStatusToIB_Control(System.currentTimeMillis()-lastConnStatusTime, 10, 60);
      lastConnStatusTime = System.currentTimeMillis();
    }
  }

  public synchronized static void setConnectionStatus(long time, boolean status) {
    lastConnStatusTime = time;
    isConnectedStatus = status;
    mDisp.mGUI.setStatusToIB_Control(System.currentTimeMillis()-lastConnStatusTime, 10, 60);
  }

  static long mCurrentTime=0;
  static long mDayTime=0;

  @Override
  public void currentTime(long time) {
    mCurrentTime =time;
    this.setConnectionStatus(time, true);
    this.currentIBLinkTime(time);
  }

  public void currentIBLinkTime(long time) {
    mCurrentTime =time;
  }
  public void currentIBLinkTime(Date time) {
    mCurrentTime =time.getTime();
    mDayTime =Utils.TimeUtils.getDayTime(time);
  }

  public static String ticktag_demo =
          "100,101,104,105,106,107,165,221,225,232,233,236,258";
//        "100,101,104,105,106,107,165,221,225,233,236,258,293,294,295,318"
  public static String ticktag = 
          "100,101,104,106,162,165,221,225,236";

  public static long IB_CurrTime = 0;

//  private static boolean mRTFeed = mDisp.mRTFeed;
  private static ReaderLink mRLink = null;

  private static IBLink mIBLink = null;

  public static IBLink getIBLink() {
    if (mIBLink == null) {
      mIBLink = new IBLink();
    }
    return mIBLink;
  }

  private static final int NOT_AN_FA_ACCOUNT_ERROR = 321;
  private int faErrorCodes[] = {503, 504, 505, 522, 1100, NOT_AN_FA_ACCOUNT_ERROR};
  private static boolean faError;
  private static EJavaSignal ers = new EJavaSignal();
  private static EClientSocket m_client =
          new EClientSocket(IBLink.getIBLink(), ers);
  private static ClientMultiplexor mMultiClient =
          new ClientMultiplexor(IBLink.getIBLink());

  public static class ClientMultiplexor {
    private IBLink iblink = null;
    public ClientMultiplexor(IBLink link) {
      iblink = link;
    }
    public void placeOrder(int orderID , Contract contract , Order order) {
      if(mDisp.mRTFeed) {
        m_client.placeOrder(orderID, contract, order);
      } else {
        int contractID = ContractRegister.getContractID(contract);
        mRLink.placeOrder(orderID,contractID,order);
      }
    };
    public void cancelOrder(int orderID) {
      if(mDisp.mRTFeed) {
        m_client.cancelOrder(orderID);
      } else {
        mRLink.cancelOrder(orderID);
      }
    };

    public boolean isConnected() {
//      System.out.println("Multi status " + isConnectedStatus + " rtfeed: " + mRTFeed);
      if(mDisp.mRTFeed) {
        return m_client.isConnected();
      }
      return isConnectedStatus;
    }
  }

  public static ClientMultiplexor getClient() {
    return mMultiClient;
  }

  public static double getTimeElapsed() {
    return (double) ((System.currentTimeMillis() - startUpTime)/1000);
  }

  @Override
  public void error(Exception e) {
    if(e!=null) {
      try{
        System.out.println("Err1 : x " + e.toString());
      } catch(Exception exx) {
        System.out.println("Err/Err1 : " + exx.toString());
      }
      System.out.flush();
    }
  }

  @Override
  public void error(String error) {
    System.out.println("Err2: " + error);
    System.out.flush();
  }

  @Override
  public void error(int orderId, int errorCode, String errorMsg) {
    System.out.println(Utils.TimeUtils.Time2String(2,System.currentTimeMillis()) + 
            " Err3: " + orderId + " : " + errorCode + " : " + errorMsg);
    System.out.flush();
    if (errorCode == 1100 || errorCode == 504 || errorCode == 2103) {// Connectivity between IB and TWS has been lost.
        this.setConnectionStatus(false);
    }

    boolean isConnectivityRestored = (errorCode == 1101 || errorCode == 1102 ||
            errorCode == 501 || errorCode == 2104 || errorCode == 2106);

    if (isConnectivityRestored) {
        System.out.println("Checking for executions while TWS was disconnected from the IB server.");
//        traderAssistant.requestExecutions();
        this.setConnectionStatus(System.currentTimeMillis(),true);
        m_client.reqAllOpenOrders();
    }

    if(errorCode == 103) { // duplicate order id

    } else if(errorCode == 104) { //Can't modify a filled order
      //try to remove the order
      if(!GlobalOrderRegister.orderIsRemoved(orderId)) {
        try {
          FinSecurity fs = GlobalOrderRegister.getFinSecurity(orderId);
          OrderManager pm = fs.getOrderManager();
          if(pm.orderExist(orderId)) {
            JOrder jorder = pm.getJOrder(orderId);
            if(jorder!=null) {
              jorder.setStatus(JOrder.Status.Cancelled);
              pm.rebookOrder(jorder,fs.getDataFeed().getLastUpdateTime());
              pm.removeOrder(orderId);
            }
          }
        } catch(Exception ex) {
          System.out.println("Err3: msg= " + ex);
        }
      }
    }
//    if (errorCode == 317) {// Market depth data has been reset
//        traderAssistant.getStrategy(id).getMarketBook().reset();
//        eventReport.report("Market depth data has been reset.");
//    }
  }

  @Override

  public void orderStatus(int orderId, String status,
          double filled, double remaining, double avgFillPrice,
          int permId, int parentId, double lastFillPrice,
          int clientId, String whyHeld, double mktCapPrice) {
    System.out.println("IBLink.orderStatus: #" + orderId + " " + status +
            "(" + filled + "," + remaining + "," + avgFillPrice + "," + lastFillPrice + ")");
    System.out.flush();
    this.setConnectionStatus(true);
    if(!GlobalOrderRegister.orderIsRemoved(orderId)) {
      try {
        FinSecurity fs = GlobalOrderRegister.getFinSecurity(orderId);
        OrderManager pm = fs.getOrderManager();
        if(pm.orderExist(orderId)) {
          JOrder jorder = pm.getJOrder(orderId);
          if(jorder!=null) {
            if(status.equalsIgnoreCase("PendingSubmit")) {
              jorder.setStatus(JOrder.Status.PendingSubmit);
            } else if(status.equalsIgnoreCase("PendingCancel")) {
              jorder.setStatus(JOrder.Status.PendingCancel);
            } else if(status.equalsIgnoreCase("PreSubmitted")) {
              jorder.setStatus(JOrder.Status.PreSubmitted);
            } else if(status.equalsIgnoreCase("Cancelled")) {
              jorder.setStatus(JOrder.Status.Cancelled);
              System.out.println("IBLink.orderStatus: order Cancelled: # " + orderId);
              if(filled>0) {
                jorder.update(filled, avgFillPrice);
                System.out.println("IBLink.orderStatus:  Order# " + orderId + " updated x " + jorder.toString());
              }
              pm.rebookOrder(jorder,fs.getDataFeed().getLastUpdateTime());
              pm.removeOrder(orderId);
            } else if(status.equalsIgnoreCase("Inactive")) {
              jorder.setStatus(JOrder.Status.Inactive);
              System.out.println("IBLink.orderStatus: order Inactive: # " + orderId);
              if(filled>0) {
                jorder.update(filled, avgFillPrice);
                System.out.println("IBLink.orderStatus:  Order# " + orderId + " updated x " + jorder.toString());
              }
            } else if(status.equalsIgnoreCase("Submitted")) {
              jorder.setStatus(JOrder.Status.Submitted);
              System.out.println("IBLink.orderStatus: order update: # " + orderId);
              jorder.update(filled, avgFillPrice);
              System.out.println("IBLink.orderStatus:  Order# " + orderId + " updated x " + jorder.toString());
              if(jorder.isFilled(status)) {
                jorder.setStatus(JOrder.Status.Filled);
                pm.rebookOrder(jorder,fs.getDataFeed().getLastUpdateTime());
                pm.removeOrder(orderId);
              }
            } else if(status.equalsIgnoreCase("Filled")) {
              jorder.setStatus(JOrder.Status.Filled);
              System.out.println("IBLink.orderStatus: order update: # " + orderId);
              jorder.update(filled, avgFillPrice);
              System.out.println("IBLink.orderStatus:  Order# " + orderId + " updated x " + jorder.toString());
              if(jorder.isFilled(status)) {
                jorder.setStatus(JOrder.Status.Filled);
                pm.rebookOrder(jorder,fs.getDataFeed().getLastUpdateTime());
                pm.removeOrder(orderId);
              }
            } else {
              System.out.println("IBLink.orderStatus: UNKNOWN STATUS: # " + orderId);
              System.out.flush();
            }
          } else {
            System.out.println("IBLink.orderStatus: jorder is null ID: " + orderId);
            System.out.flush();
          }
        } else {
          System.out.println("IBLink.orderStatus:NO ORDER REGISTERED with ID: " + orderId);
          System.out.flush();
        }
      } catch(Exception ex) {
        System.out.println("IBLink.orderStatus: failed: " + orderId + " " + ex.getMessage());
        System.out.flush();
      }
    }
  }
  
  @Override
  public void execDetails(int orderId, Contract contract, Execution execution) {
    System.out.println("IBLink.execDetails: #" + orderId + " " +
            execution.shares() + " @ " + execution.price());
    System.out.flush();
    try {
      OrderManager pm =
              GlobalOrderRegister.getFinSecurity(orderId).getOrderManager();
      if(pm.orderExist(orderId)) {
        JOrder jorder = pm.getJOrder(orderId);
        jorder.add(execution);
        System.out.println("IBLink:execDetails:JOrder:add avePrice: " + jorder.mAvePrice);
      }
    } catch(Exception ex) {
      System.out.println("IBLink.execDetails: orderID: " + orderId + " " + ex.getMessage());
      System.out.flush();
    }
    this.setConnectionStatus(true);
  }
  
  public void cancel(int orderId) {
    getClient().cancelOrder(orderId);    
    System.out.println("IBLink.cancel: Cancel Order ID " + orderId);
    System.out.flush();
  }
  
  public String getField(int field) {
    switch(field) {
      case 0: return "BidSz";
      case 1: return "Bid";
      case 2: return "Ask";
      case 3: return "AskSz";
      case 4: return "Trade";
      case 5: return "TrdSz";
    }
    return "def:"+field;
  }

// public void tickPrice(int i, int i1, double d, TickAttr ta) {}

  @Override
  public void tickPrice(int tickerId, int field, double price, TickAttr tickattr) {
//    System.out.println("TickerID1: " + tickerId + " field: " + field);System.out.flush();
    try {
      FinSecurity finsec = InstrumentManager.getFinSecurity(tickerId);
      DataFeed dfeed=finsec.getDataFeed();
//      System.out.println("IBLink:tickPrice: TickerID: " + tickerId + " feed: " + dfeed +
//              " tblIndx: " + finsec.getTableIndex() + " " + finsec.getName());
      if(mDisp.mRTFeed) this.currentTime(System.currentTimeMillis());
      boolean strat_fire = true;
      if(dfeed!=null) {
        switch(field) {
          case 1:
            {
//              bid
              dfeed.setBid(price);
              dfeed.setBidTime(this.mCurrentTime);
              if(finsec.getTableIndex()>=0) {
                if(!finsec.isOption()) {
                  ((PortfolioMonitorTableModel)mDisp.mGUI.
                          getPortfolioMonitorTable().getModel()).setBid(finsec.getTableIndex(), price);
                } else {
                  ((OptionsMonitorTableModel)mDisp.mGUI.
                          getOptionsMonitorTable().getModel()).setBid(finsec.getTableIndex(), price);
                }
              }
              if(this.mDisp.mRTFeed) finsec.recordBid(1);
              dfeed.trade_dir = 0;
            };
            break;
          case 2:
            {
//              ask:
              dfeed.setAsk(price);
              dfeed.setAskTime(this.mCurrentTime);
              if(finsec.getTableIndex()>=0) {
                if(!finsec.isOption()) {
                  ((PortfolioMonitorTableModel)mDisp.mGUI.
                          getPortfolioMonitorTable().getModel()).setAsk(finsec.getTableIndex(), price);
                } else {
                  ((OptionsMonitorTableModel)mDisp.mGUI.
                          getOptionsMonitorTable().getModel()).setAsk(finsec.getTableIndex(), price);
                }
              }
              if(this.mDisp.mRTFeed) finsec.recordAsk(2);
              dfeed.trade_dir = 0;
            };
            break;
          case 4:
            {
//              price - trade
              dfeed.setLast(price);
              dfeed.setLastTime(this.mCurrentTime);
              if(finsec.getTableIndex()>=0) {
                if(!finsec.isOption()) {
                  ((PortfolioMonitorTableModel)mDisp.mGUI.
                          getPortfolioMonitorTable().getModel()).setLast(finsec.getTableIndex(), price);
                } else {
                  ((OptionsMonitorTableModel)mDisp.mGUI.
                          getOptionsMonitorTable().getModel()).setLast(finsec.getTableIndex(), price);
                }
              }
              if(this.mDisp.mRTFeed) finsec.recordLast(4);
              dfeed.trade_dir = (price+price - (dfeed.ask+dfeed.bid))/(dfeed.ask-dfeed.bid);
            };
            break;
          default:
            strat_fire=false;
//            System.out.println("IBLink:tickPrice: No tick type is foreseen " + field);
        }
//        System.out.println("IBLink:tickPrice:field filled");
// for every updates of bids and asks update spread
        dfeed.updateSprMid();

//        if(dfeed.changeBidAsk()) {
//          System.out.print("---> IBLink.tickPrice: Tkr#" + tickerId + " " +
//                  TimeUtils.Time2String(2,dfeed.getLastUpdateTime()) +
//                  ": Price: " + getField(field) + " " + fd.format(price));
//          System.out.println(" df: " + dfeed.toString());
//          System.out.flush();
//        } else if(field==4) {
//          String action = dfeed.trade_dir >0.3 ? " Last BUY " : dfeed.trade_dir <-0.3 ? " Last SELL" : "";
//          System.out.print("xxx> IBLink.tickPrice: Tkr#" + tickerId + ": " +
//                  getField(field) + " " + fd.format(price) + action);
//          System.out.println(" df: " + dfeed.toString());
//          System.out.flush();
//        }

//        System.out.println("Strats Fire " + finsec.ListStrategies() + "  TickerID" + tickerId);

        if(price>0 && field<=5 && strat_fire) finsec.FireStrategies();

//        System.out.println("StratFired TickerID: " + tickerId);

      } else {
        System.out.println("IBLink.tickPrice: dfeed is null tickerID: " + tickerId);
      }
    } catch(Exception ex) {
      System.out.println("Exception: IBLink:tickPrice " + tickerId + " " + ex);
      System.out.flush();
    }
    this.setConnectionStatus(true);
  }

  @Override
  public void tickSize(int tickerId, int field, int size) {
//    System.out.println(tickerId + " Size:  " + getField(field) + " " + size);
    if(mDisp.mRTFeed) this.currentTime(System.currentTimeMillis());
    this.setConnectionStatus(true);
    try {
      FinSecurity finsec = InstrumentManager.getFinSecurity(tickerId);
      DataFeed dfeed=finsec.getDataFeed();
      boolean strat_fire=true;
      if(dfeed!=null) {
        switch(field) {
          case 0:
            {
              if(dfeed.bid_sz==size) return;
              dfeed.setBidSz(size);
              dfeed.setBidTime(this.mCurrentTime);
              if(finsec.getTableIndex()>=0) {
                if(!finsec.isOption()) {
                  ((PortfolioMonitorTableModel)mDisp.mGUI.
                          getPortfolioMonitorTable().getModel()).setBidSz(finsec.getTableIndex(), size);
                } else {
                  ((OptionsMonitorTableModel)mDisp.mGUI.
                          getOptionsMonitorTable().getModel()).setBidSz(finsec.getTableIndex(), size);
                }
              }
              if(this.mDisp.mRTFeed) finsec.recordBid(0);
            };
            break;
          case 3:
            {
              if(dfeed.ask_sz==size) return;
              dfeed.setAskSz(size);
              dfeed.setAskTime(this.mCurrentTime);
              if(finsec.getTableIndex()>=0) {
                if(!finsec.isOption()) {
                  ((PortfolioMonitorTableModel)mDisp.mGUI.
                          getPortfolioMonitorTable().getModel()).setAskSz(finsec.getTableIndex(), size);
                } else {
                  ((OptionsMonitorTableModel)mDisp.mGUI.
                          getOptionsMonitorTable().getModel()).setAskSz(finsec.getTableIndex(), size);
                }
              }
              if(this.mDisp.mRTFeed) finsec.recordAsk(3);
            }
            break;
          case 5:
            {
              if(dfeed.last_sz==size) return;
              dfeed.setLastSize(size);
              dfeed.setLastTime(this.mCurrentTime);
              if(finsec.getTableIndex()>=0) {
                if(!finsec.isOption()) {
                  ((PortfolioMonitorTableModel)mDisp.mGUI.
                          getPortfolioMonitorTable().getModel()).setLastSz(finsec.getTableIndex(), size);
                } else {
                  ((OptionsMonitorTableModel)mDisp.mGUI.
                          getOptionsMonitorTable().getModel()).setLastSz(finsec.getTableIndex(), size);
                }
              }
              if(this.mDisp.mRTFeed) finsec.recordLast(5);
            }
            break;
          default:
            strat_fire=false;
//            System.out.println("IBLink:tickSize: No tick type is foreseen " + field);
        }
//        System.out.print("---> IBLink.tickSize: Tkr#" + tickerId + " " +
//                TimeUtils.Time2String(2,dfeed.getLastUpdateTime()) +
//                ": " + getField(field) + " " + size);
//        System.out.println(" " + dfeed.toString());
//        System.out.flush();

        if(size>0 && field<=5 && strat_fire) finsec.FireStrategies();

      }
    } catch(Exception ex) {
      System.out.println("IBLink:tickSize " + tickerId + " " + ex);
      System.out.flush();
    }
  }

  static public int mNextOrderId=0;
  @Override
  public void nextValidId(int orderId) {
    mNextOrderId=orderId;
    this.setConnectionStatus(true);
  }

  public static void Connect(boolean rtfeed) {
//    mRTFeed = rtfeed;
    if(rtfeed) {
      Runnable r = new Runnable() {
        public void run() {
          System.out.println("IB connect");System.out.flush();
          isConnectedStatus = false;
          m_client.eConnect("", 7497, 0);
          System.out.println("IB connection passed. Check connection");
          System.out.flush();
          if (IBLink.getClient().isConnected()) {
            System.out.println("Connected to Tws server version " +
                    m_client.serverVersion() + " at " +
                    m_client.getTwsConnectionTime());
            System.out.println("NextOrderId" + mNextOrderId);
            System.out.flush();
            IBLink.getIBLink().setConnectionStatus(true);
            m_client.reqAccountUpdates(true, mAccountName);
                System.out.flush();
          } else {
            System.out.println("Not yet connected"); System.out.flush();
          }
          try {Thread.sleep(2000);} catch (Exception ex) {};
        }
      };

      Thread thr = new Thread(r, "IBLink");
      thr.setPriority(Thread.MAX_PRIORITY - 2);
      thr.setDaemon(true);
      thr.start();
    } else {
      System.out.println("Reader start");
      mRLink = new ReaderLink(getIBLink());
      System.out.println("Reader started");
      IBLink.getIBLink().setConnectionStatus(true);
    }
  }

  public static void Disconnect() {
    System.out.println("Disconnect");
    m_client.eDisconnect();
  }

  public static Contract mkCurrContract(String symb,String type, String exch, String curr) {
    Contract contract = new Contract();
    contract.symbol(symb);
    contract.secType(type);
    contract.exchange(exch);
    contract.currency(curr);
    return contract;
  }

  public static Contract mkFutContract(String symb,
          String exch, String expiry, String curr) {
    Contract contract = new Contract();
    contract.symbol(symb);
    contract.lastTradeDateOrContractMonth(expiry);
    contract.secType("FUT");
    contract.exchange(exch);
    contract.currency(curr);
    return contract;
  }

  public static Contract mkOptContract(String symb,
          String exch, String expiry, String curr, double strike, String opt_type) {
    System.out.println("MkOption: " + symb + " :xp " + expiry + " :xc " + exch + " :cr " + curr + " :st " + strike);
    Contract contract = new Contract();
    contract.symbol(symb);
    contract.lastTradeDateOrContractMonth(expiry);
    contract.secType("OPT");
    contract.exchange(exch);
    contract.primaryExch(exch);
    contract.currency(curr);
    contract.strike(strike);
    contract.right(opt_type);
    return contract;
  }

  private static java.util.List<TagValue> m_mktDataOptions = new ArrayList<TagValue>();

  public static void reqMktDataIB(int tickerID, Contract contract, String ttag) {
    System.out.println(tickerID + " : " + contract.secType() + " : "+ contract.symbol() + " : " + contract.currency());
    m_client.reqMktData(tickerID, contract, ttag, faError, false, m_mktDataOptions);
    
//    m_client.reqMktData( m_orderDlg.id(), m_orderDlg.contract(),
//                         m_orderDlg.m_genericTicks, 
//                         m_orderDlg.m_snapshotMktData, 
//                         m_orderDlg.m_reqSnapshotMktData, 
//                         m_mktDataOptions);

    System.out.println("MktRequested");
  }
//  public synchronized void reqMktData(int i, Contract cntrct, String string, boolean bln, 
//  boolean bln1, java.util.List<TagValue> list) {

  static public void startTrading(String stk_name,
          String type, String mrkt, String curr, double tickSZ,int limit) {

    String finsec_name_reg = stk_name + "." + type;

    Contract cont=null;
    if(type.equalsIgnoreCase("STK")) {
      cont = IBLink.mkCurrContract(stk_name, type, mrkt, curr);
    } else if(type.equalsIgnoreCase("CASH")) {
      cont = IBLink.mkCurrContract(stk_name, type, mrkt, curr);
      finsec_name_reg = stk_name + curr + "." + type;
    } else {
      cont = IBLink.mkFutContract(stk_name, type, mrkt, curr);
    }

    int contractID = InstrumentManager.registerID(finsec_name_reg);

    ContractRegister.addContract(contractID, cont);
    System.out.println("MAIN: " + finsec_name_reg + " contractID: " + contractID);

    FinSecurity finsec = InstrumentManager.getFinSecurity(contractID);

    finsec.getDataFeed().tick_size = tickSZ;

    MA_Strategy runner = new MA_Strategy(TimeSeries.BarSeries.FeedType.RealTime);
    runner.setTradingTime( TimeUtils.String2Time(6,"090500"),TimeUtils.String2Time(6,"171500"));
    runner.setDataFeedTime(TimeUtils.String2Time(6,"090000"),TimeUtils.String2Time(6,"173000"));

    finsec.setStrategy(runner);
    runner.setSecName(finsec_name_reg);
    runner.setContractID(contractID);
    runner.setLimit(limit);

    if(mDisp.mRTFeed) {
      IBLink.reqMktDataIB(contractID, cont, ticktag_demo);
    } else {
      mRLink.startReadingData(stk_name,contractID);
    }

  }

  enum switchStrategy {TRADE_LIMIT,FUT_BARRED,FUT_BARRED_NEG,FUT_DIRECT_OPT};

  static public void startTradingViaGUI(String finsec_name, String symb,
        String type, String mrkt, String curr, String expiry, double strike, String opt_type, double tickSZ,int limit) {

    Contract cont=null;
    if(type.equalsIgnoreCase("STK")) {
      cont = IBLink.mkCurrContract(symb, type, mrkt, curr);
    } else if(type.equalsIgnoreCase("CASH")) {
      cont = IBLink.mkCurrContract(symb, type, mrkt, curr);
    } else if(type.equalsIgnoreCase("OPT")) {
      cont = IBLink.mkOptContract(symb, mrkt, expiry, curr, strike,opt_type);
    } else {
      cont = IBLink.mkFutContract(symb, mrkt, expiry, curr);
    }

    System.out.println("startTradingViaGUI: " + symb + ", " +
            type + ", " + mrkt + ", " + curr);

    int contractID = InstrumentManager.registerID(finsec_name);

    ContractRegister.addContract(contractID, cont);
    System.out.println("MAIN: " + finsec_name + " contractID: " + contractID);

    FinSecurity finsec = InstrumentManager.getFinSecurity(contractID);
    finsec.getDataFeed().tick_size = tickSZ;

    MacroStrategy runner = null;
    
    switchStrategy strategy_switch = switchStrategy.FUT_BARRED;
//    switchStrategy strategy_switch = switchStrategy.FUT_DIRECT_OPT;
    switch(strategy_switch) { 
      case TRADE_LIMIT:
        int dir=1;
        if(finsec_name.contains("ASK")) dir=-1;
        runner = new Trade_at_LIMIT_Strategy(dir);
        break;
      case FUT_DIRECT_OPT:
        runner = new FutDirect_Opt();
        break;
      case FUT_BARRED:
        runner = new FutBarred_Strategy();
        break;
      case FUT_BARRED_NEG:
        runner = new FutBarredNeg_Strategy();
        break;
      default:

    }

//    MacroStrategy runner = new MA_Strategy();

    if(runner!=null) {
      if(mDisp.mRTFeed) {
        runner.setFeedType(FeedType.RealTime);
      } else {
        runner.setFeedType(FeedType.FeedReader);
      }

      finsec.setStrategy(runner);

      System.out.println("SetStrategy params: name: " + finsec_name + " cID: " + contractID + " FinSec: " + finsec + " Lim: " + limit);
      runner.setSecName(finsec_name);
      runner.setContractID(contractID);
      runner.setFinSecurity(finsec);
      runner.setLimit(limit);
      runner.Init();
    }

    StrategyManager.setStrategy(runner);

    if(mDisp.mRTFeed) {
      IBLink.reqMktDataIB(contractID, cont, ticktag_demo);
    } else {
      System.out.println("Start reading data for " + symb);
      mRLink.startReadingData(symb,contractID);
    }

  }

  public static void main(String[] args) {
    
    System.out.println("START IB_Test2.IBLink.main");

    boolean RealTimeFeed = false;

    IBLink.Connect(RealTimeFeed);

    while(!IBLink.getClient().isConnected() || IBLink.mNextOrderId==0) {
//      System.out.println("Connected: " + IBLink.getClient().isConnected() + " OrdID: " + IBLink.mNextOrderId);
      try {Thread.sleep(10);} catch (Exception ex) {};
    }

    if(IBLink.getClient().isConnected()) {
      System.out.println("MAIN: Connected");
      System.out.println("MAIN:NEXT Order ID: " + IBLink.mNextOrderId);      
    } else {
      System.out.println("MAIN: Not Connected");
    }

    if(RealTimeFeed) {
  //    IBLink.startTrading("EUR","CASH","IDEALPRO","USD",0.00005d,20000);
  //    IBLink.startTrading("EUR","CASH","IDEALPRO","CHF",0.00005d,20000);
  //    IBLink.startTrading("CHF","CASH","IDEALPRO","JPY",0.005d,20000);
  //    IBLink.startTrading("USD","CASH","IDEALPRO","CHF",0.005d,20000);
  //    IBLink.startTrading("EOE","FTA","20100416","EUR",0.05d,10);

      IBLink.startTrading("AAPL","STK","SMART","USD",0.01d,1000);
      IBLink.startTrading("INTC","STK","SMART","USD",0.01d,1000);
    } else {
      IBLink.startTrading("RDSA NA Equity","STK","SMART","USD",0.01d,1000);
//      IBLink.startTrading("INGA NA Equity","STK","SMART","USD",0.01d,1000);
    }

//    StrategyManager.InitAllStrategies();
    System.out.println("--------- START TRADING ---------");    System.out.flush();

    while (true) {
      try {Thread.sleep(10);} catch (Exception ex) {};
    }

  }
}
