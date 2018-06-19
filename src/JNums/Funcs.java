/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package JNums;

/**
 *
 * @author nik
 */
public class Funcs {

  public static double limitMiMa(double f,double mi, double ma) {
    return Math.min(ma,Math.max(mi, f));
  }
  
  public static double ba_band(double a,double g, double x) {
    if(x==0) {
      return 0;
    } else if(x>0) {
      return x*a;
    } else if(x<0) {
      double r = x-a*0.5/g;
      return -g*r*r + 0.25*a*a/g;
    }
    return 0;
  }
  
  public static double sigmoid(double x, double shift, double scale) {
//      x -> [0,1]
    return 1.0d/(1.0d+Math.exp(-(x-shift)/scale));
  }
//      x -> [-1,1]
  public static double sigmoid2(double x, double shift, double scale) {
    return 2.0d/(1.0d+Math.exp(-(x-shift)/scale))-1.0d;
  }

  public static double linear_sigmoid_zero(double x, double shift, double scale) {
    double tp = (x-shift)/scale;
//    tp<0: y=0
//    0<tp<1: y=x
//    1<tp: y=1
    return tp<=0.0d ? 0.0d : tp>=1.0d ? 1.0d : tp;
  }
  public static void main(String[] args) {
//    for(int k=-20;k<20;k++) {
//      System.out.println("k: " + (double)k/10d +" " + linear_sigmoid_zero((double)k/10d,0,0.5));
//    }
    for(int k=-20;k<20;k++) {
      System.out.println("k: " + k +" " + ba_band(0.05d,0.0025,(double)k)/200);
    }
  }
}
