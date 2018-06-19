package Utils;

import Utils.NumberFormatterFactory;
import javax.swing.table.*;
import java.text.*;

public class NumberRenderer extends DefaultTableCellRenderer {
    public final DecimalFormat df;

    public NumberRenderer(int precision) {
        df = NumberFormatterFactory.getNumberFormatter(precision);
        setHorizontalAlignment(RIGHT);
    }
    public NumberRenderer(int precisionMin, int precisionMax) {
        df = NumberFormatterFactory.getNumberFormatter(precisionMin, precisionMax);
        setHorizontalAlignment(RIGHT);
    }

    @Override
    public void setValue(Object value) {
        String text = "";
        if (value != null) {
            boolean validNumber;
            boolean isDouble = (value.getClass() == Double.class);
            if (isDouble) {
                Double d = (Double) value;
                validNumber = !(Double.isInfinite(d) || Double.isNaN(d));
            } else {
                validNumber = true;
            }
            if (validNumber) {
                text = df.format(value);
            }
        }

        setText(text);
    }
    
    public String format(double value) {
      return df.format(value);
    }
    
    static public void  main(String[] args) {
      NumberRenderer df = new NumberRenderer(6,6);
      System.out.println(df.format(3.234456677));
      System.out.println(df.format(3));
    }
    
}
