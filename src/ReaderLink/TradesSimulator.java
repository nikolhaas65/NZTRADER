/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ReaderLink;

import java.util.*;


/**
 *
 * @author nik
 */
public class TradesSimulator {
  private static Map<Integer,ContractBook> mContractBooks = null;

  private static synchronized Map<Integer,ContractBook> getContractBookMap() {
    if(mContractBooks==null) {
      mContractBooks = new HashMap<Integer,ContractBook>();
    }
    return mContractBooks;
  }

  public static ContractBook getBook(int contractID) {
    Map<Integer,ContractBook> map = getContractBookMap();
    if(!map.containsKey(contractID)) {
      map.put(contractID, new ContractBook(contractID,0));
    }
    return map.get(contractID);
  }

}
