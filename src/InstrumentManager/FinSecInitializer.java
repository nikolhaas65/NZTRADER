/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package InstrumentManager;

import java.util.*;
import Configurer.*;
import Utils.*;

/**
 *
 * @author nik
 */
public class FinSecInitializer {
// STATIC OBJECT
// Initializes via Configurer

//  private static Dispatcher mDisp = Dispatcher.getDispatcher();

  static  FinSecParamSet finsecSet = Configurer.getFinSecParamSet();
  static  ArbitrageList arbsList   = Configurer.getArbitrageList();

  static  private List<FinSecParam> mFinSecList = null;
  static  private List<ArbitrageItem> mArbList = null;

  static private FinSecInitializer mFSInitializer = null;

  static public FinSecInitializer getFinSecInitializer() {
    if(mFSInitializer==null) {
      mFSInitializer = new FinSecInitializer();
      mFSInitializer.Init();
    }
    return mFSInitializer;
  }

  static public void Init() {

      if(mFinSecList==null) {
        AtXMLReader xmlr = new AtXMLReader();
        xmlr.ParseAll();
        mFinSecList = finsecSet.getFinSecParamSet();
      }

//      filter finsec by selected feed-type
      Iterator<FinSecParam> iter = mFinSecList.iterator();
      while(iter.hasNext()) {
        FinSecParam fsParam = iter.next();

        if(!fsParam.getType().equals("OPT")) {
//          initialization of all but OPTIONS (e.g. Stock, Futures, Currency)
          int r = InstrumentManager.registerID(fsParam.getName());
          FinSecurity finsec = InstrumentManager.getFinSecurity(r);
          finsec.setMultiplier(fsParam.getMultiplier());
          System.out.println("Init:FinSec "  + r + " Name: " + " " + finsec.getName() + " " + finsec);
        System.out.flush();

        } else {
//          initialization of OPTIONS
          Vector strikes = Utilities.splitRecord(fsParam.getStrikes(),",");
          String optName = fsParam.getName();
          if(optName.contains("CHAIN")) {
            optName = optName.substring(6);
          }
//              loop through all strikes
          for(int s=0;s<strikes.size();s++) {
            {
//              calls
              FinSecurity finsec = null;
              int r = 0;
              double d_str = Double.parseDouble(strikes.get(s).toString());
              String full_optName = optName + " C " + strikes.get(s).toString();
              r = InstrumentManager.registerID(full_optName);
              finsec = InstrumentManager.getFinSecurity(r);
              finsec.setMultiplier(fsParam.getMultiplier());
              System.out.println("Init:FinSec "  + r + " Name: " + " " + finsec.getName() + " " + finsec);
            }
            {
//              puts
              FinSecurity finsec = null;
              int r = 0;
              double d_str = Double.parseDouble(strikes.get(s).toString());
              String full_optName = optName + " P " + strikes.get(s).toString();
              r = InstrumentManager.registerID(full_optName);
              finsec = InstrumentManager.getFinSecurity(r);
              finsec.setMultiplier(fsParam.getMultiplier());
              System.out.println("Init:FinSec "  + r + " Name: " + " " + finsec.getName() + " " + finsec);
            }
          }

        }

      } // finsecList

//      init arbitrages
      if(mArbList==null) {
        mArbList = arbsList.getArbitrageList();
      }

      Iterator<ArbitrageItem> arb_iter = mArbList.iterator();
      while(arb_iter.hasNext()) {
        ArbitrageItem arb = arb_iter.next();
        List<String> lst = arb.getFinSecList();
        Arbitrage.Arbitrage.setSecurities(arb.getName(), lst);
        System.out.println("Set Arbs: " + arb);
        System.out.flush();
      }

      System.out.println("----DONE------");

  }

  static public void main(String[] opts) {
    FinSecInitializer.Init();
  }

}
