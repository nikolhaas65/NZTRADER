/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configurer;
import java.util.*;

/**
 *
 * @author nik
 */
public class ArbitrageList {
  List<ArbitrageItem> mArbitrageList = null;
    public List<ArbitrageItem> getArbitrageList() {
    if(mArbitrageList==null) {
      mArbitrageList = (new AtXMLReader()).getArbitragesList();
    }
    return mArbitrageList;
  }
  public int findIndArbitrageByName(String name) {
    Iterator<ArbitrageItem> iter = getArbitrageList().iterator();
    int k=0;
    while(iter.hasNext()) {
      if(iter.next().getName().equalsIgnoreCase(name)) return k;
      k++;
    }
    return -1;
  }
  public ArbitrageItem findArbitrageByName(String name) {
    Iterator<ArbitrageItem> iter = getArbitrageList().iterator();
    int k=0;
    while(iter.hasNext()) {
      ArbitrageItem arb = iter.next();
      if(arb.getName().equalsIgnoreCase(name)) return arb;
      k++;
    }
    return null;
  }

}
