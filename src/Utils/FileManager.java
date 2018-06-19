/*
 * IOFileManager.java
 *
 * Created on June 16, 2005, 4:30 PM
 */
package Utils;

import java.util.*;
import java.io.*;

/**
 *
 * @author uzaits
 */
public class FileManager {

  static Map<String,PrintWriter>    fileWMap = null;
  static Map<String,BufferedReader> fileRMap = null;

  /** Creates a new instance of IOFileManager */
  private FileManager() {
    fileWMap = new HashMap<String,PrintWriter>();
    fileRMap = new HashMap<String,BufferedReader>();
  }

  private static FileManager mFileManager = null;
  public static  FileManager getFileManager() {
    if(mFileManager==null) {
      mFileManager = new FileManager();
    }
    return mFileManager;
  }

  public static void close(String fname) {
    PrintWriter file = fileWMap.get(fname);
    if(file!=null) {file.flush(); file.close();};
    BufferedReader fileR = fileRMap.get(fname);
    try {if(fileR!=null) fileR.close(); }
    catch(Exception ex) {
      System.out.println("FileManager: close(fname): problem");
      System.out.println(ex);
    }
  }

  public static void close() {
    Object[] pwArr = fileWMap.values().toArray();
    for (int ip = 0; ip < pwArr.length; ip++) {
      ((PrintWriter) pwArr[ip]).close();
    }
    try {
      Object[] brArr = fileRMap.values().toArray();
      for (int ip = 0; ip < brArr.length; ip++) {
        ((BufferedReader) brArr[ip]).close();
      }
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }

  public static PrintWriter getWFile(String fname) {
    PrintWriter obj = fileWMap.get(fname);
    if (obj != null) {
      return obj;
    } else {
      PrintWriter pw = openFile2Write(fname);
      fileWMap.put(fname, pw);
      return pw;
    }
  }
  
  public static PrintWriter openFile2Write(String fname) {
    PrintWriter a = null;
    try {
      a = new PrintWriter(new BufferedWriter(new FileWriter(fname)));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return a;
  }
  public static PrintWriter getWFileAppended(String fname) {
    PrintWriter obj = fileWMap.get(fname);
    if (obj != null) {
      return obj;
    } else {
      PrintWriter pw = openWFileAppended(fname);
      fileWMap.put(fname, pw);
      return pw;
    }
  }
  public static PrintWriter openWFileAppended(String fname) {
    PrintWriter a = null;
    try {
      a = new PrintWriter(new BufferedWriter(new FileWriter(fname,true)));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return a;
  }

  public static BufferedReader getFile2Read(String fname) {
    Object obj = fileRMap.get(fname);
    if (obj != null) {
      return (BufferedReader) obj;
    } else {
      BufferedReader pw = openFile2Read(fname);
      fileRMap.put(fname, pw);
      return pw;
    }
  }

  public static BufferedReader openFile2Read(String fname) {
    BufferedReader a = null;
    try {
      a = new BufferedReader(new FileReader(fname));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return a;
  }

  static public void main(String[] args) {
    FileManager fm = FileManager.getFileManager();
    fm.getWFile("test1.dat");
    fm.close("test1.dat");
    fm.getWFile("test2.dat");
    fm.getWFileAppended("test3.dat");
    fm.getFile2Read("test1.dat");
    fm.close();
  }
}
