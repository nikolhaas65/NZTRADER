/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Graphs;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.*;
import java.awt.BorderLayout;

import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.ui.RefineryUtilities;

import java.util.*;
import Graphs.Utils.*;
import Indicators.*;

/**
 *
 * @author nik
 */
public class DynamicOHLCGraph extends ApplicationFrame {

  boolean mTimeSegmented = true;
  static Color[] mListColors = {
    Color.red, Color.blue,
    Color.cyan, Color.magenta, 
    Color.green, Color.orange
  };

  javax.swing.JCheckBox jCheckBox_Indices = null;
  public javax.swing.JButton jButtonCloseGraph = null;

  static public enum GraphType {OHLC, Candlestick};
  
  private GraphType mType;
  int mInxColor = 0;

  private Color nextColor() {
    Color col = mListColors[mInxColor];
    System.out.println(col);
    if (mInxColor < mListColors.length) {
      mInxColor++;
    } else {
      mInxColor = 0;
    }
    return col;
  }

  JFreeChart mJFChart = null;
  int ind_series = 1;
  public ChartPanel mJPanel = null;

  public void clearAll() {
    int n = mJFChart.getXYPlot().getDatasetCount();
    for(int k=0;k<n;k++) {
      LinkedOHLCDataset ds = (LinkedOHLCDataset)mJFChart.getXYPlot().getDataset(k);
    };
  }

  public DynamicOHLCGraph(String s, GraphType type, boolean timeSegmented, OHLCDataset ts) {
    super(s);
    mTimeSegmented = timeSegmented;
    mType = type;
    if (type == GraphType.OHLC) {
      mJFChart = createOHLCChart(s, ts);
    } else if (type == GraphType.Candlestick) {
      mJFChart = createCandlestickChart(s, ts);
    }

    ((XYPlot) mJFChart.getPlot()).getRangeAxis().isAutoRange();
    mJPanel = new ChartPanel(mJFChart);
    mJPanel.setPreferredSize(new Dimension(1000, 500));
//    mJPanel.setVisible(true);

    JPanel content = new JPanel(new BorderLayout());
    content.add(mJPanel);

    JPanel buttonPanel = new JPanel(new BorderLayout());

    jCheckBox_Indices = new javax.swing.JCheckBox();
    jCheckBox_Indices.setText("Indicators");

   setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    jButtonCloseGraph = new javax.swing.JButton();
    jButtonCloseGraph.setText("Close");
    jButtonCloseGraph.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButtonCloseGraphActionPerformed(evt);
      }
    });

    buttonPanel.add(jCheckBox_Indices,BorderLayout.CENTER);
    buttonPanel.add(jButtonCloseGraph,BorderLayout.SOUTH);

    content.add(buttonPanel,BorderLayout.WEST);

//    setContentPane(mJPanel);
    setContentPane(content);

  }

  private void jButtonCloseGraphActionPerformed(java.awt.event.ActionEvent evt) {
      // TODO add your handling code here:
      if (evt.getActionCommand().equalsIgnoreCase("close graph")) {
        System.out.println("close graph");System.out.flush();
      }
    }

  public static SegmentedTimeline newMinuteTimeline() {

    long period = 10; // seconds
    long periodms = period * 1000;
    long nPeriodsInHour = 3600L / period;
    long incl = (long) ((17.25 - 9) * (double) nPeriodsInHour);
    long excl = 24L * nPeriodsInHour - incl;
    long start = 9L * nPeriodsInHour;

    System.out.println("Period: " + period + " nP: " + nPeriodsInHour
            + " IN: " + incl + " XCL: " + excl + " STRT: " + start);

    SegmentedTimeline timeline = new SegmentedTimeline(
            periodms, (int) incl, (int) excl);
    timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900() + start
            * timeline.getSegmentSize());
    timeline.setBaseTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
    return timeline;
  }

  private JFreeChart createOHLCChart(String name, OHLCDataset ohlcdataset) {
    JFreeChart jfreechart = null;
    if (mTimeSegmented) {
      Timeline tLine = newMinuteTimeline();
      jfreechart = ChartFactory.createHighLowChart(name, "Time", "Value", ohlcdataset, tLine, true);
    } else {
      jfreechart = ChartFactory.createHighLowChart(name, "Time", "Value", ohlcdataset, true);
    }

    XYPlot xyplot = (XYPlot) jfreechart.getPlot();
    HighLowRenderer highlowrenderer = (HighLowRenderer) xyplot.getRenderer();
    highlowrenderer.setOpenTickPaint(nextColor());
    highlowrenderer.setCloseTickPaint(Color.black);

    NumberAxis axis = (NumberAxis) xyplot.getRangeAxis();
    axis.setAutoRangeIncludesZero(false);

    DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
    dateaxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

    return jfreechart;

  }

  private JFreeChart createCandlestickChart(String name, OHLCDataset ohlcdataset) {

    JFreeChart jfreechart = null;
    if (mTimeSegmented) {
      Timeline tLine = newMinuteTimeline();
      jfreechart = ChartFactory.createCandlestickChart(name, "Time", "Value", ohlcdataset, tLine, true);
    } else {
      jfreechart = ChartFactory.createCandlestickChart(name, "Time", "Value", ohlcdataset, true);
    }


    XYPlot xyplot = (XYPlot) jfreechart.getPlot();

    NumberAxis axis = (NumberAxis) xyplot.getRangeAxis();
    axis.setAutoRangeIncludesZero(false);

    DateAxis dateaxis = (DateAxis) xyplot.getDomainAxis();
    dateaxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

    return jfreechart;
  }

  public void addOHLCDataset(OHLCDataset ohlcdataset) {

    XYPlot xyplot = (XYPlot) mJFChart.getPlot();
    xyplot.setDataset(ind_series, ohlcdataset);
    if (mType == GraphType.OHLC) {
      HighLowRenderer hlr = new HighLowRenderer();
      hlr.setOpenTickPaint(nextColor());
      hlr.setCloseTickPaint(Color.black);
      xyplot.setRenderer(ind_series, hlr);
    }
    ind_series++;
  }

  public void addXYDataset(XYDataset xydataset) {
    XYPlot xyplot = (XYPlot) mJFChart.getPlot();
    xyplot.setDataset(ind_series, xydataset);
    XYLineAndShapeRenderer xyrend = new XYLineAndShapeRenderer();
    xyrend.setBaseLinesVisible(false);
    xyrend.setBaseShapesVisible(true);
    xyrend.setBaseShapesFilled(true);
    Shape shape = new Ellipse2D.Float();
    xyrend.setBaseShape(shape);
    xyplot.setRenderer(ind_series, xyrend);
    ind_series++;
  }

  public void arrange() {
    this.pack();
    RefineryUtilities.centerFrameOnScreen(this);
    this.setVisible(true);
  }

  public static void main(String[] args) {

    String name = "my Example";

//        List<OHLCDataItem> data = Collections.synchronizedList(new LinkedList<OHLCDataItem>());
//        LinkedOHLCDataset ts = new LinkedOHLCDataset(name,data,100);

    long delay = 60000;
    int buffSZ = 100;

    OHLCDataItem initBar = new OHLCDataItem(new Date(), 100d, 101d, 99d, 100d, 1);
    LinkedOHLCDataset ts = OHLCGenerator.generateTSData(name, initBar, buffSZ, 200, delay);


//        OHLCDataItem initBar2 = new OHLCDataItem(new Date(),100d,101d,99d,100d,1);
//        LinkedOHLCDataset ts2 = OHLCGenerator.generateTSData(name,initBar2,100,200,delay);

    LinkedXYDataset sarTS = new LinkedXYDataset(name + " SAR", buffSZ);
    SARIndicator sar = new SARIndicator(0.02, 0.02, 0.2);
    Iterator<OHLCDataItem> iter = ts.getDataset().iterator();
    while (iter.hasNext()) {
      OHLCDataItem bar = iter.next();
      sar.update(bar.getOpen().doubleValue(), bar.getHigh().doubleValue(),
              bar.getLow().doubleValue(), bar.getClose().doubleValue());
      sarTS.addPoint(new TimedDataItem(bar.getDate(), sar.getValue()));
    }

    System.out.println(ts.getDataset().size());
    System.out.println(sarTS.getDataset().size());

    DynamicOHLCGraph chart = new DynamicOHLCGraph(name, GraphType.OHLC, false, ts);

//        chart.addOHLCDataset(ts2);
    chart.addXYDataset(sarTS);

    chart.arrange();


//        OHLCGenerator.addMoreData(ts, 200, delay, 100);
//        OHLCGenerator.addMoreData(ts,sarTS,sar, 20, delay, 100);
//        OHLCGenerator.addMoreData2(ts,ts2, 200, delay, 100);

  }
}
