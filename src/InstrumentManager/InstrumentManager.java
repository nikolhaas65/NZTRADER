/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package InstrumentManager;
import java.util.*;

/**
 *
 * @author nik
 */
public class InstrumentManager {

  // STATIC PART - TO REGISTER CONTRACTS UNIQUELY BY NAME
  static private Hashtable<String,FinSecurity> mFinSecurityTable=null;
  static private Hashtable<Integer,FinSecurity> mFinSecurityTablebyInteger=null;
  static private List<FinSecurity> mFinSecList=null;
  
  static private synchronized List<FinSecurity> getFinSecurityList() {
      if (mFinSecList == null) {
        mFinSecList = new ArrayList<FinSecurity>();
      }
      return mFinSecList;
  }  // Order Map and List
  static private synchronized Hashtable<String,FinSecurity> getFinSecurityTable() {
      if (mFinSecurityTable == null) {
        mFinSecurityTable = new Hashtable<String,FinSecurity>();
      }
      return mFinSecurityTable;
  }  // Order Map and List
  
  static private synchronized Hashtable<Integer,FinSecurity> getFinSecurityTablebyInteger() {
      if (mFinSecurityTablebyInteger == null) {
        mFinSecurityTablebyInteger = new Hashtable<Integer,FinSecurity>();
      }
      return mFinSecurityTablebyInteger;
  }  // Order Map and List
  
  static private void setFinSecurityNameAndID(String name, Integer id) {
    Hashtable<String,FinSecurity> table = getFinSecurityTable();
    FinSecurity finsec = new FinSecurity(name,id);
    
    if(!table.containsKey(name)) {
      
      getFinSecurityTable().put(name, finsec);
      
      Hashtable<Integer,FinSecurity> table_int = getFinSecurityTablebyInteger();
      table_int.put(id, finsec);
      
      getFinSecurityList().add(finsec);
      
    }
    
  }
  
  static public Iterator<FinSecurity> getALLFinSecurities() {
//    return (FinSecurity[])getFinSecurityTablebyInteger().values().toArray();
    return getFinSecurityList().iterator();
  }
  
  static public synchronized FinSecurity getFinSecurity(String name) {
    Hashtable<String,FinSecurity> table = getFinSecurityTable();
    FinSecurity data = null;
    if(table.containsKey(name)) {
      data = getFinSecurityTable().get(name);
    }
    return data;
  }
  
  static public synchronized FinSecurity getFinSecurity(int id) {
    Hashtable<Integer,FinSecurity> table = getFinSecurityTablebyInteger();
    FinSecurity data = null;
    if(table.containsKey(id)) {
      data = table.get(id);
    }
    return data;
  }
  
  // register all contracts
  static private Integer currentID = 0;
  static private Hashtable<Integer,String> mRegisterTable=null;  
  static private synchronized Hashtable<Integer,String> getRegisterTable() {
      if (mRegisterTable == null) {
        mRegisterTable = new Hashtable<Integer,String>();
      }
      return mRegisterTable;
  }  // Order Map and List
  
  static public int registerID(String name) {
    Hashtable<Integer,String> table = getRegisterTable();
    if(!table.containsValue(name)) {
      currentID++;
      table.put(currentID, name);
      setFinSecurityNameAndID(name,currentID);
    } else {
//      System.out.println("registerID: " + name + " is registered");
      return getFinSecurityList().indexOf(getFinSecurity(name))+1;
    }
    return currentID.intValue();
  }
  
  
  static public void main(String[] args) {
    System.out.println("GOOGL " + registerID("GOOGL"));
    System.out.println("GOOG "+registerID("GOOG"));
    System.out.println("GOOGL "+registerID("GOOGL"));
    System.out.println("YHOO "+registerID("YHOO"));
    System.out.println("YHOO "+registerID("YHOO"));
    FinSecurity im = new FinSecurity("GGOG",1);
    System.out.println("GGOG "+im.getDataFeed().toString());
  }
}
