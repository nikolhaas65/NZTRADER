/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimeSeries;

import java.util.*;

import Graphs.*;
import InstrumentManager.*;

/**
 *
 * @author nik
 */
public class refObject {

  // refObject is defined by pair of FinSecurity and BarSeries
  //          1<->1 LinkedOHLCDataset (graph of BarSeries)
  //          1<->M LinkedXYDataset (many graphs of various algorithms, referred by name)
  
    public FinSecurity mFinSec = null;
    public BarSeries mBar_TS = null;
    public LinkedOHLCDataset mOHLC_TS = null;
    public Map<String,LinkedXYDataset> mXY_TS = new HashMap<String,LinkedXYDataset>();

    public refObject(FinSecurity finsec, BarSeries barts) {
      mFinSec = finsec;
      mBar_TS = barts;
    }

    public void setLinkedOHLC(LinkedOHLCDataset ohlc) {mOHLC_TS= ohlc;}
    public void removeLinkedOHLC()                    {mOHLC_TS = null;}

    /**
     * Tells graph to display algorithms associated with BarSeries
     * @param algoName - {@link String} name of the algorithm
     * @param xy - {@link LinkedXYDataset} object
     */
    public void setLinkedXY(String algoName, LinkedXYDataset xy) {
      mXY_TS.put(algoName,xy);
    }
    /**
     * Returns {@link LinkedXYDataset} object by given Algo-name
     * @param algoName - Name of algorithm
     * @return {@link LinkedXYDataset} object
     */
    public LinkedXYDataset getLinkedXY(String algoName) {
      return mXY_TS.get(algoName);
    }
    /**
     * Removes association of {@link LinkedXYDataset} object with Algo-name
     * @param algoName - Name of algorithm
     */
    public void removeLinkedXY(String algoName) {
      mXY_TS.remove(algoName);
    }
}
