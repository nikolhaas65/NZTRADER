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
public class LU {
    public static final int lunb = 8;

    public static RetMatObject rmatrixlu(double[][] a,
        int m,
        int n,
        int[] pivots)
    {

      RetMatObject ret=new RetMatObject();
      ret.a=a;
      ret.pivots=pivots;

        double[][] b = null;
        double[] t = null;
        int[] bp = null;
        int minmn = 0;
        int i = 0;
        int ip = 0;
        int j = 0;
        int j1 = 0;
        int j2 = 0;
        int cb = 0;
        int nb = 0;
        double v = 0;
        int i_ = 0;
        int i1_ = 0;

        nb = lunb;

        //
        // Decide what to use - blocked or unblocked code
        //
        if( n<=1 | Math.min(m, n)<=nb | nb==1 )
        {

            //
            // Unblocked code
            //
            ret = rmatrixlu2(a, m, n, pivots);
        }
        else
        {

            //
            // Blocked code.
            // First, prepare temporary matrix and indices
            //
            b = new double[m][nb];
            t = new double[n];
            pivots = new int[Math.min(m, n)];
            minmn = Math.min(m, n);
            j1 = 0;
            j2 = Math.min(minmn, nb)-1;

            //
            // Main cycle
            //
            while( j1<minmn )
            {
                cb = j2-j1+1;

                //
                // LU factorization of diagonal and subdiagonal blocks:
                // 1. Copy columns J1..J2 of A to B
                // 2. LU(B)
                // 3. Copy result back to A
                // 4. Copy pivots, apply pivots
                //
                for(i=j1; i<=m-1; i++)
                {
                    i1_ = (j1) - (0);
                    for(i_=0; i_<=cb-1;i_++)
                    {
                        b[i-j1][i_] = a[i][i_+i1_];
                    }
                }

                ret = rmatrixlu2(b, m-j1, cb,bp);
                a=ret.a;
                pivots=ret.pivots;

                for(i=j1; i<=m-1; i++)
                {
                    i1_ = (0) - (j1);
                    for(i_=j1; i_<=j2;i_++)
                    {
                        a[i][i_] = b[i-j1][i_+i1_];
                    }
                }
                for(i=0; i<=cb-1; i++)
                {
                    ip = bp[i];
                    pivots[j1+i] = j1+ip;
                    if( bp[i]!=i )
                    {
                        if( j1!=0 )
                        {

                            //
                            // Interchange columns 0:J1-1
                            //
                            for(i_=0; i_<=j1-1;i_++)
                            {
                                t[i_] = a[j1+i][i_];
                            }
                            for(i_=0; i_<=j1-1;i_++)
                            {
                                a[j1+i][i_] = a[j1+ip][i_];
                            }
                            for(i_=0; i_<=j1-1;i_++)
                            {
                                a[j1+ip][i_] = t[i_];
                            }
                        }
                        if( j2<n-1 )
                        {

                            //
                            // Interchange the rest of the matrix, if needed
                            //
                            for(i_=j2+1; i_<=n-1;i_++)
                            {
                                t[i_] = a[j1+i][i_];
                            }
                            for(i_=j2+1; i_<=n-1;i_++)
                            {
                                a[j1+i][i_] = a[j1+ip][i_];
                            }
                            for(i_=j2+1; i_<=n-1;i_++)
                            {
                                a[j1+ip][i_] = t[i_];
                            }
                        }
                    }
                }

                //
                // Compute block row of U
                //
                if( j2<n-1 )
                {
                    for(i=j1+1; i<=j2; i++)
                    {
                        for(j=j1; j<=i-1; j++)
                        {
                            v = a[i][j];
                            for(i_=j2+1; i_<=n-1;i_++)
                            {
                                a[i][i_] = a[i][i_] - v*a[j][i_];
                            }
                        }
                    }
                }

                //
                // Update trailing submatrix
                //
                if( j2<n-1 )
                {
                    for(i=j2+1; i<=m-1; i++)
                    {
                        for(j=j1; j<=j2; j++)
                        {
                            v = a[i][j];
                            for(i_=j2+1; i_<=n-1;i_++)
                            {
                                a[i][i_] = a[i][i_] - v*a[j][i_];
                            }
                        }
                    }
                }

                //
                // Next step
                //
                j1 = j2+1;
                j2 = Math.min(minmn, j1+nb)-1;
            }
        }
        return ret;
    }

    /*************************************************************************
    Level 2 BLAS version of RMatrixLU

      -- LAPACK routine (version 3.0) --
         Univ. of Tennessee, Univ. of California Berkeley, NAG Ltd.,
         Courant Institute, Argonne National Lab, and Rice University
         June 30, 1992
    *************************************************************************/
    private static RetMatObject rmatrixlu2(double[][] a,
        int m,
        int n,
        int[] pivots)
    {
      RetMatObject ret=new RetMatObject();
      ret.a=a;
      ret.pivots=pivots;

        int i = 0;
        int j = 0;
        int jp = 0;
        double[] t1 = null;
        double s = 0;
        int i_ = 0;

        pivots = new int[Math.min(m, n)];
      ret.pivots=pivots;

        t1 = new double[Math.max(m, n)];

        if(m<0 || n<0){
          System.out.println("Error in LUDecomposition: incorrect function arguments");
        };

        //
        // Quick return if possible
        //
        if( m==0 | n==0 )
        {
            return ret;
        }
        for(j=0; j<=Math.min(m, n)-1; j++)
        {

            //
            // Find pivot and test for singularity.
            //
            jp = j;
            for(i=j+1; i<=m-1; i++)
            {
                if( Math.abs(a[i][j])>Math.abs(a[jp][j]) )
                {
                    jp = i;
                }
            }
            pivots[j] = jp;
            if( a[jp][j]!=0 )
            {

                //
                //Apply the interchange to rows
                //
                if( jp!=j )
                {
                    for(i_=0; i_<=n-1;i_++)
                    {
                        t1[i_] = a[j][i_];
                    }
                    for(i_=0; i_<=n-1;i_++)
                    {
                        a[j][i_] = a[jp][i_];
                    }
                    for(i_=0; i_<=n-1;i_++)
                    {
                        a[jp][i_] = t1[i_];
                    }
                }

                //
                //Compute elements J+1:M of J-th column.
                //
                if( j<m )
                {
                    jp = j+1;
                    s = 1/a[j][j];
                    for(i_=jp; i_<=m-1;i_++)
                    {
                        a[i_][j] = s*a[i_][j];
                    }
                }
            }
            if( j<Math.min(m, n)-1 )
            {

                //
                //Update trailing submatrix.
                //
                jp = j+1;
                for(i=j+1; i<=m-1; i++)
                {
                    s = a[i][j];
                    for(i_=jp; i_<=n-1;i_++)
                    {
                        a[i][i_] = a[i][i_] - s*a[j][i_];
                    }
                }
            }
        }
        return ret;
    }
}
