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
public class ArbitrageItem {
  String mName="";

  List<String> mFinSecNamesList = null;

  @Override
  public String toString() {
    String msg="Name: " + mName + " Arbs: ";
    Iterator<String> iter = mFinSecNamesList.iterator();
    while(iter.hasNext()) {
      msg = msg + " / " + iter.next();
    }
    return msg;
  }

  public ArbitrageItem(String name) {
    mName = name;
    getFinSecList();
  }

  public List<String> getFinSecList() {
    if(mFinSecNamesList==null) {
      mFinSecNamesList = new ArrayList<String>();
    }
    return mFinSecNamesList;
  }

  public String getName() {
    return mName;
  }
  public void addFinSecName(String name) {
    List<String> arbs = getFinSecList();
    arbs.add(name);
  }
  public void addFinSecNames(List<String> names) {
    List<String> arbs = getFinSecList();
    arbs.addAll(names);
  }
}
