/*
 * Math.java
 *
 * Created on 28 June 2005, 17:25
 */
package JNums;

/**
 *
 * @author nik
 */
public class jMath {

  /** Creates a new instance of Math */
  public jMath() {
  }

  public static double[] pchip1(final double[] x, final double[] y, final double xi) throws IllegalArgumentException {
    int N = x.length;
    if (N <= 1) {
      throw new IllegalArgumentException("ERROR: There should be at least two data points! ");
    }
    if (y.length != N) {
      throw new IllegalArgumentException("ERROR: Y and X should have the same length! ");
    }
    double[] dx = new double[N - 1];
    for (int k = 0; k < N - 1; k++) {
      dx[k] = x[k + 1] - x[k];
      if (dx[k] == 0) {
        throw new IllegalArgumentException("ERROR: The data abscisaae (X) should be distinct! ");
      }
    }
    double[] del = new double[N - 1];
    for (int k = 0; k < N - 1; k++) {
      del[k] = (y[k + 1] - y[k]) / dx[k];
    }
    double[] yi = new double[4];
    double[] f = new double[4]; // f, f', f'', f'''
    double[] slopes = getCHPSlopes(x, y, dx, del);
    int k = 0;
    if (xi <= x[0]) {
      yi[0] = y[0];
      yi[1] = 0;
      yi[2] = 0;
      yi[3] = 0;
    } else if (xi >= x[N - 1]) {
      yi[0] = y[N - 1];
      yi[1] = 0;
      yi[2] = 0;
      yi[3] = 0;
    } else {
      while (xi > x[k + 1] && k < N - 1) {
        k++;
      }
      f = getCHPValues(k, xi - x[k], x, y, slopes);
      yi[0] = f[0];
      yi[1] = f[1];
      yi[2] = f[2];
      yi[3] = f[3];
    }
    return yi;
  }

  public static double[][] pchip(final double[] x, final double[] y, final double[] xi) throws IllegalArgumentException {
    int N = x.length;
    if (N <= 1) {
      throw new IllegalArgumentException("ERROR: There should be at least two data points! ");
    }
    if (y.length != N) {
      throw new IllegalArgumentException("ERROR: Y and X should have the same length! ");
    }
    double[] dx = new double[N - 1];
    for (int k = 0; k < N - 1; k++) {
      dx[k] = x[k + 1] - x[k];
      if (dx[k] == 0) {
        throw new IllegalArgumentException("ERROR: The data abscisaae (X) should be distinct! ");
      }
    }
    double[] del = new double[N - 1];
    for (int k = 0; k < N - 1; k++) {
      del[k] = (y[k + 1] - y[k]) / dx[k];
    }
    int Ni = xi.length;
    double[][] yi = new double[Ni][4];
    double[] f = new double[4]; // f, f', f'', f'''
    double[] slopes = getCHPSlopes(x, y, dx, del);
    int k = 0;
    for (int i = 0; i < Ni; i++) {
      if (xi[i] <= x[0]) {
        yi[i][0] = y[0];
        yi[i][1] = 0;
        yi[i][2] = 0;
        yi[i][3] = 0;
      } else if (xi[i] >= x[N - 1]) {
        yi[i][0] = y[N - 1];
        yi[i][1] = 0;
        yi[i][2] = 0;
        yi[i][3] = 0;
      } else {
        while (xi[i] > x[k + 1] && k < N - 1) {
          k++;
        }
        f = getCHPValues(k, xi[i] - x[k], x, y, slopes);
        yi[i][0] = f[0];
        yi[i][1] = f[1];
        yi[i][2] = f[2];
        yi[i][3] = f[3];
      }
    }
    return yi;
  }

  private static double[] getCHPSlopes(final double[] x, final double[] y, final double[] dx, final double[] del) {
    int N = x.length;
    double[] d = new double[N];
    if (N == 2) {
      d[0] = d[1] = (y[1] - y[0]) / (x[1] - x[0]);
      return d;
    }
    // Slopes at interior points
    for (int k = 1; k < N - 1; k++) {
      if (del[k] * del[k - 1] <= 0) {
        d[k] = 0;
      } else {
        double w1 = (2 * dx[k - 1] + dx[k]) / (3 * (dx[k - 1] + dx[k]));
        double w2 = (2 * dx[k] + dx[k - 1]) / (3 * (dx[k - 1] + dx[k]));
        double dmax = Math.max(Math.abs(del[k - 1]), Math.abs(del[k]));
        double dmin = Math.min(Math.abs(del[k - 1]), Math.abs(del[k]));
        d[k] = dmin / (w1 * del[k - 1] / dmax + w2 * del[k] / dmax);
      }
    }
    // Slopes at end points
    d[0] = ((2 * dx[0] + dx[1]) * del[0] - dx[0] * del[1]) / (dx[0] + dx[1]);
    if (d[0] * del[0] < 0) {
      d[0] = 0;
    } else if (del[0] * del[1] < 0 && Math.abs(d[0]) > Math.abs(3 * del[0])) {
      d[0] = 3 * del[0];
    }

    d[N - 1] = ((2 * dx[N - 2] + dx[N - 3]) * del[N - 2] - dx[N - 2] * del[N - 3]) / (dx[N - 2] + dx[N - 3]);
    if (d[N - 1] * del[N - 2] < 0) {
      d[N - 1] = 0;
    } else if (del[N - 2] * del[N - 3] < 0 && Math.abs(d[N - 1]) > Math.abs(3 * del[N - 2])) {
      d[N - 1] = 3 * del[N - 2];
    }
    return d;
  }

  private static double[] getCHPValues(final int k, final double dx, final double[] x, final double[] y, final double[] slopes) {
    int N = x.length;
    double[] f = new double[4]; // f, f', f'', f'''
    double dx_k = x[k + 1] - x[k];
    double rdx = dx / dx_k;
    double deriv_left;
    double deriv_right;
    double dx_k_dx = dx_k - dx;
    f[0] = ((2 * dx * dx_k_dx * dx_k_dx + dx_k * dx_k_dx * dx_k_dx) / dx_k * y[k] + (2 * dx * dx * dx_k_dx + dx * dx * dx_k) / dx_k * y[k + 1] + dx * dx_k_dx * dx_k_dx * slopes[k] - dx * dx * dx_k_dx * slopes[k + 1]) / dx_k / dx_k;
    f[1] = (6 * dx * dx_k_dx / dx_k * (y[k + 1] - y[k]) + (dx_k_dx * dx_k_dx - 2 * dx * dx_k_dx) * slopes[k] + (dx * dx - 2 * dx * dx_k_dx) * slopes[k + 1]) / dx_k / dx_k;
    f[3] = 12 / dx_k / dx_k * (0.5 * (slopes[k] + slopes[k + 1]) - (y[k + 1] - y[k]) / dx_k);
    f[2] = f[3] * dx + 2 / dx_k * (3 * (y[k + 1] - y[k]) / dx_k - 2 * slopes[k] - slopes[k + 1]);
    return f;
  }

  private static double[] getCHPValues_0(final int k, final double dx, final double[] x, final double[] y) {
    double[] f = new double[4]; // f, f', f'', f'''
    double dx_k = x[k + 1] - x[k];
    double rdx = dx / dx_k;
    if (k == 0) {
      f[0] = (1 - rdx) * y[0] + rdx * y[1];
      f[1] = (y[1] - y[0]) / dx_k;
      f[2] = 0;
      f[3] = 0;
    } else {
      double[] f_d = getCHPValues_0(k - 1, x[k] - x[k - 1], x, y);
      f[0] = (1 - rdx * rdx * rdx) * y[k] + rdx * rdx * rdx * y[k + 1] + dx * (1 - rdx * rdx) * f_d[1] + 0.5 * dx * dx * (1 - rdx) * f_d[2];
      f[3] = 6.0 / dx_k / dx_k / dx_k * (y[k + 1] - y[k] - f_d[1] * dx_k - f_d[2] * 0.5 * dx_k * dx_k);
      f[2] = f[3] * dx + f_d[2];
      f[1] = f[3] * 0.5 * dx * dx + f_d[1] + f_d[2] * dx;
    }
    return f;
  }

  public static double Freq(final double x) {
    // Computation of the normal frequency function freq(x).
    // Freq(x) = (1/sqrt(2pi)) Integral(exp(-t^2/2))dt between -infinity and x
    //
    // translated from CERNLIB C300 by Rene Brun

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
// Numeric integration by Gauss method
//
  static double W[] = {0.1012285362903762591525313543,
    0.2223810344533744705443559944,
    0.3137066458778872873379622020,
    0.3626837833783619829651504493,
    0.02715245941175409485178057246,
    0.06225352393864789286284383699,
    0.09515851168249278480992510760,
    0.1246289712555338720524762822,
    0.1495959888165767320815017305,
    0.1691565193950025381893120790,
    0.1826034150449235888667636680,
    0.1894506104550684962853967232
  };
  static double X[] = {0.9602898564975362316835608686,
    0.7966664774136267395915539365,
    0.5255324099163289858177390492,
    0.1834346424956498049394761424,
    0.9894009349916499325961541735,
    0.9445750230732325760779884155,
    0.8656312023878317438804678977,
    0.7554044083550030338951011948,
    0.6178762444026437484466717640,
    0.4580167776572273863424194430,
    0.2816035507792589132304605015,
    0.09501250983763744018531933543
  };

  public double gauss(REFFUN F, final double A, final double B, final double EPS) {

    double AA, BB, C1, C2, U, S8, S16, CONST;
    //extern double F;
    double DGAUSS1 = 0.;

    int I;
    double dist, edist, tU, tL;
    if (B == A) {
      return DGAUSS1;
    }
    CONST = 0.005 / (B - A);
    BB = A;

    //  COMPUTATIONAL LOOP.
    do {
      AA = BB;
      BB = B;
      boolean over = false;
      do {
        C1 = 0.5 * (BB + AA);
        C2 = 0.5 * (BB - AA);
        S8 = 0.0;
        for (I = 0; I < 4; I++) {
          U = C2 * X[I];
          tU = C1 + U;
          tL = C1 - U;
          S8 = S8 + W[I] * (F.function(tU) + F.function(tL));
        }//endfor
        S8 = C2 * S8;
        S16 = 0.0;
        for (I = 4; I < 12; I++) {
          U = C2 * X[I];
          tU = C1 + U;
          tL = C1 - U;
          S16 = S16 + W[I] * (F.function(tU) + F.function(tL));
        }//endfor
        S16 = C2 * S16;
        dist = S16 - S8 > 0. ? S16 - S8 : S8 - S16;
        edist = S16 > 0. ? S16 : -S16;
        if (dist <= EPS * (1. + edist)) {
          over = true;
        } else {
          BB = C1;
        }
      } while (CONST * C2 != 0. & !over); //enddo
      if (!over) {
        DGAUSS1 = 0.0;
      } else {
        DGAUSS1 = DGAUSS1 + S16;
      }
    } while (BB != B); //enddo

    return DGAUSS1;

  }

//retain previous function 
//better to make modification
  static public double gauss2(REFFUN F, final int tag, final double A, final double B, final double EPS) {

    double AA, BB, C1, C2, U, S8, S16, CONST;
    //extern double F;
    double DGAUSS1 = 0.;

    int I;
    double dist, edist, tU, tL;
    int tTag = tag;
    if (B == A) {
      return DGAUSS1;
    }
    CONST = 0.005 / (B - A);
    BB = A;

    //  COMPUTATIONAL LOOP.
    do {
      AA = BB;
      BB = B;
      boolean over = false;
      do {
        C1 = 0.5 * (BB + AA);
        C2 = 0.5 * (BB - AA);
        S8 = 0.0;
        for (I = 0; I < 4; I++) {
          U = C2 * X[I];
          tU = C1 + U;
          tL = C1 - U;
          S8 = S8 + W[I] * (F.function2(tU, tTag) + F.function2(tL, tTag));
        }//endfor
        S8 = C2 * S8;
        S16 = 0.0;
        for (I = 4; I < 12; I++) {
          U = C2 * X[I];
          tU = C1 + U;
          tL = C1 - U;
          S16 = S16 + W[I] * (F.function2(tU, tTag) + F.function2(tL, tTag));
        }//endfor
        S16 = C2 * S16;
        dist = S16 - S8 > 0. ? S16 - S8 : S8 - S16;
        edist = S16 > 0. ? S16 : -S16;
        if (dist <= EPS * (1. + edist)) {
          over = true;
        } else {
          BB = C1;
        }
      } while (CONST * C2 != 0. && !over); //enddo
      if (!over) {
        DGAUSS1 = 0.0;
      } else {
        DGAUSS1 = DGAUSS1 + S16;
      }
    } while (BB != B); //enddo

    return DGAUSS1;

  }
  
  public static double round(double val, double f) {
    return (Math.round(val/f))/(1.0d/f);
  }
  
  static public void main(String[] args) {
//    System.out.println("freq " + (Freq(3)*2-1));
//    System.out.println("freq " + (Freq(-3)*2-1));
    
    System.out.println("round: " + 1./7.);
    System.out.println("round: " + jMath.round(1.0d/7.0d,1e-4)  );
  }
}
