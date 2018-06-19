/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configurer;
import java.util.*;
import Utils.*;

/**
 *
 * @author nik
 */
public class FinSecParamSet {
  List<FinSecParam> mFinsecList = null;
  public List<FinSecParam> getFinSecParamSet() {
    if(mFinsecList==null) {
      mFinsecList = (new AtXMLReader()).getFinSecParamSet();
    }
    return mFinsecList;
  }
  public int findIndFinsecByName(String name) {
    Iterator<FinSecParam> iter = getFinSecParamSet().iterator();
    int k=0;
    while(iter.hasNext()) {
      if(iter.next().getName().equalsIgnoreCase(name)) return k;
      k++;
    }
    return -1;
  }

  public List<String> getOptionsByFilter(String filter) {
    if(mFinsecList.size()>0) {
      List<String> list = new ArrayList<String>();
      Iterator<FinSecParam> iter = mFinsecList.iterator();
      while(iter.hasNext()) {
        FinSecParam fsPar = iter.next();
        String name = fsPar.getName();
        if(name.contains("CHAIN")) { 
                  name=name.substring(6);
        }
        if(name.contains(filter)) {
          Vector strikes = Utilities.splitRecord(fsPar.getStrikes(),",");
          for(int s=0;s<strikes.size();s++) {

            double d_str = Double.parseDouble(strikes.get(s).toString());

            String callFinSec = name + " C " + strikes.get(s).toString();
            System.out.println("finsec: OPTION: CALL: "+callFinSec);
            list.add(callFinSec);

            String putFinSec = name + " P " + strikes.get(s).toString();
            System.out.println("finsec: OPTION: PUT: "+putFinSec);
            list.add(putFinSec);
            
          }
        }
      }
      return list;
    }
    return null;
  }

  public FinSecParam findFinSecParamByName(String name) {
    Iterator<FinSecParam> iter = getFinSecParamSet().iterator();
    int k=0;
    while(iter.hasNext()) {
      FinSecParam fs = iter.next();
      if(fs.getName().equalsIgnoreCase(name)) return fs;
      k++;
    }
    return null;
  }
}
