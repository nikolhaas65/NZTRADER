/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Graphs;

import org.jfree.data.xy.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

import org.jfree.util.PublicCloneable;

/**
 * A simple implementation of the {@link OHLCDataset} interface.  This
 * implementation supports only one series.
 */
public class LinkedXYDataset extends AbstractXYDataset
        implements XYDataset, PublicCloneable {

    /** The series key. */
    private Comparable key;

    /** Storage for the data items. */
    private List<TimedDataItem> data;

    private int maxsize;

    /**
     * Creates a new dataset.
     *
     * @param key  the series key.
     * @param data  the data items.
     */
    public LinkedXYDataset(Comparable key, List<TimedDataItem> data) {
        this.key = key;
        this.data = data;
        this.maxsize = -1;
    }

    public LinkedXYDataset(Comparable key, List<TimedDataItem> data, int max_size) {
        this.key = key;
        this.data = data.subList(data.size()-max_size, data.size()-1);
        this.maxsize = max_size;
    }

    public LinkedXYDataset(Comparable key,int max_size) {
        this.key = key;
        this.data = Collections.synchronizedList(new LinkedList<TimedDataItem>());
        this.maxsize = max_size;
    }

    public synchronized void addPoint(Date day,double value) {
        TimedDataItem bar = new TimedDataItem(day,value);
        this.addPoint(bar);
    }

    public synchronized void addPoint(TimedDataItem bar) {
        this.data.add(bar);
        if(this.maxsize>0 && this.data.size()>this.maxsize) {
            this.data.remove(0);
        }
        this.fireDatasetChanged();
    }

    public List<TimedDataItem> getDataset() {
        return data;
    }

    public synchronized TimedDataItem getLastItem() {
        return (TimedDataItem)this.data.get(this.data.size()-1);
    }

    /**
     * Returns the series key.
     *
     * @param series  the series index (ignored).
     *
     * @return The series key.
     */
    public Comparable getSeriesKey(int series) {
        return this.key;
    }

    /**
     * Returns the x-value for a data item.
     *
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     *
     * @return The x-value.
     */
    public Number getX(int series, int item) {
        return new Long(this.data.get(item).getDate().getTime());
    }


    /**
     * Returns the x-value for a data item as a date.
     *
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     *
     * @return The x-value as a date.
     */
    public Date getXDate(int series, int item) {
        return this.data.get(item).getDate();
    }

    /**
     * Returns the y-value.
     *
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     *
     * @return The y value.
     */
    public Number getY(int series, int item) {
        return getValue(series, item);
    }
    public Number getValue(int series, int item) {
        return this.data.get(item).getValue();
    }

    /**
     * Returns the high value.
     *
     * @param series  the series index (ignored).
     * @param item  the item index (zero-based).
     *
     * @return The high value.
     */

    /**
     * Returns the series count.
     *
     * @return 1.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the item count for the specified series.
     *
     * @param series  the series index (ignored).
     *
     * @return The item count.
     */
    public int getItemCount(int series) {
        return this.data.size();
    }

    /**
     * Sorts the data into ascending order by date.
     */
    public void sortDataByDate() {
        Collections.sort(this.data);
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LinkedXYDataset)) {
            return false;
        }
        LinkedXYDataset that = (LinkedXYDataset) obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        if (!(this.data.equals(that.data))) {
            return false;
        }
        return true;
    }

    /**
     * Returns an independent copy of this dataset.
     *
     * @return A clone.
     * @throws CloneNotSupportedException if not supported
     */
    public Object clone() throws CloneNotSupportedException {
        LinkedXYDataset clone = (LinkedXYDataset) super.clone();
        clone.data = Collections.synchronizedList(new LinkedList<TimedDataItem>());
        Collections.copy(clone.data, this.data);
        return clone;
    }

}
