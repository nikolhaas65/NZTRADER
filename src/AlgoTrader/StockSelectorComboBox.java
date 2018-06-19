/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AlgoTrader;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ComboBoxModel;
import Configurer.*;
import java.util.*;

/**
 *
 * @author nik
 */
public class StockSelectorComboBox extends DefaultComboBoxModel {
  public static FinSecParamSet finsecSet=new FinSecParamSet();
  public StockSelectorComboBox() {
    List<FinSecParam> fl = finsecSet.getFinSecParamSet();
    Iterator<FinSecParam> iter = fl.iterator();
    while(iter.hasNext()) {
      FinSecParam fs = iter.next();
      this.addElement(fs.getName());
    }
  };
}
