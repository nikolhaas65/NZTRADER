/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AlgoTrader;

import java.util.*;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;

import Configurer.*;
import InstrumentManager.*;
import Graphs.*;
import TimeSeries.*;
import Utils.*;

/**
 * 
 * @author nik
 */
public class FinSecTable extends AbstractTableModel implements TableModelListener {

    private static Dispatcher mDisp = Dispatcher.getDispatcher();

    public static enum FeedTypes
            {NONE,  IBFeed,   FeedReader};

    private boolean DEBUG = true;
    public Object[][] mData = null;

    private class ColumnID {
      public static final int Name=0;
      public static final int Feed=1;
      public static final int Limit=2;
      public static final int Interval=3;
      public static final int ActiveFeed=4;
      public static final int ActiveStrategy=5;
      public static final int Graph=6;
    }

//    should be synchrnized with types
    public String[] columnNames = {
      "FinSec Name",
      "Feed",
      "Limit",
      "Interval",
      "Active Feed",
      "Active Strategy",
      "Graph Pr_Ind"
    };

//    should be synchrnized with columnNames
    Class[] types = new Class [] {
      java.lang.Object.class,
      java.lang.Object.class,
      java.lang.Integer.class,
      java.lang.String.class,
      java.lang.Boolean.class,
      java.lang.Boolean.class,
      java.lang.Boolean.class
    };

    FinSecParamSet finsecSet = Configurer.getFinSecParamSet();
    ArbitrageList arbsList   = Configurer.getArbitrageList();

    private List<FinSecParam> mFinSecList = null;
//    private List<ArbitrageItem> mArbList = null;
    private List<FinSecurity> mFSListTickedON = null;
    private List<FinSecurity> mOptFSListTickedON = null;
    private Map<String,DynamicOHLCGraph> mMapGraphs = null;

    private JTable table = null;
    static private TraderGUI mParent = null;

    public void setParent(TraderGUI parent) {
      System.out.println("Parent "  + parent);
      mParent = parent;
    }

    public void clearFSListTickedON() {
      mFSListTickedON.clear();
    }
    public void clearOptFSListTickedON() {
      mOptFSListTickedON.clear();
    }

    public FinSecTable(TraderGUI parent) {
        
      super();

//      parent.setLayout(new GridLayout(2,3));

      initData();

      mParent = parent;
      table = new JTable(mData, columnNames);
      table.setPreferredScrollableViewportSize(new Dimension(500, 70));
      table.setFillsViewportHeight(true);

      table.getModel().addTableModelListener(this);
      System.out.println("listener2 " + this); System.out.flush();

      if (DEBUG) {
          table.addMouseListener(new MouseAdapter() {
              @Override
              public void mouseClicked(MouseEvent e) {
                  printDebugData(table);
              }
          });
      }

      //Create the scroll pane and add the table to it.
      JScrollPane scrollPane = new JScrollPane(table);

//Add the scroll pane to this panel.
//        add(scrollPane);
    }

    private void initData() {

      if(mFinSecList==null) {
        mFinSecList = finsecSet.getFinSecParamSet();
      }

//      filter finsec by selected feed-type
      Iterator<FinSecParam> iter = mFinSecList.iterator();
      int nRows=0;
      while(iter.hasNext()) {
        FinSecParam fs = iter.next();
        if(mDisp.mRTFeed && fs.getFeed().equalsIgnoreCase("IBFeed")) nRows++;
        if(!mDisp.mRTFeed && fs.getFeed().equalsIgnoreCase("FeedReader")) nRows++;
      }

      int nColumns = columnNames.length;
      System.out.println("nRows/nColumns " + nRows + " " + nColumns);
      mData = new Object[nRows][nColumns];

      int k = 0;
      iter = mFinSecList.iterator();
      while(iter.hasNext()) {
        FinSecParam fsParam = iter.next();

        if(mDisp.mRTFeed && fsParam.getFeed().equalsIgnoreCase("IBFeed")) {

          if(!fsParam.getName().contains("CHAIN")) {
            int r = InstrumentManager.registerID(fsParam.getName());
            FinSecurity finsec = InstrumentManager.getFinSecurity(r);
  //          finsec.setMultiplier(fsParam.getMultiplier());
            System.out.println("Init:FinSec " + " Name " + r + " " + finsec);
            System.out.println("Init:FinSec " + " Name " + r + " " + finsec.getName());
          }

          mData[k][ColumnID.Name] = fsParam.getName();
          mData[k][ColumnID.Feed] = fsParam.getFeed();
          mData[k][ColumnID.Limit] = fsParam.getLimit();
          mData[k][ColumnID.Interval] = fsParam.getInterval();
          mData[k][ColumnID.ActiveFeed] = new Boolean(false);
          mData[k][ColumnID.ActiveStrategy] = new Boolean(false);
          mData[k][ColumnID.Graph] = new Boolean(false);
          k++;
        } else if(!mDisp.mRTFeed && fsParam.getFeed().equalsIgnoreCase("FeedReader")) {

          int r = InstrumentManager.registerID(fsParam.getName());
          FinSecurity finsec = InstrumentManager.getFinSecurity(r);
//          finsec.setMultiplier(fsParam.getMultiplier());

          mData[k][ColumnID.Name] = fsParam.getName();
          mData[k][ColumnID.Feed] = fsParam.getFeed();
          mData[k][ColumnID.Limit] = fsParam.getLimit();
          mData[k][ColumnID.Interval] = fsParam.getInterval();
          mData[k][ColumnID.ActiveFeed] = new Boolean(false);
          mData[k][ColumnID.ActiveStrategy] = new Boolean(false);
          mData[k][ColumnID.Graph] = new Boolean(false);
          k++;
        }
      } // finsecList

//      init arbitrages
//      if(mArbList==null) {
//        mArbList = arbsList.getArbitrageList();
//      }

//      Iterator<ArbitrageItem> arb_iter = mArbList.iterator();
//      while(arb_iter.hasNext()) {
//        ArbitrageItem arb = arb_iter.next();
//        List<String> lst = arb.getFinSecList();
//        Arbitrage.Arbitrage.setSecurities(arb.getName(), lst);
//        System.out.println("Set Arbs: " + arb);
//      }

      System.out.println("DATA SIZE " + mData.length + "x" + mData[0].length);

//      graphics
      mMapGraphs = new HashMap<String,DynamicOHLCGraph>();
      mFSListTickedON = Collections.synchronizedList(new ArrayList<FinSecurity>());
      mOptFSListTickedON = Collections.synchronizedList(new ArrayList<FinSecurity>());


    }

    public boolean getFeedActivity(String finsec) {
      int r = InstrumentManager.registerID(finsec);
      return (Boolean)mData[r][3];
    }

    public void tableChanged(TableModelEvent e) {

      int row = e.getFirstRow();
      int column = e.getColumn();
      TableModel model = (TableModel)e.getSource();
      String columnName = model.getColumnName(column);
      String FinSecName = mData[row][ColumnID.Name].toString();
//      System.out.println("r:" + row + " c:" + column + " v:" + data);

// ============= ACTIVATE FEED =================
      if(column==ColumnID.ActiveFeed) { // ActiveFeed

        if(((Boolean)mData[row][ColumnID.ActiveFeed]).equals(Boolean.TRUE)) {
//Feed is ON

          if(mDisp.mIB.getClient().isConnected()) {

            System.out.println("isConnected " + mData[row][ColumnID.Feed] + " " + FeedTypes.IBFeed.name());
            FinSecParam fsPar = finsecSet.findFinSecParamByName(FinSecName);
            System.out.println("Start feed: " + fsPar.getType()+"//"+fsPar.getSymbol()+
                    "//"+fsPar.getExchange()+"//"+fsPar.getCurrency()+"//"+fsPar.getTick());

            if(!fsPar.getType().equals("OPT")) {
// call to feed request for non-options

              if(fsPar.getType().equals("FUT")) {
                IBLink.IBLink.startTradingViaGUI(FinSecName,
                        fsPar.getSymbol(),fsPar.getType(),fsPar.getExchange(),fsPar.getCurrency(),fsPar.getExpiry(),
                        0,"",fsPar.getTick(),
                        (Integer)mData[row][ColumnID.Limit]);
              } else {
                IBLink.IBLink.startTradingViaGUI(FinSecName,
                        fsPar.getSymbol(),fsPar.getType(),fsPar.getExchange(),fsPar.getCurrency(),"",
                        0,"",fsPar.getTick(),
                        (Integer)mData[row][ColumnID.Limit]);
              }

              FinSecurity finsec = InstrumentManager.getFinSecurity(FinSecName);

              if(!mFSListTickedON.contains(finsec)) mFSListTickedON.add(finsec);
              int ind = mFSListTickedON.indexOf(finsec);
              finsec.setTableIndex(ind);

              if(mParent!=null) {
                PortfolioMonitorTableModel tbl =
                        (PortfolioMonitorTableModel) mParent.getPortfolioMonitorTable().getModel();
                tbl.setFinSecName(ind, FinSecName);
              }

                  finsec.DisActivateTrading();

              if(!mDisp.mRTFeed) {
                finsec.ActivateTrading();
                System.out.println("# " + finsec.getContractID() + " canTrade " + finsec.canTrade());
              }

            } else {
// call to feed request for OPTION-series
              if(FinSecName.contains("CHAIN")) {
                FinSecName=FinSecName.substring(6);
              }
              
              Vector strikes = Utilities.splitRecord(fsPar.getStrikes(),",");

//              loop through all strikes
              for(int s=0;s<strikes.size();s++) {

                double d_str = Double.parseDouble(strikes.get(s).toString());

                String callFinSec = FinSecName + " C " + strikes.get(s).toString();
                IBLink.IBLink.startTradingViaGUI(callFinSec,
                        fsPar.getSymbol(),fsPar.getType(),fsPar.getExchange(),fsPar.getCurrency(),fsPar.getExpiry(),
                        d_str,"C",fsPar.getTick(),
                        (Integer)mData[row][ColumnID.Limit]);
                FinSecurity callFS = InstrumentManager.getFinSecurity(callFinSec);

                String putFinSec = FinSecName + " P " + strikes.get(s).toString();
                IBLink.IBLink.startTradingViaGUI(putFinSec,
                        fsPar.getSymbol(),fsPar.getType(),fsPar.getExchange(),fsPar.getCurrency(),fsPar.getExpiry(),
                        d_str,"P",fsPar.getTick(),
                        (Integer)mData[row][ColumnID.Limit]);
                FinSecurity putFS = InstrumentManager.getFinSecurity(putFinSec);

                if(!mOptFSListTickedON.contains(callFS)) mOptFSListTickedON.add(callFS);
                if(!mOptFSListTickedON.contains( putFS)) mOptFSListTickedON.add( putFS);

                int indC = mOptFSListTickedON.indexOf(callFS);
                callFS.setTableIndex(indC);
                int indP = mOptFSListTickedON.indexOf(putFS);
                putFS.setTableIndex(indP);
                System.out.println("Inited: " + indC + " " + indP + " " + callFinSec + " " + putFinSec);
                System.out.flush();

                if(mParent!=null) {
                  OptionsMonitorTableModel otbl =
                          (OptionsMonitorTableModel) mParent.getOptionsMonitorTable().getModel();
                  otbl.setFinSecName(indC, callFinSec);
                  otbl.setFinSecName(indP, putFinSec);
                }

                callFS.DisActivateTrading();
                putFS.DisActivateTrading();

                if(!mDisp.mRTFeed) {
                  callFS.ActivateTrading();
                  System.out.println("# " + callFS.getContractID() + " canTrade " + callFS.canTrade());
                  putFS.ActivateTrading();
                  System.out.println("# " + putFS.getContractID() + " canTrade " + putFS.canTrade());
                }

              }
            }

          } else  if(((Boolean)mData[row][ColumnID.ActiveFeed]).equals(Boolean.FALSE)) {
// close Trading is OFF
//            Feed is still on, but not updated in the Table!!!

            if(!FinSecName.contains("OPT")) {
//              close Trading for non-options
              FinSecurity finsec = InstrumentManager.getFinSecurity(FinSecName);
              int ind = mFSListTickedON.indexOf(finsec);
              if(mParent!=null) {
                PortfolioMonitorTableModel tbl =
                        (PortfolioMonitorTableModel) mParent.getPortfolioMonitorTable().getModel();
                tbl.setFinSecName(ind, "");
              }

              finsec.setTableIndex(-1);
              mFSListTickedON.remove(finsec);

              Iterator<FinSecurity> iter = mFSListTickedON.iterator();
              while(iter.hasNext()) {
                iter.next().setTableIndex(mFSListTickedON.indexOf(finsec));
              }

              if(!mDisp.mRTFeed) {
                  finsec.DisActivateTrading();
              }

            } else {
//              close Trading for option series
              int nOpts = mOptFSListTickedON.size();
              FinSecurity[] fsArray = (FinSecurity[])mOptFSListTickedON.toArray();
              for(int k=0;k<nOpts;k++) {
                int ind = mOptFSListTickedON.indexOf(fsArray[k]);
                if(mParent!=null) {
                  OptionsMonitorTableModel otbl =
                          (OptionsMonitorTableModel) mParent.getOptionsMonitorTable().getModel();
                  otbl.setFinSecName(ind, "");
                }

                fsArray[k].setTableIndex(-1);
                mOptFSListTickedON.remove(fsArray[k]);

                Iterator<FinSecurity> iter = mOptFSListTickedON.iterator();
                while(iter.hasNext()) {
                  iter.next().setTableIndex(mOptFSListTickedON.indexOf(fsArray[k]));
                }

                if(!mDisp.mRTFeed) {
                    fsArray[k].DisActivateTrading();
                }

              }
            }

          }

        }
      }

// ============= ACTIVATE STRATEGY =================
      if(columnName.equalsIgnoreCase("Active Strategy")) { // Active Strategy

        FinSecParam fsPar = finsecSet.findFinSecParamByName(FinSecName);

        if(((Boolean)mData[row][ColumnID.ActiveStrategy]).equals(Boolean.TRUE)) {
          if(!fsPar.getType().equals("OPT")) {
            FinSecurity finsec = InstrumentManager.getFinSecurity(mData[row][0].toString());
            finsec.ActivateTrading();
            System.out.println("# " + finsec.getContractID() + " canTrade " + finsec.canTrade());
          } else {
            int nOpts = mOptFSListTickedON.size();
            FinSecurity[] fsArray = (FinSecurity[])mOptFSListTickedON.toArray();
            for(int k=0;k<nOpts;k++) {
              fsArray[k].ActivateTrading();
              System.out.println("# " + fsArray[k].getContractID() + " canTrade " + fsArray[k].canTrade());
            }
          }
        } else if(((Boolean)mData[row][ColumnID.ActiveStrategy]).equals(Boolean.FALSE)) {
          if(!fsPar.getType().equals("OPT")) {
            FinSecurity finsec = InstrumentManager.getFinSecurity(mData[row][0].toString());
            System.out.println("# " + finsec.getContractID() + " DisActivate Trading " + finsec.canTrade());
            finsec.DisActivateTrading();
          } else {
            int nOpts = mOptFSListTickedON.size();
            FinSecurity[] fsArray = (FinSecurity[])mOptFSListTickedON.toArray();
            for(int k=0;k<nOpts;k++) {
              fsArray[k].DisActivateTrading();
              System.out.println("# " + fsArray[k].getContractID() + " DisActivate Trading " + fsArray[k].canTrade());
            }
          }
        }

      }

// ============= ACTIVATE GRAPH =================
      if(columnName.equalsIgnoreCase("Graph Pr_Ind")) { // Graph

        String fsname = mData[row][ColumnID.Name].toString();
        if(((Boolean)mData[row][ColumnID.Graph]).equals(Boolean.TRUE)) {
          FinSecurity finsec = InstrumentManager.getFinSecurity(fsname);

          BarSeries bs = TSManager.getBarSeries(finsec);
          if(bs!=null) {
            System.out.println("Graph1"); System.out.flush();
              LinkedOHLCDataset ts = TSManager.getLinkedOHLC(bs);
              System.out.println("Graph FSNAME: " + fsname + " "+ ts);
              DynamicOHLCGraph chart = new DynamicOHLCGraph(fsname, DynamicOHLCGraph.GraphType.OHLC, false, ts);
              mMapGraphs.put(fsname,chart);
              chart.arrange();
          }

        } else if(((Boolean)mData[row][ColumnID.Graph]).equals(Boolean.FALSE)) {
          System.out.println("no Graph"); System.out.flush();
          DynamicOHLCGraph chart = (DynamicOHLCGraph) mMapGraphs.get(fsname);
          if(chart!=null) chart.clearAll();
        }

      }

    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return mData.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
//        System.out.println("[row,col]=[" + row + "," + col + "] = " + mData[row][col]); System.out.flush();
        return mData[row][col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the mData/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
//      System.out.println("edit");
      mData[row][col]=aValue;
      this.fireTableCellUpdated(row,col);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
//      System.out.println("col= " + columnIndex); System.out.flush();
      return getValueAt(0, columnIndex).getClass();
    }

    private void printDebugData(JTable table) {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        System.out.println("Value of data: ");
        for (int i=0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j=0; j < numCols; j++) {
                System.out.print("  " + model.getValueAt(i, j));
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }

}
