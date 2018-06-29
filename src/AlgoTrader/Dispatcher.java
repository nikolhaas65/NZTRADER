/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package AlgoTrader;

import Utils.FileManager;
import IBLink.*;
import InstrumentManager.*;

/**
 * High level object which initiates all necessary components:
 * <ul>
 * <li> Read and initialize list of all fin-securities which will be used to 
 * as price indicators and/or traded
 * <li> Start GUI ({@link TraderGUI}), which aggregates and presents all needed information
 * <li> Initiates a link connection ({@link IBLink}) with trading systems. These can be data feeds 
 * and/or order execution platforms
 * <li> {@link FileManager} consistently handles all i/o streams
 * </ul>
 * Singleton.
 * @author nik
 */
public class Dispatcher {

  /**
   * Selects which feed is used: 
   * <ul>
   * <li>real-time (=true)
   * <li>historical stream (=false)
   * </ul>
   */
  public static final boolean mRTFeed = true;
  /**
   * Run with (=false) or without (=true) the GUI
   */
  public static final boolean mNoGUI = false;

  /**
   * holds Singleton object of Dispatcher
   */
  static Dispatcher mDisp = Dispatcher.getDispatcher();
  /**
   * Returns Singleton object of Dispatcher.
   * Use this one.
   * @return Singleton dispatcher
   */
  public static Dispatcher getDispatcher() {
    if(mDisp==null) {
      mDisp = new Dispatcher();
    }
    return mDisp;
  }

  /**
   * Singleton of In-memory FinSec DataBase
   */
  public static final FinSecInitializer mFinSecDB = FinSecInitializer.getFinSecInitializer();
  /**
   * Singleton of GUI {@link TraderGUI}
   */
  public static final TraderGUI mGUI = TraderGUI.getTraderGUI();
  /**
   * Singleton of {@link IBLink}
   */
  public static final IBLink mIB = IBLink.getIBLink();
  /**
   * Singleton of {@link FileManager}
   */
  public static final FileManager mFM = FileManager.getFileManager();

  public static String ticktag_demo =
          "100,101,104,105,106,107,165,225,232,221,233,236,258";

  public static void main(String args[]) {
    System.out.println("+++++++++++++++++++++++++++");
    System.out.println("Start AlgoTrader.Dispatcher");
    System.out.println("+++++++++++++++++++++++++++");
  }

}
