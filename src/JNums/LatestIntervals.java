/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package JNums;

import java.util.*;

/**
 *
 * @author nik
 */
public class LatestIntervals<C> {
  private int mWindow;
  private List<C> mQuotes = null;
  public LatestIntervals(int window) {
    //mQuotes=new LinkedList<C>();
    mQuotes = Collections.synchronizedList(new LinkedList<C>());
    mWindow = window;
    System.out.println("win: " + window);
    System.out.flush();
  }
  
  public List<C> getList() {
    return mQuotes;
  }
  
  public C[] getArray() {
    return (C[]) mQuotes.toArray();
  }
  
  public void put(C val) {
    int iLast = mQuotes.size();
    if(iLast==mWindow) mQuotes.remove(0);
    mQuotes.add(val);
  }
  
  public void print() {
    Iterator<C> iter = mQuotes.iterator();
    while(iter.hasNext()) {
      System.out.println(iter.next().toString());
    }
  }
  
  public double average() {
    double ave=0;
    Iterator<C> iter = mQuotes.iterator();
    while (iter.hasNext()) {
      ave+=Double.parseDouble(iter.next().toString());
    }
    return ave/mQuotes.size();
  }
  
  public static void main(String[] args) {
    long iters=10;
    LatestIntervals<Double> dlist = new LatestIntervals<Double>(5);
    long ct=System.currentTimeMillis(); 
    for(double k=0;k<iters;k++) {
      dlist.put(k);
      //System.out.println(dlist.getList().size()); System.out.flush();              
    }
    
    System.out.println("size " + dlist.getList().size());
    System.out.println("ave " + dlist.average());
    System.out.println("time " + (System.currentTimeMillis()-ct));
    
    dlist.print();

    /*
    FixedDoubleArray fixedArr = new FixedDoubleArray(10);
    ct=System.currentTimeMillis(); 
    for(int k=0;k<iters;k++) {
      fixedArr.update(k);
    }
    System.out.println(fixedArr.getArray().length);
    System.out.println(fixedArr.average());
    System.out.println(System.currentTimeMillis()-ct);
*/
    
  }
  
}
