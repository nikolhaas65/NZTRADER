/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package matrixalgebra;

/**
 *
 * @author nik
 */
public class Trinverse {

    public static double[][] rmatrixtrinverse(double[][] a,
        int n,
        boolean isupper,
        boolean isunittriangular)
    {
        boolean result = true;
        boolean nounit = !isunittriangular;
        int i = 0;
        int j = 0;
        double v = 0;
        double ajj = 0;
        double[] t = new double[n];
        int i_ = 0;

        //
        // Test the input parameters.
        //
        if( isupper )
        {

            //
            // Compute inverse of upper triangular matrix.
            //
            for(j=0; j<=n-1; j++)
            {
                if( nounit )
                {
                    if( a[j][j]==0 )
                    {
                        result = false;
                        if(!result) {
                          System.out.println("rmatrixtrinverse:result false");
                        }
                        return a;
                    }
                    a[j][j] = 1/a[j][j];
                    ajj = -a[j][j];
                }
                else
                {
                    ajj = -1;
                }

                //
                // Compute elements 1:j-1 of j-th column.
                //
                if( j>0 )
                {
                    for(i_=0; i_<=j-1;i_++)
                    {
                        t[i_] = a[i_][j];
                    }
                    for(i=0; i<=j-1; i++)
                    {
                        if( i<j-1 )
                        {
                            v = 0.0;
                            for(i_=i+1; i_<=j-1;i_++)
                            {
                                v += a[i][i_]*t[i_];
                            }
                        }
                        else
                        {
                            v = 0;
                        }
                        if( nounit )
                        {
                            a[i][j] = v+a[i][i]*t[i];
                        }
                        else
                        {
                            a[i][j] = v+t[i];
                        }
                    }
                    for(i_=0; i_<=j-1;i_++)
                    {
                        a[i_][j] = ajj*a[i_][j];
                    }
                }
            }
        }
        else
        {

            //
            // Compute inverse of lower triangular matrix.
            //
            for(j=n-1; j>=0; j--)
            {
                if( nounit )
                {
                    if( a[j][j]==0 )
                    {
                        result = false;
                        if(!result) {
                          System.out.println("rmatrixtrinverse:result false [2]");
                        }
                        return a;
                    }
                    a[j][j] = 1/a[j][j];
                    ajj = -a[j][j];
                }
                else
                {
                    ajj = -1;
                }
                if( j<n-1 )
                {

                    //
                    // Compute elements j+1:n of j-th column.
                    //
                    for(i_=j+1; i_<=n-1;i_++)
                    {
                        t[i_] = a[i_][j];
                    }
                    for(i=j+1; i<=n-1; i++)
                    {
                        if( i>j+1 )
                        {
                            v = 0.0;
                            for(i_=j+1; i_<=i-1;i_++)
                            {
                                v += a[i][i_]*t[i_];
                            }
                        }
                        else
                        {
                            v = 0;
                        }
                        if( nounit )
                        {
                            a[i][j] = v+a[i][i]*t[i];
                        }
                        else
                        {
                            a[i][j] = v+t[i];
                        }
                    }
                    for(i_=j+1; i_<=n-1;i_++)
                    {
                        a[i_][j] = ajj*a[i_][j];
                    }
                }
            }
        }
        return a;
    }
}
