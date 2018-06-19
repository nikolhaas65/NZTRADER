/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JNums;

import Utils.*;

//import java.math.*;
//import org.apache.commons.math.special.*;

/**
 * Helps to calculate statistical measures over samples: means, stdevs 
 * Possibility to filter data is included
 * 
 * @author nik
 */
public class Stats {

  /**
   * Calculation of CDF
   * Computation of the normal frequency function freq(x).
   * <p>
   * Freq(x) = (1/sqrt(2pi)) Integral(exp(-t^2/2))dt between -infinity and x
   * <p>
   * \Phi(x) = \frac{1}{2\pi} \int_(-\infty)^(x) (e^(-frac{t^2}{2})) \cdot dt
   * @param x variable
   * @return freq(x)
   */
    public static double Freq(final double x) {

    double C1 = 0.56418958354775629;
    double W2 = 1.41421356237309505;
    double p10 = 2.4266795523053175e+2;
    double q10 = 2.1505887586986120e+2,
            p11 = 2.1979261618294152e+1, q11 = 9.1164905404514901e+1,
            p12 = 6.9963834886191355e+0, q12 = 1.5082797630407787e+1,
            p13 = -3.5609843701815385e-2, q13 = 1;

    double p20 = 3.00459261020161601e+2, q20 = 3.00459260956983293e+2,
            p21 = 4.51918953711872942e+2, q21 = 7.90950925327898027e+2,
            p22 = 3.39320816734343687e+2, q22 = 9.31354094850609621e+2,
            p23 = 1.52989285046940404e+2, q23 = 6.38980264465631167e+2,
            p24 = 4.31622272220567353e+1, q24 = 2.77585444743987643e+2,
            p25 = 7.21175825088309366e+0, q25 = 7.70001529352294730e+1,
            p26 = 5.64195517478973971e-1, q26 = 1.27827273196294235e+1,
            p27 = -1.36864857382716707e-7, q27 = 1;

    double p30 = -2.99610707703542174e-3, q30 = 1.06209230528467918e-2,
            p31 = -4.94730910623250734e-2, q31 = 1.91308926107829841e-1,
            p32 = -2.26956593539686930e-1, q32 = 1.05167510706793207e+0,
            p33 = -2.78661308609647788e-1, q33 = 1.98733201817135256e+0,
            p34 = -2.23192459734184686e-2, q34 = 1;

    double v = Math.abs(x) / W2;
    double vv = v * v;
    double ap, aq, h, hc, y;
    if (v < 0.5) {
      y = vv;
      ap = p13;
      aq = q13;
      ap = p12 + y * ap;
      ap = p11 + y * ap;
      ap = p10 + y * ap;
      aq = q12 + y * aq;
      aq = q11 + y * aq;
      aq = q10 + y * aq;
      h = v * ap / aq;
      hc = 1 - h;
    } else if (v < 4) {
      ap = p27;
      aq = q27;
      ap = p26 + v * ap;
      ap = p25 + v * ap;
      ap = p24 + v * ap;
      ap = p23 + v * ap;
      ap = p22 + v * ap;
      ap = p21 + v * ap;
      ap = p20 + v * ap;
      aq = q26 + v * aq;
      aq = q25 + v * aq;
      aq = q24 + v * aq;
      aq = q23 + v * aq;
      aq = q22 + v * aq;
      aq = q21 + v * aq;
      aq = q20 + v * aq;
      hc = Math.exp(-vv) * ap / aq;
      h = 1 - hc;
    } else {
      y = 1 / vv;
      ap = p34;
      aq = q34;
      ap = p33 + y * ap;
      ap = p32 + y * ap;
      ap = p31 + y * ap;
      ap = p30 + y * ap;
      aq = q33 + y * aq;
      aq = q32 + y * aq;
      aq = q31 + y * aq;
      aq = q30 + y * aq;
      hc = Math.exp(-vv) * (C1 + y * ap / aq) / v;
      h = 1 - hc;
    }
    if (x > 0) {
      return 0.5 + 0.5 * h;
    } else {
      return 0.5 * hc;
    }
  }//

  public static double nextRandNorm() {
    double U1=Math.random();
    double U2=Math.random();
    return Math.sqrt(-2*Math.log(U2))*Math.cos(2*Math.PI*U1);
  }

  /** 
   * NormInvertedDirection: output = [-1;1];
   * @param x - {@link double} variable distributed with Norm(m,s)
   * @param m - {@link double} mean of Norm(m,s)
   * @param s - {@link double} stdev of Norm(m,s)
   * @return {@link double} 2*Freq(x)-1 corresponding to x given Norm(m,s)
   */
  public static double NormInvertedDirection(double x, double m, double s) {
    double val=0;
    try {
      if(Math.abs(x-m)>0 && Math.abs(s)>0)
        val=Freq((x-m)/s)*2-1;
      else if(x==m)
        val=0.5;
      else
        val=1d;
    } catch (Exception ex) {}
    return val;
  }
  /**
   * Standard deviation of the sample 
   * @param a {@link double}-array of sample data
   * @return stdev of the sample
   */
  public static double stdn(double[] a) {
    long n = a.length;
    double m1 = 0, m2 = 0;
    for (int k = 0; k < n; k++) {
      m1 = +a[k];
      m2 = +a[k] * a[k];
    }
    return Math.sqrt(m2 / n - m1 * m1 / n / n);
  }
  /**
   * 
   * Standard deviation of the sample 
   * @param arr    {@link double}-array of sample data
   * @param filter {@link boolean} filter applied to arr. Both must be of the same size
   * @return {@link double} stdev of the sample
   */
  public static double stdn(double[] arr, boolean[] filter) {
    long n = arr.length;
    double m1 = 0, m2 = 0;
    int nn=0;
    for (int k = 0; k < n; k++) {
      if(!filter[k]) {
        m1 = +arr[k];
        m2 = +arr[k] * arr[k];
        nn++;
      }
    }
    if(nn>0)
      return Math.sqrt(m2 / nn - m1 * m1 / (nn * nn));
    else
      return 0;
  }

  public static double mean(double[] a) {
    double m1 = 0;
    for (int k = 0; k < a.length; k++) {
        m1 = +a[k];
    }
    return m1 / a.length;
  }
  
  public static double mean(double[] a, boolean[] filter) {
    double m1 = 0;
    int nn=0;
    for (int k = 0; k < a.length; k++) {
      if(!filter[k]) {
        m1 = +a[k];
        nn++;
      }
    }
    if (nn>0)
      return m1 / nn;
    else
      return -9999999;
  }
  
  public static double[] getCutAverage( 
          int filter,int[] call_put_flag, boolean[] good,
          double cut, double shift_mean, double[] val) 
		{
    /* Average of sample selected by:
     * good=true & 
     * call (filetr =1) or put (filter=-1) or all (filter = 0) &
     * |val-shift|<cut
     */
			double average=0;
			double average_2=0;

			int n=0;
			for (int k=0;k<call_put_flag.length;k++ ) {
				if (good[k]==true && 
                (call_put_flag[k]==filter || filter==0) && 
                Math.abs(val[k]-shift_mean)<cut) {
					average=average+val[k];
					average_2=average_2+val[k]*val[k];
					n++;
				}
			}
      if (n>0)
        return new double[] {average/n, average_2/n-average*average/n/n,(double)n};
      else
        return new double[] {0,0,-1};
		}

  static public void main(String[] args) {
    System.out.println(Stats.NormInvertedDirection(-3, 0, 1));
    System.out.println(Stats.Freq(10));
  }
  
}
