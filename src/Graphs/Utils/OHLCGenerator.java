/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs.Utils;

import Indicators.*;
import Graphs.LinkedOHLCDataset;
import Graphs.LinkedXYDataset;
import Graphs.TimedDataItem;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

import org.jfree.data.xy.OHLCDataItem;
//import org.jfree.data.xy.OHLCDataset;


/**
 *
 * @author nik
 */
public class OHLCGenerator {

//    static private final double normFlat = 1.0d / 24.0d;
    static private final double normFlat = 0.0d;

    private static OHLCDataItem createOHLCBar(Date newTime, OHLCDataItem bar) {
        double open = bar.getOpen().doubleValue();
        double high = bar.getHigh().doubleValue();
        double close = bar.getClose().doubleValue();
        double low = bar.getLow().doubleValue();
        boolean add = (Math.random() >= 0.5d) ? true : false;
        open = (add) ? open + Math.random() : open - Math.random();
        open = open - normFlat;
        high = open + Math.random() - normFlat;
        low = open - Math.random() - normFlat;
        close = low + (Math.random() - normFlat) * (high - low);

        return new OHLCDataItem(newTime, open, high, low, close, 0d);

    }

    static public String BarToString(OHLCDataItem bar) {
      String msg= Utils.TimeUtils.Time2String(4, bar.getDate().getTime());
      msg += " " + bar.getOpen().floatValue();
      msg += " " + bar.getHigh().floatValue();
      msg += " " + bar.getLow().floatValue();
      msg += " " + bar.getClose().floatValue();
      return msg;
    }

    public static LinkedOHLCDataset generateTSData(String name,
            OHLCDataItem initBar, int maxsize, int ts_size, long mlsecs) {

        int np = ts_size;
        Date dd = new Date();
        List<OHLCDataItem> data = Collections.synchronizedList(new LinkedList<OHLCDataItem>());
        data.add(initBar);

//        printOHLCDataItem(data.get(0));

        for (int k = 1; k < np; k++) {
//            System.out.println(k);
            Date tmp = new Date(dd.getTime() + ((long) k * mlsecs));
            OHLCDataItem bar = createOHLCBar(tmp, data.get(k - 1));
//            System.out.println(k + ": " + BarToString(bar));
            data.add(bar);
        }

//        System.out.println(data.getLast().getDate());
        LinkedOHLCDataset ts = new LinkedOHLCDataset(name, data, maxsize);
        return ts;

    }

    public static void addMoreData(LinkedOHLCDataset ts, int data2gen, long delay, long sleep) {
        Date dd = ts.getLastItem().getDate();
        List<OHLCDataItem> data = ts.getDataset();
        int m = data.size();
        for (int k = m; k < m + data2gen; k++) {
//            System.out.println(k);
            Date tmp = new Date(dd.getTime() + ((long) (k - m) * delay));
//                              System.out.println(tmp.toString());
            OHLCDataItem bar = createOHLCBar(tmp, data.get(data.size()-1));
            ts.addPoint(bar);
//                            data.add(bar);
            try {Thread.sleep(sleep);} catch (Exception ex) {};
        }
    }

    public static void addMoreData(LinkedOHLCDataset ts, LinkedXYDataset ts2,
            SARIndicator indicator, int data2gen, long delay, long sleep) {
        Date dd = ts.getLastItem().getDate();
        List<OHLCDataItem> data = ts.getDataset();
        int m = data.size();
        for (int k = m; k < m + data2gen; k++) {
//            System.out.println(k);
            Date tmp = new Date(dd.getTime() + ((long) (k - m) * delay));
//                              System.out.println(tmp.toString());
            OHLCDataItem bar = createOHLCBar(tmp, data.get(data.size()-1));
            ts.addPoint(bar);
            indicator.update(bar.getOpen().doubleValue(), bar.getHigh().doubleValue(),
                  bar.getLow().doubleValue(), bar.getClose().doubleValue());
            TimedDataItem titem = new TimedDataItem(bar.getDate(),indicator.getValue());
            ts2.addPoint(titem);
//                            data.add(bar);
            try {Thread.sleep(sleep);} catch (Exception ex) {};
        }
    }


    public static void addMoreData2(LinkedOHLCDataset ts,LinkedOHLCDataset ts2, int data2gen, long delay, long sleep) {
        Date dd = ts.getLastItem().getDate();
        Date dd2 = ts2.getLastItem().getDate();
        List<OHLCDataItem> data = ts.getDataset();
        List<OHLCDataItem> data2 = ts2.getDataset();
        int m = data.size();
        for (int k = m; k < m + data2gen; k++) {
//            System.out.println(k);
            Date tmp = new Date(dd.getTime() + ((long) (k - m) * delay));
//                              System.out.println(tmp.toString());
            OHLCDataItem bar = createOHLCBar(tmp, data.get(data.size()-1));
            ts.addPoint(bar);

            Date tmp2 = new Date(dd2.getTime() +300+ ((long) (k - m) * delay));
//                              System.out.println(tmp.toString());
            OHLCDataItem bar2 = createOHLCBar(tmp2, data2.get(data2.size()-1));
            ts2.addPoint(bar2);
//                            data.add(bar);
            try {Thread.sleep(sleep);} catch (Exception ex) {};
        }
    }
}
