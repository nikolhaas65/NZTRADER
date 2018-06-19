/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configurer;

import javax.xml.parsers.*;
import java.io.File;
import org.w3c.dom.*;
import java.util.*;

/**
 *
 * @author nik
 */
public class AtXMLReader {
  
  public static String mainConfigDir = "src\\Configurer";

  static String mTickDataDirName = "";
  static public String getTickDataDir() {
    return mTickDataDirName;
  }

  FinSecParamSet finsecSet = Configurer.getFinSecParamSet();

  static String mBarDataDirName = "";
  static public String getBarDataDir() {
    return mBarDataDirName;
  }

  static String mConfigDirName = "";
//  File mFile = null;
  static boolean allParsed = false;
  static Document mDoc = null;
  static NodeList mStrategiesLst = null;
  static NodeList mArbitrageLst = null;
  static NodeList mSecFinList = null;
  static NodeList mConfigDir=null;
  static NodeList mTickDataDir=null;
  static NodeList mBarDataDir=null;

  public void ParseAll() {
    if(!allParsed) {
      try {
        File mFile = new File(mainConfigDir + "\\mainConfig.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        mDoc = db.parse(mFile);
        mDoc.getDocumentElement().normalize();
        mConfigDir = mDoc.getElementsByTagName("ibt_config_dir");
        mTickDataDir = mDoc.getElementsByTagName("tick_data_dir");
        mBarDataDir = mDoc.getElementsByTagName("bar_data_dir");
        mStrategiesLst = mDoc.getElementsByTagName("strategy");
        mArbitrageLst = mDoc.getElementsByTagName("arbitrage");
        mSecFinList = mDoc.getElementsByTagName("secfin");

        allParsed = true;

        mTickDataDirName = (String)getValue(mTickDataDir);
        mBarDataDirName = (String)getValue(mBarDataDir);
        mConfigDirName = (String)getValue(mConfigDir);

      } catch(Exception ex) {
        System.out.println(ex);
      }
    }
  }

  public List<FinSecParam> getFinSecParamSet() {
    List<FinSecParam> finsecList = new ArrayList<FinSecParam>();
    Node node = null;
    for(int k = 0 ; k < mSecFinList.getLength() ; k++) {
      node = mSecFinList.item(k);
      if(node.getNodeType()==Node.ELEMENT_NODE) {
        Element el = (Element) node;
        NodeList nlist = el.getChildNodes();
        FinSecParam finsec = new FinSecParam();
        for(int k2=0;k2<nlist.getLength();k2++) {
          Node node2 = nlist.item(k2);
          if(node2.getNodeType()==Node.ELEMENT_NODE) {
            if(node2.getNodeName().equalsIgnoreCase("name")) {
              finsec.setName(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("type")) {
              finsec.setType(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("symb")) {
              finsec.setSymbol(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("currency")) {
              finsec.setCurrency(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("strikes")) {
              finsec.setStrikes(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("exchange")) {
              finsec.setExchange(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("expiry")) {
              finsec.setExpiry(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("tick")) {
              finsec.setTick(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("feed")) {
              finsec.setFeed(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("limit")) {
              finsec.setLimit(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("interval")) {
              finsec.setInterval(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("mult")) {
              finsec.setMultiplier(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("arb")) {
              finsec.setArbitrage(node2.getFirstChild().getNodeValue());
            }
          }
        }
        finsecList.add(finsec);
      }
    }
    return finsecList;
  }

  public Node getStrategy(String name) {
    Node node = null;
    for(int k = 0 ; k < mStrategiesLst.getLength() ; k++) {
      node = mStrategiesLst.item(k);
      NamedNodeMap map = node.getAttributes();
      Node att_name = map.getNamedItem("name");
      System.out.println("att_name "+att_name.getNodeValue());
      if(att_name.getNodeValue().equalsIgnoreCase(name)) {
            return node;
      }
    }
    return node;
  }

  public List<ArbitrageItem> getArbitragesList() {
    List<ArbitrageItem> arbitrageList = new ArrayList<ArbitrageItem>();
    Node node = null;
    for(int k = 0 ; k < mArbitrageLst.getLength() ; k++) {
      node = mArbitrageLst.item(k);
      NamedNodeMap map = node.getAttributes();
      Node att_name = map.getNamedItem("name");
      System.out.println("getArbitragesList: att_name "+att_name.getNodeValue());

      ArbitrageItem arb = new ArbitrageItem(att_name.getNodeValue());
      arbitrageList.add(arb);

      NodeList nodes = node.getChildNodes();
      Node node2 = null;

      for(int k2 = 0 ; k2 < nodes.getLength() ; k2++) {
        node2 = nodes.item(k2);
        if(node2.getNodeType()==Node.ELEMENT_NODE) {
          NodeList nlist = node2.getChildNodes();
          System.out.println("k2: " + k2 + " " + node2.getNodeName() + " " + nlist.item(0).getNodeValue());
          if(nlist.item(0).getNodeValue().contains("OPT")) {
            List<String> listSeries = finsecSet.getOptionsByFilter(nlist.item(0).getNodeValue());
            System.out.println("sz: "+listSeries.size());
            arb.addFinSecNames(listSeries);
          } else {
            arb.addFinSecName(nlist.item(0).getNodeValue());
          }
        }
      }

    }
    return arbitrageList;
  }

  public List<String> getArbitrage(String name) {
    Node node = null;
    System.out.println("getArb: " + name + " " + mArbitrageLst.getLength());
    List<String> lst = new ArrayList<String>();
    for(int k = 0 ; k < mArbitrageLst.getLength() ; k++) {
      node = mArbitrageLst.item(k);
      NamedNodeMap map = node.getAttributes();
      Node att_name = map.getNamedItem("name");
      System.out.println("k: " + k + " att_name "+att_name.getNodeValue());
      if(att_name.getNodeValue().equalsIgnoreCase(name)) {
        NodeList nodes = node.getChildNodes();
        Node node2 = null;
        for(int k2 = 0 ; k2 < nodes.getLength() ; k2++) {
          node2 = nodes.item(k2);
          if(node2.getNodeType()==Node.ELEMENT_NODE) {
            NodeList nlist = node2.getChildNodes();
            System.out.println("kNode: " + k2 + " " + node2.getNodeName() + " > " + nlist.item(0).getNodeValue());
            if(nlist.item(0).getNodeValue().contains("OPT")) {
              List<String> listSeries = finsecSet.getOptionsByFilter(nlist.item(0).getNodeValue());
              System.out.println(listSeries.get(0));
              lst.addAll(listSeries);
            } else {
              lst.add(nlist.item(0).getNodeValue());
            }
          }
        }
      }
    }
    return lst;
  }

  List<StrategyParameter> listPar = new ArrayList<StrategyParameter>();
  public List<StrategyParameter> getParam(NodeList par_nodes) {
    Node node = null;
    for(int k = 0 ; k < par_nodes.getLength() ; k++) {
      node = par_nodes.item(k);
      if(node.getNodeType()==Node.ELEMENT_NODE) {
        NodeList nlist = node.getChildNodes();
        StrategyParameter paramSet = new StrategyParameter();
        for(int k2=0;k2<nlist.getLength();k2++) {
          Node node2 = nlist.item(k2);
          if(node2.getNodeType()==Node.ELEMENT_NODE) {
            if(node2.getNodeName().equalsIgnoreCase("param_name")) {
              paramSet.setName(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("param_type")) {
              paramSet.setType(node2.getFirstChild().getNodeValue());
            } else if(node2.getNodeName().equalsIgnoreCase("param_value")) {
              paramSet.setValue(node2.getFirstChild().getNodeValue());
            }
          }
        }
        if(!paramSet.isEmpty()) listPar.add(paramSet);
      }
    }
    return listPar;
  }

  public List<StrategyParameter> getParamSet(String stratName) {
    this.ParseAll();
    Node node = this.getStrategy(stratName);
    NodeList nodes = node.getChildNodes();
    List<StrategyParameter> lp = this.getParam(nodes);
    return lp;
  }

  public static Object getValue(NodeList list) {
    return list.item(0).getFirstChild().getNodeValue();
  }

  public static void main(String[] args) {
    AtXMLReader xmlR = new AtXMLReader();
//    xmlR.ParseAll();
//    Node node = xmlR.getStrategy("Futures Volume Dynamics");
//    NodeList nodes = node.getChildNodes();
////    System.out.println(nodes.getLength());
//    List<StrategyParameter> lp = xmlR.getParam(nodes);
    List<StrategyParameter> lp = xmlR.getParamSet("Futures Volume Dynamics");
    Iterator iter = lp.iterator();
    while(iter.hasNext()) {
      System.out.println(iter.next().toString());
    }

    List<FinSecParam> lf = xmlR.getFinSecParamSet();
    Iterator iter2 = lf.iterator();
    while(iter2.hasNext()) {
      System.out.println(iter2.next().toString());
    }

    System.out.println("ff: " + mTickDataDirName);

    ArbitrageList arbList = new ArbitrageList();
    ArbitrageItem arb = arbList.findArbitrageByName("EURUSD");
    System.out.println(arb);
    ArbitrageItem arb2 = arbList.findArbitrageByName("AEX_NOV");
    System.out.println(arb2);
  }
}
