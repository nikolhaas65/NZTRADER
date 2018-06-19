/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Configurer;

/**
 *
 * @author nik
 */
public class StrategyParameter extends Object {

  enum ParamType {Double, Integer, String};

  String mName = "";
  ParamType mType = ParamType.Double;
  String mValue = "";

  public boolean isDouble() {return mType==ParamType.Double;};
  public boolean isInteger() {return mType==ParamType.Integer;};
  public boolean isString() {return mType==ParamType.String;};
  public boolean isEmpty() {return mValue.isEmpty();};

  public StrategyParameter() {

  }

  public void setName(String name) {
    mName = name;
  }

  public void setValue(String value) {
    mValue = value;
  }

  public String getName() {
    return mName;
  }

  public Object getValue() {
    return this.isDouble()? Double.parseDouble(mValue) :
      this.isInteger()? Integer.parseInt(mValue) :
        mValue;
  }

  public void setType(String type) {
    if(type.equalsIgnoreCase("double")) {
      mType = ParamType.Double;
    } else if(type.equalsIgnoreCase("integer")) {
      mType = ParamType.Integer;
    } else if(type.equalsIgnoreCase("string")) {
      mType = ParamType.String;
    }
  }

  public String toString() {
    String msg= "ParamSet: " + mName + " : " + mType + " : "+ mValue;
    return msg;
  }

}
