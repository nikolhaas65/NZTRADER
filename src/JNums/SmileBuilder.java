/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package JNums;
import java.util.*;

import matrixalgebra.*;

/**
 *
 * @author nik
 */
public class SmileBuilder {

  private double[] strikes=null;
  private double[] vols = null;

  public SmileBuilder(double[] v_str, double[] v_vols) {
    strikes = new double[v_str.length];
    vols = new double[v_vols.length];
    if(strikes.length==vols.length) {
      for(int k=0;k<vols.length;k++) {
        strikes[k]=v_str[k];
        vols[k]=v_vols[k];
      }
    } else {
      System.out.println("ERROR:SmileBuilder:constructor: vectors are of different length\n No initiation\n exit");
      System.exit(0);
    }
  }

  public static double[] fitPolyN(int npoly,double[] x, double[] y)
  {
//    fit of a.x^2 + b.x + c = y
//    C := [c,b,a]
    double[][] X=CFMatrix.getMatrixX4LinearFit(npoly,x);
    double[][] Xt=CFMatrix.Transpose(X);
    double[][] XtX = CFMatrix.Multiply(Xt,X);
//    CFMatrix.print(XtX);
    double[][] invXtX = CFMatrix.Invert(XtX);
    double[] Xty = CFMatrix.Multiply(Xt,y);

    double[] C = CFMatrix.Multiply(invXtX,Xty);
    return C;
  }

  public static void main(String[] args) {

    double[][] m = {{3,1,2},{3,4,5},{2,0,3}};

    List<Integer> aa = new ArrayList<Integer>();
    System.out.println("aa "+ aa.size());

    CFMatrix.print(m);
//    System.out.println("Cpy====");
//    double[][] mcpy = CFMatrix.Copy(m);
//    CFMatrix.print(mcpy);
    System.out.println("Inv====");
    double[][] minv = CFMatrix.Invert(m);
    CFMatrix.print(minv);
    System.out.println("Inv(Inv)====");
    CFMatrix.print(CFMatrix.Invert(minv));

    System.out.println("Fit====");
    double[] x = {0,1,2,3,4,5};
    double[] y = {-3,0,13,36,69,112};

    double[] ret=fitPolyN(3,x,y);
    System.out.println("a: " + ret[0] + " b: "+ ret[1] + " c: " + ret[2] + " d: " + ret[3]);
  }

}
