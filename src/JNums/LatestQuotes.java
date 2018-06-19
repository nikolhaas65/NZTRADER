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
public class LatestQuotes<C> {
  private int mWindow;
  private LinkedList<C> mQuotes = null;
  public LatestQuotes(int window) {
    mQuotes=new LinkedList<C>();
    mWindow = window;
  }
  
  public List<C> getList() {
    return mQuotes;
  }
  
  public C[] getArray() {
    return (C[]) mQuotes.toArray();
  }
  
  public void put(C val) {
    mQuotes.add(val);
    if(mQuotes.size()>mWindow) mQuotes.removeFirst();
  }
  
  public double average() {
    double ave=0;
    Iterator<C> iter = mQuotes.iterator();
    while (iter.hasNext()) {
      ave+=Double.parseDouble(iter.next().toString());
    }
    return ave/mQuotes.size();
  }
  
//  public static void main(String[] args) {
//    long iters=50;
//    LatestQuotes<Double> dlist = new LatestQuotes<Double>(10);
//    long ct=System.currentTimeMillis(); 
//    for(double k=0;k<iters;k++) {
//      dlist.put(k);
//    }
//    System.out.println(dlist.getList().size());
//    System.out.println(dlist.average());
//    System.out.println(System.currentTimeMillis()-ct);
//
//    FixedDoubleArray fixedArr = new FixedDoubleArray(10);
//    ct=System.currentTimeMillis(); 
//    for(int k=0;k<iters;k++) {
//      fixedArr.update(k);
//    }
//    System.out.println(fixedArr.getArray().length);
//    System.out.println(fixedArr.average());
//    System.out.println(System.currentTimeMillis()-ct);
//
//  }
  
}
