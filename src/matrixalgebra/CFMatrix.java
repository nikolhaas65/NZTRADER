/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrixalgebra;

/**
 *
 * @author nik
 */
public class CFMatrix {

public static void print(double[][] a) {
  int n1=a.length;
  int n2=a[0].length;
  for(int k1=0;k1<n1;k1++) {
    for(int k2=0;k2<n2;k2++) {
      System.out.print(a[k1][k2] + " ");
    }
    System.out.println();
  }
}

public static void print(double[] a) {
  int n1=a.length;
  for(int k1=0;k1<n1;k1++) {
      System.out.print(a[k1] + " ");
  }
  System.out.println();
}


		public static double[][] Invert(double[][] mtx) {
			double[][] ret=CFMatrix.Copy(mtx);
			int n = mtx[0].length;
			ret = Inv.rmatrixinverse(ret,n);
			return ret;
		}

		public static double[][] Copy(double[][] mtx) {
			double[][] ret=new double[mtx.length][mtx[0].length];
			for (int k0=0;k0<mtx.length ;k0++ ) {
				for (int k1=0;k1<mtx[0].length ;k1++ ) {
					ret[k0][k1]=mtx[k0][k1];
				}
			}
			return ret;
		}

		public static double[][] getMatrixSquare(int sz)
		{
			double[][] mtx=new double[sz][sz];
			return mtx;
		}
		public static double[][] getMatrixGeneric(int sz1,int sz2)
		{
			double[][] mtx=new double[sz1][sz2];
			return mtx;
		}

		public static int[] getPivots(int sz1)
		{
			int[] mtx=new int[sz1];
			return mtx;
		}

		public static double[][] getMatrixX4LinearFit(int dim, double[] x)
		{
			double[][] mtx=new double[x.length][dim+1];
			for (int r=0;r<x.length;r++ )
			{
				mtx[r][0]=1;
				for (int c=1;c<=dim;c++ )
				{
					mtx[r][c]=Math.pow(x[r],c);
				}
			}
			return mtx;
		}

		public static double[][] Transpose(double[][] mtx) {
			double[][] ret=new double[mtx[0].length][mtx.length];
			for(int r=0;r<mtx.length;r++)
				for(int c=0;c<mtx[0].length;c++)
					ret[c][r]=mtx[r][c];
			return ret;
		}

		public static double[][] Multiply(double[][] mLeft, double[][] mRight)
		{
			// multiply matrices: Left*Right
			double[][] ret=new double[mLeft.length][mRight[0].length];
			int convN=mLeft[0].length;
			for (int r=0;r<ret.length;r++ )
			{
				for (int c=0;c<ret[0].length ;c++ )
				{
					for (int k=0;k<convN ;k++ )
					{
						ret[r][c]=ret[r][c]+mLeft[r][k]*mRight[k][c];
					}
				}
			}
			return ret;
		}

		public static double[] Multiply(double[][] mLeft, double[] vRight)
		{
			// multiply matrix x vector: Left*Right
			double[] ret=new double[mLeft.length];
			int convN=mLeft[0].length;
			for (int r=0;r<ret.length ;r++ )
			{
					for (int k=0;k<convN ;k++ )
					{
						ret[r]=ret[r]+mLeft[r][k]*vRight[k];
					}
			}
			return ret;
		}

	};
