/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package TimeSeries;

/**
 *
 * @author nik
 */
public class BarOHLC extends Bar {
  // data info
  private long time =0;
  private double high = -1;
  private double low = 999999999.;
  private double open = -1;
  private double close = -1;
  private long volume = 0;

  public BarOHLC() {
  }

  public double getOpen() {return open;}
  public double getHigh() {return high;}
  public double getLow() {return low;}
  public double getClose() {return close;}
  public long getVolume() {return volume;}
  public long getTime() {return time;}

  public BarOHLC(long ctime, double price, long sz) {
    high = price;
    low = price;
    open = price;
    close = price;
    volume = sz;
    time = ctime;
  }

  public boolean isInformative() {
    return high>=0 && open>=0 && close>=0 && time>0;
  }

  @Override
  public String toString() {
    String msgout= "T:" +Utils.TimeUtils.Time2String(2, time)+
            " O:" + open + " H:" + high + " L:" + low + " C:" + close +
            " sz: " + volume;
    return msgout;
  }

  private void update(double open, double high, double low, double close, long volume ) {
    if(this.open==-1) this.open=open;
    if(this.high<=high) this.high=high;
    if(this.low>=low || this.low==-1) this.low=low;
    this.close=close;
    this.volume+=volume;
  }

  public void update(double price, long sz) {
    if(open==-1) open=price;
    if(price>=high) high = price;
    if(price<=low || low==-1) low = price;
    close = price;
    volume += sz;
  }

}
