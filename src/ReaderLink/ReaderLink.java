/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ReaderLink;

import OrderManager.GlobalOrderRegister;
import IBLink.*;
import java.util.*;
import InstrumentManager.*;
import com.ib.client.*;

/**
 *
 * @author nik
 */
public class ReaderLink {

  static List<FeedReader> mListReaders = Collections.synchronizedList(new LinkedList<FeedReader>());

  private static int mOrderID = -1;

  private static IBLink mIBLink = null;
  public ReaderLink(IBLink iblink) {
    mIBLink  = iblink;
    if(mOrderID<0) {
      mOrderID = 1;
      mIBLink.nextValidId(mOrderID);
   }
  }

  public void placeOrder(int orderID,int contractID, Order order) {
    System.out.println("ReaderLink.placeOrder: order# " + orderID + " contract# " + contractID);
    TradesSimulator.getBook(contractID).putOrder(orderID, order);
  }

  public void cancelOrder(int orderID) {
    int contractID = GlobalOrderRegister.getFinSecurity(orderID).getContractID();
    TradesSimulator.getBook(contractID).removeOrder(orderID);
  }

  public static void startReadingData(String name, int contractID) {
    TradesSimulator tf = new TradesSimulator();
    FeedReader fr = null;
    if(name.contains("TickData")) {
      fr = new FeedReader("D:\\data\\" + name + ".dat",contractID,tf);
    } else {
      fr = new FeedReader("D:\\data\\" + name + ".txt",contractID,tf);
    }
    mListReaders.add(fr);
    fr.start();
  }
}
