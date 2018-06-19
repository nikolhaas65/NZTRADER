/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AlgoTrader;

/**
 *
 * @author nik
 */
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;

import java.util.*;

import Configurer.*;

public class AlgoParTable extends AbstractTableModel implements TableModelListener {
    private boolean DEBUG = true;
    public Object[][] data = null;

    public String[] columnNames = {"Par Name",
                      "Par Value"};
    Class[] types = new Class [] {
      java.lang.Object.class, java.lang.Object.class
    };

    private JTable table = null;

    private void initData() {
      AtXMLReader xmlr = new AtXMLReader();
      List<StrategyParameter> parSet = xmlr.getParamSet("Futures Volume Dynamics");
      int n = parSet.size();

      data = new Object[n][2];
      for(int k=0;k<n;k++) {
        StrategyParameter par = parSet.get(k);
        data[k][0] = par.getName();
        data[k][1] = par.getValue();
      }

    }

    public AlgoParTable() {
//        super(new GridLayout(1,0));

      initData();

      table = new JTable(data, columnNames);
      table.setPreferredScrollableViewportSize(new Dimension(500, 70));
      table.setFillsViewportHeight(true);

      table.getModel().addTableModelListener(this);
      System.out.println("listener1 " + this);

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

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int col) {
      System.out.println("edit");
      data[row][col]=aValue;
      this.fireTableCellUpdated(row,col);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
      return types [columnIndex];
    }

    public void tableChanged(TableModelEvent e) {
      System.out.println("changed " + e);
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
        System.out.println("r:" + row + " c:" + column + " v:" + data);

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
