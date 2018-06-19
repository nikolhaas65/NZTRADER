/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Utils;

import java.io.File;

/**
 *
 * @author nik
 */
public class CanonicalPath {
    public static String getPath() {
        String path="";
        try {
            path=(new File(".")).getCanonicalPath();
            System.out.println(path);
        } catch(Exception ex) {};
        return path;
    }
}
