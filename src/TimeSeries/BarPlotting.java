/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimeSeries;

import java.util.Date;

import ReaderLink.FeedReader;
import ReaderLink.TradesSimulator;
import InstrumentManager.*;
import Graphs.*;
import Indicators.*;
import Utils.*;

import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;

import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.TimeSeries;

/**
 *
 * @author nik
 */
public class BarPlotting {

    public static void main(String[] args) {
        long delay = 250;
        String name = "RDSA NA";

//        DataFeed df = new DataFeed();
        TradesSimulator tf = new TradesSimulator();
        String fs_name = "RDSA NA Equity";
        int id = InstrumentManager.registerID(fs_name);
        DataFeed df = InstrumentManager.getFinSecurity(id).getDataFeed();
        System.out.println("Finsec register: # " + id);
        FinSecurity mFinSec = InstrumentManager.getFinSecurity(id);
        FeedReader fr = new FeedReader("D:\\data\\RDSA NA Equity.txt",id,tf);

        int mins = 5;
        long shift = 5000;
        int buffSZ = 150/mins;

        long vtime = Utils.TimeUtils.String2Time(6, "000500");

        BarSeries.FeedType feedtype = BarSeries.FeedType.FeedReader;

        TimeWithinLimits tLIMITED = new TimeWithinLimits(
                TimeUtils.String2Time(6,"090000"),
                TimeUtils.String2Time(6,"171500"));

        BarSeries bs_bid = new BarSeries(name + " BID",feedtype,vtime,mins,BarSeries.PeriodType.minutes,buffSZ);
        BarSeries bs_ask = new BarSeries(name + " ASK",feedtype,vtime,mins,BarSeries.PeriodType.minutes,buffSZ);
        BarSeries bs_trd = new BarSeries(name + " TRD",feedtype,vtime,mins,BarSeries.PeriodType.minutes,buffSZ);
        TSManager.setTS(bs_bid,mFinSec);
        TSManager.setTS(bs_ask,mFinSec);
        TSManager.setTS(bs_trd,mFinSec);

        LinkedOHLCDataset tsA = new LinkedOHLCDataset(name + " ASK",buffSZ);
        LinkedOHLCDataset tsB = new LinkedOHLCDataset(name + " BID",buffSZ);
        LinkedOHLCDataset tsT = new LinkedOHLCDataset(name + " TRD",buffSZ);
        LinkedXYDataset   tsSAR = new LinkedXYDataset(name + " SAR",buffSZ);
        SARIndicator sar = new SARIndicator(0.02,0.02,0.2);
        TSManager.setLinkedOHLC(bs_bid,tsB);
        TSManager.setLinkedOHLC(bs_ask,tsA);
        TSManager.setLinkedOHLC(bs_trd,tsT);
        TSManager.setLinkedXY(bs_trd, "sar",tsSAR);

        DynamicOHLCGraph chart = new DynamicOHLCGraph(name,DynamicOHLCGraph.GraphType.OHLC,true,tsT);

        chart.addOHLCDataset(tsA);
        chart.addOHLCDataset(tsB);
        chart.addXYDataset(tsSAR);
        chart.arrange();

//        System.exit(0);


//        try{Thread.sleep(5000);}catch(Exception ex){};
//        System.exit(0);

        long irec=0;
//        while(fr.readRecord()>-2 && irec<25000){
        while(fr.readRecord()>-2){

//            System.out.println("q: irec: " + irec + " "+ df.bid_sz + " " +
//                    df.bid + " " + df.ask + " " + df.ask_sz);System.out.flush();

            if(df.changeBidAsk()) {
                int indA = bs_ask.update(df.lastUpdateTimeAsk,df.ask, df.ask_sz);
                if(bs_ask.isIncremented()) {
                    BarOHLC bar = bs_ask.getOneButLast();
                    Date bDate = new Date(bar.getTime());
                    System.out.println("ask " + bar.getTime() + " " +
                            bDate.toString() + " " +
                            bar.getOpen() + "/" + bar.getHigh() + "/" + 
                            bar.getLow() + "/" + bar.getClose() + "/" +
                            bar.getVolume());
                    if(bar.getTime()>0 && tLIMITED.isWithin(bar.getTime())) {
                        tsA.addPoint(new Date(bar.getTime()+shift),
                                bar.getOpen(),bar.getHigh(),bar.getLow(),
                                bar.getClose(),bar.getVolume());
                    }
                }

                int indB = bs_bid.update(df.lastUpdateTimeBid,df.bid, df.bid_sz);
                if(bs_bid.isIncremented()) {
                    BarOHLC bar = bs_bid.getOneButLast();
                    System.out.println("bid " + bar.getTime() + " " +
                            (new Date(bar.getTime())).toString() + " " +
                            bar.getOpen() + "/" + bar.getHigh() + "/" + 
                            bar.getLow() + "/" + bar.getClose() + "/" +
                            bar.getVolume());
                    if(bar.getTime()>0 && tLIMITED.isWithin(bar.getTime())) {
                    tsB.addPoint(new Date(bar.getTime()+shift*2),bar.getOpen(),
                            bar.getHigh(),bar.getLow(),bar.getClose(),bar.getVolume());
                    }
                }

                int indT = bs_trd.update(df.lastUpdateTimeLast,df.last, df.last_sz);
                if(bs_trd.isIncremented()) {
                    BarOHLC bar = bs_trd.getOneButLast();
                    System.out.println("trd " + bar.getTime() + " " +
                            (new Date(bar.getTime())).toString() + " " +
                            bar.getOpen() + "/" + bar.getHigh() + "/" +
                            bar.getLow() + "/" + bar.getClose() + "/" +
                            bar.getVolume());
                    if(bar.getTime()>0 && tLIMITED.isWithin(bar.getTime())) {
                      tsT.addPoint(new Date(bar.getTime()),bar.getOpen(),
                              bar.getHigh(),bar.getLow(),bar.getClose(),bar.getVolume());
                      sar.update(bar.getOpen(), bar.getHigh(),bar.getLow(), bar.getClose());
                      tsSAR.addPoint(new TimedDataItem(new Date(bar.getTime()),sar.getValue()));
                    }
                }
            }

            try{Thread.sleep(1);}catch(Exception ex){};
            irec++;

        };

        fr.close();
        System.out.println("END");
    }

}
