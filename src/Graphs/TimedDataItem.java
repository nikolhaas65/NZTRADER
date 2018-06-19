/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2008, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * -----------------
 * OHLCDataItem.java
 * -----------------
 * (C) Copyright 2003-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 03-Dec-2003 : Version 1 (DG);
 * 29-Apr-2005 : Added equals() method (DG);
 *
 */

package Graphs;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single (open-high-low-close) data item in
 * an {@link LinkedOHLCDataset}.  This data item is commonly used
 * to summarise the trading activity of a financial commodity for
 * a fixed period (most often one day).
 */
public class TimedDataItem implements Comparable, Serializable {

    /**
     *  For serialization
     */
    private static final long serialVersionUID = 7753817154401169901L;

    /** The date. */
    private Date date;

    /** The open value. */
    private Number value;

    /**
     * Creates a new item.
     *
     * @param date  the date (<code>null</code> not permitted).
     * @param val value
     */
    public TimedDataItem(Date date,
                        double val) {
        if (date == null) {
            throw new IllegalArgumentException("Null 'date' argument.");
        }
        this.date = date;
        this.value = new Double(val);
    }

    /**
     * Returns the date that the data item relates to.
     *
     * @return The date (never <code>null</code>).
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Returns the open value.
     *
     * @return The open value.
     */
    public Number getValue() {
        return this.value;
    }

    /**
     * Checks this instance for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TimedDataItem)) {
            return false;
        }
        TimedDataItem that = (TimedDataItem) obj;
        if (!this.date.equals(that.date)) {
            return false;
        }
        if (!this.value.equals(that.value)) {
            return false;
        }
        return true;
    }

    /**
     * Compares this object with the specified object for order. Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * @param object  the object to compare to.
     *
     * @return A negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Object object) {
        if (object instanceof TimedDataItem) {
            TimedDataItem item = (TimedDataItem) object;
            return this.date.compareTo(item.date);
        }
        else {
            throw new ClassCastException("OHLCDataItem.compareTo().");
        }
    }

}
