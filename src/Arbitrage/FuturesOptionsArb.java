/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Arbitrage;

import java.util.*;
import InstrumentManager.*;

/**
 *
 * @author nik
 */
public class FuturesOptionsArb {
  public FinSecurity mFutures = null;
  public List<FinSecurity> mStocks = null;
  public List<FinSecurity> mOptions = null;
  public long mMaturity = 0;
  public double[] mStrikes = null;
  public double[] mCPflags = null;
  public Map<Integer,Integer> mapOptionContractID2Indx = null;
  public Map<Integer,Integer> mapStocksContractID2Indx = null;

  public String printStrikesNCPs() {
    String msg="";
    for(int k=0;k<mStrikes.length;k++) {
      msg = msg + mStrikes[k] + " " + mCPflags[k] + "\n";
    }
    return msg;
  }

  public void sortArbitrage(String arb_name) {

      List<FinSecurity> arb_list = Arbitrage.getSecurities(arb_name);

      if(mapOptionContractID2Indx==null) {
        mapOptionContractID2Indx = new HashMap<Integer,Integer>();
      }
      if(mapStocksContractID2Indx==null) {
        mapStocksContractID2Indx = new HashMap<Integer,Integer>();
      }

      int opt_indx=0;
      int stx_indx=0;
      Iterator<FinSecurity> iter = arb_list.iterator();
      int j=0;
      while(iter.hasNext()) {
        FinSecurity fsCurrent = iter.next();

        System.out.println("sortArb: next " + j + " " + fsCurrent.getName());
        System.out.flush();

        j++;
        if(fsCurrent.getName().contains("FUT")) {
          mFutures = fsCurrent;
//          System.out.println(">> "+fsCurrent.getName());
        } else if (fsCurrent.getName().contains("STK")) {
          if(mStocks==null) {
            mStocks = new ArrayList<FinSecurity>();
          }
          mStocks.add(fsCurrent);
//          System.out.println(">> "+fsCurrent.getName());
          mapStocksContractID2Indx.put(fsCurrent.getContractID(), stx_indx);
          stx_indx++;
        } else if (fsCurrent.getName().contains("OPT")) {
          if(mOptions==null) {
            mOptions = new ArrayList<FinSecurity>();
          }
          mOptions.add(fsCurrent);
//          System.out.println(">> "+fsCurrent.getName());
          mapOptionContractID2Indx.put(fsCurrent.getContractID(), opt_indx);
          opt_indx++;
        }
      }
      mStrikes = new double[mOptions.size()];
      mCPflags = new double[mOptions.size()];
      Iterator<FinSecurity> iter2 = arb_list.iterator();
      int k=0;
      while(iter2.hasNext()) {
        FinSecurity next = iter2.next();
        if (next.getName().contains("OPT")) {
          Vector vec = Utils.Utilities.splitRecord(next.getName(), " ");
//          System.out.println("vec split: " + vec);
          double strike = Double.parseDouble(vec.get(4).toString());
          mStrikes[k]=strike;
          String cpflag = vec.get(3).toString();
          mCPflags[k] = cpflag.equalsIgnoreCase("C") ? 1.0 : -1.0;
          System.out.println("parsed: " + strike + " " + cpflag + " " + mCPflags[k]);
          k++;
        }
      }
  }

  double[] mPricesStx = null;
  public double[] getPricesStx(int contract, double price) {
    int inx=-1;
    try{
      if(mPricesStx==null) {
        mPricesStx = new double[mStocks.size()];
      }
      inx = mapStocksContractID2Indx.get(contract);
  //    System.out.println("getPriceStx " + inx);
      mPricesStx[inx]=price;
    }catch(Exception ex) {
      System.out.println("getPriceStx: inx: " + inx + " cntr: " + contract + " "+ ex);
    }
    return mPricesStx;
  }

  double[] mCallBids = null;
  public double[] getCallBids(int contract, double bid) {
    if(mCallBids==null) {
      mCallBids = new double[mOptions.size()];
    }
    int inx = mapOptionContractID2Indx.get(contract);
//    System.out.println("getCallBids " + inx);
    if(mCPflags[inx]==1) mCallBids[inx]=bid;
    return mCallBids;
  }

  double[] mCallAsks = null;
  public double[] getCallAsks(int contract, double ask) {
    if(mCallAsks==null) {
      mCallAsks = new double[mOptions.size()];
    }
    int inx = mapOptionContractID2Indx.get(contract);
//    System.out.println("getCallAsks " + inx);
    if(mCPflags[inx]==1) mCallAsks[inx]=ask;
    return mCallAsks;
  }

  double[] mPutBids = null;
  public double[] getPutBids(int contract, double bid) {
    if(mPutBids==null) {
      mPutBids = new double[mOptions.size()];
    }
    int inx = mapOptionContractID2Indx.get(contract);
//    System.out.println("getPutBids " + inx);
    if(mCPflags[inx]==-1) mPutBids[inx]=bid;
    return mPutBids;
  }
  double[] mPutAsks = null;
  public double[] getPutAsks(int contract, double ask) {
    if(mPutAsks==null) {
      mPutAsks = new double[mOptions.size()];
    }
    int inx = mapOptionContractID2Indx.get(contract);
//    System.out.println("getPutAsks " + inx);
    if(mCPflags[inx]==-1) mPutAsks[inx]=ask;
    return mPutAsks;
  }

}
