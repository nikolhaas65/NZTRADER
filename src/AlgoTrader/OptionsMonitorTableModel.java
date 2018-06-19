/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AlgoTrader;

import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.JLabel;

/**
 *
 * @author nik
 */
public class OptionsMonitorTableModel extends AbstractTableModel {

  static Double zDbl = 0.00001d;
  static Integer zInt = 0;
  static int mNRows = 60;

  Object[][] mData=null;
  static Object[] objRow =
  {"", zInt, zDbl, zDbl, zInt, zDbl, zInt, zInt, "", zDbl, zDbl, zDbl, zDbl};

  String[] columnNames = {
    "Name", "bidSZ", "bid", "ask", "askSZ", "Last", "LastSZ",
    "Position", "Order",
    "Purchase Price", "Current Price",
    "Non-Real P/L", "Realised P/L"
  };

  int[] columnWidth = {
    160, 100,100,100,100,100,100,
    100,80,
    100,100,
    100,100
  };

  Class[] types = new Class[]{
    java.lang.String.class,
    java.lang.Long.class, java.lang.Double.class, java.lang.Double.class, java.lang.Long.class,
    java.lang.Double.class, java.lang.Long.class,
    java.lang.Integer.class, java.lang.String.class,
    java.lang.Float.class, java.lang.Float.class,
    java.lang.Float.class, java.lang.Float.class
  };

  public OptionsMonitorTableModel() {
    mData = getArrayOfCopiedObjects(objRow,mNRows);
  }

  public Object[][] getArrayOfCopiedObjects(Object[] obj, int nCols) {
    Object[][] objArr = new Object[nCols][obj.length];
    for(int k1=0;k1<nCols;k1++) {
      for(int k2=0;k2<obj.length;k2++) {
        objArr[k1][k2]=obj[k2];
      }
    }
    return objArr;
  }

  boolean[] canEdit = new boolean[]{
    false, false, false, false, false, false, false, false, false, false, false, false, false
  };

  public Object[][] getData() {
    return mData;
  }

  public int getColumnWidth(int k) {
    if(k<columnWidth.length) {
      return columnWidth[k];
    }
    return 100;
  }

  @Override
  public Class getColumnClass(int columnIndex) {
    return types[columnIndex];
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return canEdit[columnIndex];
  }

//    public void tableChanged(TableModelEvent e) {
//        int row = e.getFirstRow();
//        int column = e.getColumn();
//        TableModel model = (TableModel)e.getSource();
//        String columnName = model.getColumnName(column);
//        Object data = model.getValueAt(row, column);
//    }

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

  public void setFinSecName(int row, String name) {
    setValueAt(name, row, 0);
  }

  public void setBidSz(int row, int val) {
//      setValueAt(new Integer(val),row,1);
    setValueAt(val, row, 1);
  }

  public void setBid(int row, double val) {
//      setValueAt(new Double(val),row,2);
    setValueAt(val, row, 2);
  }

  public void setAsk(int row, double val) {
//      setValueAt(new Double(val),row,3);
    setValueAt(val, row, 3);
  }

  public void setAskSz(int row, int val) {
//      setValueAt(new Integer(val),row,4);
    setValueAt(val, row, 4);
  }

  public void setLast(int row, double val) {
    setValueAt(val, row, 5);
  }

  public void setLastSz(int row, double val) {
    setValueAt(val, row, 6);
  }

  public void setPosition(int row, int val) {
    setValueAt(val, row, 7);
  }

  public void setOrderBuySell(int row, String val) {
    setValueAt(val, row, 8);
  }

  public void setPurchasePrice(int row, double val) {
    setValueAt(val, row, 9);
  }

  public void setCurrentPrice(int row, double val) {
    setValueAt(val, row, 10);
  }

  public void setUnRealisedPNL(int row, double val) {
    setValueAt(val, row, 11);
  }

  public void setRealisedPNL(int row, double val) {
    setValueAt(val, row, 12);
  }

  @Override
  public Object getValueAt(int row, int col) {
//        System.out.println("[row,col]=[" + row + "," + col + "] = " + mData[row][col]); System.out.flush();
    return mData[row][col];
  }

  @Override
  public void setValueAt(Object aValue, int row, int col) {
//      System.out.println("edit");
    mData[row][col] = aValue;
    this.fireTableCellUpdated(row, col);
  }
}
