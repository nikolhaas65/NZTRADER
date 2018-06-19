/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ReaderLink;

import InstrumentManager.*;
import Utils.*;
import java.io.*;
import java.util.*;
import java.sql.Timestamp;
import IBLink.IBLink;
import OrderManager.*;

/**
 *
 * @author nik
 */
public class FeedReader extends Thread {
    public static enum dataType {
        tick, interval
    }

    BufferedReader mFeedFromFile = null;
    dataType type = dataType.interval;
    String mFName = "";
    DataFeed mDFeed = null;
    TradesSimulator mTFeed = null;
    int mContractID = -1;
    int mTimeFormatType = 1;

    public FeedReader(String name, int contractID, TradesSimulator tf) {
        mFeedFromFile = FileManager.getFileManager().getFile2Read(name);
        mFName = name;
        if(name.contains("Tick")) type=dataType.tick;
        mContractID = contractID;
        FinSecurity fs = InstrumentManager.getFinSecurity(mContractID);
        mDFeed = fs.getDataFeed();
        mTFeed = tf;
    }

    public void close() {
        try {
            mFeedFromFile.close();
        } catch(Exception ex) {

        }
    }

    String data=null;

    public int readRecord() {

        int ptype=-2;

        try {data = mFeedFromFile.readLine();}
        catch (Exception ex) {
            System.out.println("FeedReader: name: " + mFName + " problem reading");
        }

        // read data string
        if(data!=null) {
            String[] words = data.split("\\s");
//            Timestamp tm =  Utils.Utilities.TimeString2Timestamp(words[0], words[1], 4);
//            System.out.println(words[0] + " " + words[1]);

            Date tm = (type==dataType.tick) ? Utils.TimeUtils.String2Date2(words[0] + " " + words[1]) :
              Utils.TimeUtils.String2Date(words[0] + " " + words[1]) ;

            long day_time = Utils.TimeUtils.getDayTime(tm);

            int k=2;
            while(k<words.length && words[k].trim().isEmpty()) {k++;}
            //bid/ask/trade
            try { ptype = Integer.parseInt(words[k]);}
            catch(Exception ex) {ptype=3;};

            k++;
            while(k<words.length && words[k].trim().isEmpty()) {k++;}
            double price=0;
            //price
            try{ price = Double.parseDouble(words[k].replace(",","."));}
            catch(Exception ex) {};

            k++;
            while(k<words.length && words[k].trim().isEmpty()) {k++;}
            int sz=0;
            //size
            if(k<words.length) sz = Integer.parseInt(words[k]);

            //
            if(ptype==1) {
                //ask
                if(price>0 && sz>0) {
                    if(this.mContractID>0) {
                      IBLink.getIBLink().currentIBLinkTime(tm);
                      if(mDFeed.ask!=price) IBLink.getIBLink().tickPrice(this.mContractID,2,price, null);
                      if(mDFeed.ask_sz!=sz) IBLink.getIBLink().tickSize(this.mContractID,3,sz);
                    } else {
                      mDFeed.setAsk(price);
                      mDFeed.setAskSz(sz);
                      mDFeed.setAskTime(tm.getTime());
                    }
                }
            } else if(ptype==-1) {
                //bid
                if(price>0 && sz>0) {
                    if(this.mContractID>0) {                      
                      IBLink.getIBLink().currentIBLinkTime(tm.getTime());
                      if(mDFeed.bid!=price) IBLink.getIBLink().tickPrice(this.mContractID,1,price, null);
                      if(mDFeed.bid_sz!=sz) IBLink.getIBLink().tickSize(this.mContractID,0,sz);
                    } else {
                      mDFeed.setBid(price);
                      mDFeed.setBidSz(sz);
                      mDFeed.setBidTime(tm.getTime());
                    }
                }
            } else if(ptype==0) {
                if(price>0 && sz>0) {
                  //trade
                  // at what value?
                  if(this.mContractID>0) {
                    IBLink.getIBLink().currentIBLinkTime(tm.getTime());
                    IBLink.getIBLink().tickPrice(this.mContractID,4,price, null);
                    IBLink.getIBLink().tickSize(this.mContractID,5,sz);
                  }
                  ContractBook book = mTFeed.getBook(this.mContractID);
                  double mid = (mDFeed.ask+mDFeed.bid)/2d;
                  if(price>mid) {
                      book.setTrade(price, sz, 1);
                  } else if(price<mid) {
                      book.setTrade(price, sz,-1);
                  }
                  mDFeed.setLast(price);
                  mDFeed.setLastSize(sz);
                  mDFeed.setLastTime(tm.getTime());
                  book.execTrades();
                }
            }
            if(ptype==-1 || ptype==1) {
                mDFeed.updateSprMid();
            }
        }
        return ptype;
    }

    public void run() {
      System.out.println("Start Reading " + this.mFName);
      int nrec = 100000; int k=0;
//      while(this.readRecord()>-2){};
      while(this.readRecord()>-2 && (k<nrec || nrec==0)){k++;};
      System.out.println("Reading " + this.mFName + " is ENDED");
      this.close();
      
//      test
      FinSecurity fs = InstrumentManager.getFinSecurity(mContractID);
      OrderManager pm = fs.getOrderManager();
      pm.updateRealisedPnl();
    }
    
    public static void main(String[] args) {
      String fs_name = "RDSA NA Equity";
        TradesSimulator tf = new TradesSimulator();
        int id = InstrumentManager.registerID(fs_name);
        DataFeed df = InstrumentManager.getFinSecurity(id).getDataFeed();
        FeedReader fr = new FeedReader("D:\\data\\" + fs_name + ".txt",id,tf);
        long irec=0;
        while(fr.readRecord()>-2 && irec<200){
            System.out.println("q: irec: " + irec + " " + TimeUtils.Time2String(4, df.getLastUpdateTime()) + " "+ df.bid_sz + " " +
                    df.bid + " " + df.ask + " " + df.ask_sz);System.out.flush();
            irec++;
        };
        fr.close();
    }
}
