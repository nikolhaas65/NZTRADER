/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrixalgebra;
import java.util.*;

/**
 *
 * @author nik
 */
public class Inv {

    public static double[][] rmatrixinverse(double[][] a,
        int n)
    {
        int[] pivots = null;
        RetMatObject ret = LU.rmatrixlu(a, n, n, pivots);
        a=ret.a;
        pivots=ret.pivots;
        RetMatObject ret2 = rmatrixluinverse(a,pivots, n);
        return ret2.a;
    }

    public static RetMatObject rmatrixluinverse(double[][] a,
        int[] pivots,
        int n)
    {
      RetMatObject ret=new RetMatObject();
      ret.a=a;
      ret.pivots=pivots;

        if( n==0 ) return ret;

        boolean result = true;
        int i = 0;
        int iws = 0;
        int j = 0;
        int jb = 0;
        int jj = 0;
        int jp = 0;
        double v = 0;
        int i_ = 0;

        //
        // Quick return if possible
        //

        double[] work = new double[n];

        //
        // Form inv(U)
        //
        a = Trinverse.rmatrixtrinverse(a, n, true, false);
        boolean ress=true;
        for(int ka=0;ka<a.length;ka++)
          if(a[ka][ka]==0) ress=false;
        if(!ress)
        {
            return ret;
        }

        //
        // Solve the equation inv(A)*L = inv(U) for inv(A).
        //
        for(j=n-1; j>=0; j--)
        {

            //
            // Copy current column of L to WORK and replace with zeros.
            //
            for(i=j+1; i<=n-1; i++)
            {
                work[i] = a[i][j];
                a[i][j] = 0;
            }

            //
            // Compute current column of inv(A).
            //
            if( j<n-1 )
            {
                for(i=0; i<=n-1; i++)
                {
                    v = 0.0;
                    for(i_=j+1; i_<=n-1;i_++)
                    {
                        v += a[i][i_]*work[i_];
                    }
                    a[i][j] = a[i][j]-v;
                }
            }
        }

        //
        // Apply column interchanges.
        //
        for(j=n-2; j>=0; j--)
        {
            jp = pivots[j];
            if( jp!=j )
            {
                for(i_=0; i_<=n-1;i_++)
                {
                    work[i_] = a[i_][j];
                }
                for(i_=0; i_<=n-1;i_++)
                {
                    a[i_][j] = a[i_][jp];
                }
                for(i_=0; i_<=n-1;i_++)
                {
                    a[i_][jp] = work[i_];
                }
            }
        }
        return ret;
    }
}
