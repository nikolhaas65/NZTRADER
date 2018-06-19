/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JNums;

/**
 *
 * @author nik
 */
public class BAPriceLevel {

  public static double getEstimPriceLevel(int direction, double bid, double ask,
          double[] mid_estim, double[] sprd_estim, double fba) {
    double best_bid = Math.min(bid, mid_estim[0] - sprd_estim[0] / 2 - sprd_estim[1]);
    double best_ask = Math.max(ask, mid_estim[0] + sprd_estim[0] / 2 + sprd_estim[1]);
    return getPriceLevel(direction, best_bid, best_ask, fba);
  }

  public static double getEstimPriceLevel(int direction, double bid, double ask,
          double mid_estim, double sprd_estim, double fba) {
    double best_bid = Math.min(bid, mid_estim - sprd_estim / 2);
    double best_ask = Math.max(ask, mid_estim + sprd_estim / 2);
    return getPriceLevel(direction, best_bid, best_ask, fba);
  }

  public static double getPriceLevel(int direction, double dX, double uX, double fba) {
    // fba=[0;1]
    return getPriceLevel((double)direction,dX,uX,fba);
  }
  //double
  public static double getPriceLevel(double direction, double dX, double uX, double fba) {
    // fba=[0;1]
    // fba=0 price=dX; fba=1 price=uX

    if (fba >=1d) {
      fba = 1d;
      return direction<0?dX:direction>0?uX:0;
    } else if (fba <= 0d) {
      fba = 0d;
      return direction>0?dX:direction<0?uX:0;
    } else if(Math.abs(fba)<1) {
      return (uX + dX) * 0.5 + Math.signum(direction) * (fba - 0.5) * (uX - dX);
    }
    return 0d;
  }

  public static double getEstimPriceLevel(double direction, double bid, double ask,
          double mid_estim, double sprd_estim, double fba) {
    double best_bid = Math.min(bid, mid_estim - sprd_estim / 2);
    double best_ask = Math.max(ask, mid_estim + sprd_estim / 2);
    return getPriceLevel(direction, best_bid, best_ask, fba);
  }

  static Utils.NumberRenderer df = new Utils.NumberRenderer(3,3);

  static public void main(String[] args) {
    for(int k=0;k<11;k++) {
      double fba = k/10d;
      System.out.println("["+ -1 + ":"+ 1 +"] "+fba +
              " [" + df.format(getEstimPriceLevel(1d,-1d,1d,0d,1.d,fba)) +
              ":" + df.format(getEstimPriceLevel(-1d,-1d,1d,0d,1.d,fba)) + "]");
    }
  }

}

