package com.needhamsoftware.easiermock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BeanTesterTest {

  @Test
  public void testGoodProps() {
    BeanTester tester = new BeanTester();
    tester.getExampleTypes().put(GoodTestBean.class, new GoodTestBean());
    tester.getExampleTypes().put(List.class, new ArrayList<>());
    tester.getExampleTypes().put(NoDefaultConstructor.class, new NoDefaultConstructor("foo"));
    GoodTestBean testBean = new GoodTestBean();
    tester.testBean(testBean);
  }

  @Test(expected = RuntimeException.class)
  public void testMissingObjectPropType() {
    BeanTester tester = new BeanTester();
    tester.getExampleTypes().put(List.class, new ArrayList<>());
    GoodTestBean testBean = new GoodTestBean();
    tester.testBean(testBean);
  }

  @Test
  public void testMissingObjectPropNewInstanceType() {
    BeanTester tester = new BeanTester();
    tester.getExampleTypes().put(GoodTestBean.class, new GoodTestBean());
    tester.getExampleTypes().put(NoDefaultConstructor.class, new NoDefaultConstructor("foo"));
    GoodTestBean testBean = new GoodTestBean();
    tester.testBean(testBean);
  }

  @Test(expected = RuntimeException.class)
  public void testMissingObjectPropNewInstanceNotPossible() {
    BeanTester tester = new BeanTester();
    tester.getExampleTypes().put(GoodTestBean.class, new GoodTestBean());
    GoodTestBean testBean = new GoodTestBean();
    tester.testBean(testBean);
  }

  @Test(expected = RuntimeException.class)
  public void testBadlyNamedGetterFails() {
    BeanTester tester = new BeanTester();
    BadPropNameGetterBean testBean = new BadPropNameGetterBean();
    tester.testBean(testBean);
  }

  @Test(expected = RuntimeException.class)
  public void testBadlyNamedSetterFails() {
    BeanTester tester = new BeanTester();
    BadPropNameSetterBean testBean = new BadPropNameSetterBean();
    tester.testBean(testBean);
  }

  @Test(expected = RuntimeException.class)
  public void testSetterMoreThanOneArg() {
    BeanTester tester = new BeanTester();
    SetterTooManyArgs testBean = new SetterTooManyArgs();
    tester.testBean(testBean);
  }

  @SuppressWarnings("unused")
  static class GoodTestBean {
    @SimpleProperty
    public byte getSimpleByte() {
      return simpleByte;
    }

    @SimpleProperty
    public void setSimpleByte(byte simpleByte) {
      this.simpleByte = simpleByte;
    }

    @SimpleProperty
    public char getSimpleChar() {
      return simpleChar;
    }

    @SimpleProperty
    public void setSimpleChar(char simpleChar) {
      this.simpleChar = simpleChar;
    }

    @SimpleProperty
    public short getSimpleShort() {
      return simpleShort;
    }

    @SimpleProperty
    public void setSimpleShort(short simpleShort) {
      this.simpleShort = simpleShort;
    }

    @SimpleProperty
    public int getSimpleInteger() {
      return simpleInteger;
    }

    @SimpleProperty
    public void setSimpleInteger(int simpleInteger) {
      this.simpleInteger = simpleInteger;
    }

    @SimpleProperty
    public long getSimpleLong() {
      return simpleLong;
    }

    @SimpleProperty
    public void setSimpleLong(long simpleLong) {
      this.simpleLong = simpleLong;
    }

    @SimpleProperty
    public float getSimpleFloat() {
      return simpleFloat;
    }

    @SimpleProperty
    public void setSimpleFloat(float simpleFloat) {
      this.simpleFloat = simpleFloat;
    }

    @SimpleProperty
    public double getSimpleDouble() {
      return simpleDouble;
    }

    @SimpleProperty
    public void setSimpleDouble(double simpleDouble) {
      this.simpleDouble = simpleDouble;
    }

    @SimpleProperty
    public String getSimpleString() {
      return simpleString;
    }

    @SimpleProperty
    public void setSimpleString(String simpleString) {
      this.simpleString = simpleString;
    }

    @SimpleProperty
    public GoodTestBean getSimpleObject() {
      return simpleObject;
    }

    @SimpleProperty
    public void setSimpleObject(GoodTestBean simpleObject) {
      this.simpleObject = simpleObject;
    }

    @SimpleProperty
    public boolean isSimpleBoolean() {
      return simpleBoolean;
    }

    @SimpleProperty
    public Date getSimpleDate() {
      return simpleDate;
    }

    @SimpleProperty
    public void setSimpleDate(Date simpleDate) {
      this.simpleDate = simpleDate;
    }

    @SimpleProperty
    public void setSimpleBoolean(boolean simpleBoolean) {
      this.simpleBoolean = simpleBoolean;
    }

    // this prop only has setter intentionally
    @SimpleProperty(testInstance = ArrayList.class)
    public void setListProp(List<Object> listProp) {
      this.listProp = listProp;
    }

    @SimpleProperty
    public void setNoDefaultConstructorObject(NoDefaultConstructor noDefaultConstructorObject) {
      this.noDefaultConstructorObject = noDefaultConstructorObject;
    }

    boolean simpleBoolean;
    byte simpleByte;
    char simpleChar;
    short simpleShort;
    int simpleInteger;
    long simpleLong;
    float simpleFloat;
    double simpleDouble;
    String simpleString;


    Date simpleDate;
    GoodTestBean simpleObject;
    List<Object> listProp;
    NoDefaultConstructor noDefaultConstructorObject;

  }

  @SuppressWarnings("unused")
  static class BadPropNameGetterBean {
    @SimpleProperty
    public int getIntProp() {
      return anInt;
    }

    @SimpleProperty
    public void setAnInt(int intProp) {
      this.anInt = intProp;
    }

    int anInt;
  }

  @SuppressWarnings("unused")
  static class BadPropNameSetterBean {
    @SimpleProperty
    public int getAnInt() {
      return anInt;
    }

    @SimpleProperty
    public void setintProp(int intProp) {
      this.anInt = intProp;
    }

    int anInt;
  }

  static class NoDefaultConstructor {
    String stringProp;

    NoDefaultConstructor(String stringProp) {
      this.stringProp = stringProp;
    }

  }

  @SuppressWarnings("unused")
  static class SetterTooManyArgs {
    @SimpleProperty
    public int getAnInt() {
      return anInt;
    }

    @SimpleProperty
    public void setAnInt(int intProp, boolean flag) {
      this.anInt = intProp;
    }

    int anInt;

  }
}
