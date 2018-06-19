/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package JNums;

/**
 *
 * @author nik
 */
public class FixedDoubleArray {
  private double[] mArr=null;
  private int mSize;
  private int last_index;
  private boolean filled;
  
  public FixedDoubleArray(int size) {
    mArr=new double[size];
    mSize=size-1;
    last_index=0;
    filled=false;
  }
  
  public void update(double val) {
    mArr[last_index]=val;
    last_index++;
    if(last_index>mSize) {last_index=0;filled=true;};
  }
  
  public double[] getArray() {
    if(filled) {return mArr.clone();}
    double[] cl = new double[last_index];
    for(int k=0;k<last_index;k++) {cl[k]=mArr[k];}
    return cl;
  }

  public double average() {
    double ave=0;
    int kMax=last_index;
    if(filled) {kMax=mSize+1;}
    for(int k=0;k<kMax;k++) {ave+=mArr[k];}
    return ave/kMax;
  }
  
//  public static void main(String[] args) {
//    FixedDoubleArray fixedArr = new FixedDoubleArray(10);
//    long ct=System.currentTimeMillis(); 
//    for(int k=0;k<100;k++) {
//      fixedArr.update(k);
//    }
//    System.out.println(fixedArr.getArray().length);
//    System.out.println(fixedArr.average());
//    System.out.println(System.currentTimeMillis()-ct);
//  }

}
