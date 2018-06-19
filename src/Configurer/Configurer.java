/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configurer;

/**
 *
 * @author nik
 */
public class Configurer {
    static FinSecParamSet finsecSet=new FinSecParamSet();
    static ArbitrageList arbsList = new ArbitrageList();
    static public FinSecParamSet getFinSecParamSet() {
      return finsecSet;
    }
    static public ArbitrageList getArbitrageList() {
      return arbsList;
    }
}
