/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IBLink;
import com.ib.client.*;
import java.util.*;

/**
 *
 * @author nik
 */
public class ContractRegister {
  static private Hashtable<Integer,Contract> mContractTable=null;
  static private Hashtable<Contract,Integer> mContractIDTable=null;
  
  static private synchronized Hashtable<Integer,Contract> getContractTable() {
//    synchronized(mPositionHistory) {
      if (mContractTable == null) {
  //      mPositionHistory = Collections.synchronizedList(new ArrayList<Position>());
        mContractTable = new Hashtable<Integer,Contract>();
      }
      return mContractTable;
//    }
  }  // Order Map and List
  static private synchronized Hashtable<Contract,Integer> getContractIDTable() {
//    synchronized(mPositionHistory) {
      if (mContractIDTable == null) {
  //      mPositionHistory = Collections.synchronizedList(new ArrayList<Position>());
        mContractIDTable = new Hashtable<Contract,Integer>();
      }
      return mContractIDTable;
//    }
  }  // Order Map and List

  static public void addContract(int contractID, Contract contract) {
    getContractTable().put(contractID, contract);
    getContractIDTable().put(contract, contractID);
  }
  static public synchronized Contract getContract(int contractID) {
    return getContractTable().get(contractID);
  }
  static public synchronized Integer getContractID(Contract contract) {
    return getContractIDTable().get(contract);
  }
  
}
