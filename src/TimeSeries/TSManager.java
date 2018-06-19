/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimeSeries;

import java.util.*;
import Graphs.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import InstrumentManager.*;

/**
 *
 * @author nik
 */
public class TSManager {
// nameBarTS -> refObject

  static private Hashtable<BarSeries,refObject> mRefObjectByName=null;

  static private synchronized Hashtable<BarSeries,refObject> getRefObjectByBarSeriesTable() {
      if (mRefObjectByName == null) {
        mRefObjectByName = new Hashtable<BarSeries,refObject>();
      }
      return mRefObjectByName;
  }  // Order Map and List

  static public BarSeries getBarSeries(FinSecurity finsec) {
    Hashtable<BarSeries,refObject> table = getRefObjectByBarSeriesTable();
    Enumeration<refObject> elements = table.elements();
    BarSeries bs=null;
    while(elements.hasMoreElements()) {
      refObject obj = elements.nextElement();
      if(finsec==obj.mFinSec) {
        bs=obj.mBar_TS;
        return bs;
      }
    }
    return bs;
  }

  static public void setTS(BarSeries barts, FinSecurity finsec) {
    Hashtable<BarSeries,refObject> table = getRefObjectByBarSeriesTable();
    refObject obj = new refObject(finsec,barts);
    if(!table.containsKey(barts)) {
      table.put(barts, obj);
    }
  }

  static public Iterator<refObject> getAllRefObjects() {
//    return (FinSecurity[])getFinSecurityTablebyInteger().values().toArray();
    return getRefObjectByBarSeriesTable().values().iterator();
  }

  static void updateAll(BarSeries barseries) {
    refObject obj = getRefObjectByBarSeries(barseries);
    System.out.println("updateAll [" + obj.mFinSec.getName() + "] " + obj + " " + obj.mOHLC_TS);System.out.flush();
    BarOHLC bar = barseries.getOneButLast();
    if(bar.isInformative()) {
      obj.mOHLC_TS.addPoint(new Date(bar.getTime()), bar.getOpen(), bar.getHigh(), bar.getLow(), bar.getClose(), bar.getVolume());
    }
  }

  static public synchronized refObject getRefObjectByBarSeries(BarSeries barseries) {
    Hashtable<BarSeries,refObject> table = getRefObjectByBarSeriesTable();
    refObject obj = null;
//    if(table.containsKey(name)) {
      obj = table.get(barseries);
//    }
    return obj;
  }

  static public synchronized void setLinkedOHLC(BarSeries bs, LinkedOHLCDataset ohlcDS) {
    refObject obj = getRefObjectByBarSeries(bs);
    if(obj!=null) {
      obj.mOHLC_TS = ohlcDS;
    }
  }

  static public synchronized LinkedOHLCDataset getLinkedOHLC(BarSeries bs) {
    LinkedOHLCDataset data = null;
    refObject obj = getRefObjectByBarSeries(bs);
    if(obj!=null) {
      data = obj.mOHLC_TS;
    }
    return data;
  }

  static public synchronized void setLinkedXY(BarSeries bs,String nameAlgo, LinkedXYDataset data) {
    Hashtable<BarSeries,refObject> table = getRefObjectByBarSeriesTable();
    if(table.contains(bs)) {
      table.get(bs).setLinkedXY(nameAlgo,data);
    }
  }

  static public synchronized LinkedXYDataset getLinkedXY(BarSeries bs,String nameAlgo) {
    LinkedXYDataset data = null;
    refObject obj = getRefObjectByBarSeries(bs);
    if(obj!=null) {
      data = obj.getLinkedXY(nameAlgo);
    }
    return data;
  }

  public static void main(String[] args) {
    String mSecName = "names";
    int buffSZ=500;
    long vtime = Utils.TimeUtils.String2Time(6, "000500");

    BarSeries barTS = new BarSeries(mSecName,BarSeries.FeedType.FeedReader,vtime,1,BarSeries.PeriodType.minutes,buffSZ);

    TSManager.setTS(barTS, new FinSecurity(mSecName,1));
    System.out.println("SecName: " + mSecName + " " + barTS.getSeriesName());

    System.out.println("Graph FSNAME: " + barTS.getSeriesName());

  }

}
