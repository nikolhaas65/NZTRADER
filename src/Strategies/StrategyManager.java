/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Strategies;
import java.util.*;

/**
 *
 * @author nik
 */
public class StrategyManager {
  static List<MacroStrategy> mStrategyMan = new ArrayList<MacroStrategy>();
  static public List<MacroStrategy> getAllStrategies() {
    return mStrategyMan;
  }

  static public void setStrategy(MacroStrategy str) {
    mStrategyMan.add(str);
  }
  static public void InitAllStrategies() {
    System.out.println(">>>>>>> START ALL STRATEGIES <<<<<<<<");
    Iterator<MacroStrategy> iter = mStrategyMan.iterator();
    while(iter.hasNext()) {
      iter.next().Init();
    }
  }
}
