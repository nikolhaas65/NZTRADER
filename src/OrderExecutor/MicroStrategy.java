/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package OrderExecutor;
import InstrumentManager.*;
import OrderManager.*;
import com.ib.client.*;
import IBLink.*;

/**
 *
 * @author nik
 */
public abstract class MicroStrategy {
  public abstract String getOrderType();
  public abstract void MICRO_TRADE(FinSecurity mFinSec,
          String action,double price,double totalQuantity);
//  public abstract void modify(int contractID, JOrder jorder, double price);
  public abstract void checkOrder(FinSecurity finsec,OrderManager pm,JOrder jorder);
  public synchronized void modify(int contractID, JOrder jorder, double price) {

    jorder.setPrice(price);
    Order order = jorder.getOrder();
    Contract contract = ContractRegister.getContract(contractID);
    System.out.println("Modify price: "+ "ID "+order.orderId()+ " " + order.lmtPrice() +
            " Contract: " + contractID + " : " + contract.symbol() + " " + jorder.mStatus);
    IBLink.getClient().placeOrder(order.orderId(), contract, order);
  }

}
