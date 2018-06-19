/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimeSeries;

import ReaderLink.*;
import InstrumentManager.*;
import Utils.*;

/**
 *
 * @author nik
 */
public class Tick2BarConverter {
  public static void main(String[] args) {
        String name = "RDSA NA";

        int mins = 1;
        int buffSZ = 150/mins;

        long vtime = Utils.TimeUtils.String2Time(3, "000500");

        BarSeries.FeedType feedtype = BarSeries.FeedType.FeedReader;

        BarSeries bs_bid = new BarSeries(name + " BID",feedtype,
                vtime,mins,BarSeries.PeriodType.minutes,buffSZ);
        BarSeries bs_ask = new BarSeries(name + " ASK",feedtype,
                vtime,mins,BarSeries.PeriodType.minutes,buffSZ);
        BarSeries bs_trade = new BarSeries(name + " TRADE",feedtype,
                vtime,mins,BarSeries.PeriodType.minutes,buffSZ);

        bs_bid.LogIt(true);
        bs_ask.LogIt(true);
        bs_trade.LogIt(true);

//        DataFeed df = new DataFeed();
        TradesSimulator tf = new TradesSimulator();
        String fs_name = "RDSA NA Equity";
        int id = InstrumentManager.registerID(fs_name);
        DataFeed df = InstrumentManager.getFinSecurity(id).getDataFeed();
        InstrumentManager.getFinSecurity(id);
        FeedReader fr = new FeedReader("D:\\data\\RDSA NA Equity.txt",id,tf);

        long irec=0;
//        while(fr.readRecord()>-2 && irec<25000){
        while(fr.readRecord()>-2){

          if(Math.IEEEremainder(irec,100000)==0) {
            System.out.println("q: irec: " + irec + " "+ df.bid_sz + " " +
                    df.bid + " " + df.ask + " " + df.ask_sz);System.out.flush();
          }

            if(df.changeBidAsk()) {
//              if(bs_ask.isIncremented() && bs_ask.getOneButLast().getTime()>0)
                bs_ask.update(df.lastUpdateTimeAsk,df.ask, df.ask_sz);
//              if(bs_bid.isIncremented() && bs_bid.getOneButLast().getTime()>0)
                bs_bid.update(df.lastUpdateTimeBid,df.bid, df.bid_sz);
            }

            if(df.changeTrade()) {
//              if(bs_trade.isIncremented() && bs_trade.getOneButLast().getTime()>0)
                bs_trade.update(df.lastUpdateTimeLast,df.last, df.last_sz);
            }

//            try{Thread.sleep(1);}catch(Exception ex){};
            irec++;

        }

        bs_bid.end_update(0);
        bs_ask.end_update(0);
        bs_trade.end_update(0);

        fr.close();
        System.out.println("END");
  }
}
