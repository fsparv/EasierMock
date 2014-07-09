/* 
 * Copyright 2011-2012 Copyright Clearance Center 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.package com.copyright.rup.common.test; 
 */

package com.copyright.easiertest;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Automates testing of simple Java Bean properties.
 * <p/>
 * <p>This class is meant to provide Get/Set testing for simple properties on Beans that conform
 * to the BeanSpecification suggested naming standards (i.e. getFoo/setFoo and isBar/setBar).
 * Indexed properties (i.e. getBaz(3) and setBaz(3, bazObj) ) are not supported. A property is
 * considered "simple" if it has no code other than assignment from a field in the case of a
 * getter, and return of the raw field value as a setter. Properties that perform defensive
 * copying or lazy initialization will be reported as failing, and are not suitable for testing
 * with this class since they have behavior that should be checked by a real unit test. It is
 * also the case that the property is not considered simple unless the name of the field
 * corresponds to the name of the field in the class. This restriction is necessary so that
 * cases where two properties are backed by the same field, or the getter and setter don't
 * use the same field are detected as failures.
 * <p/>
 * <p>This class only supports JUnit for the time being</p>
 * <p/>
 * <p>Enum types and types shown in the {@link #exampleTypes} map are supported.
 *
 * @author gheck
 */

//TODO: make a testNG version, or make this detect the test harness and do the right thing
public class BeanTester {

  @SuppressWarnings("rawtypes")
  private Map<Class, Object> exampleTypes;

  @SuppressWarnings("rawtypes")
  public BeanTester() {
    this.setExampleTypes(new HashMap<Class, Object>());
    getExampleTypes().put(Boolean.TYPE, true);
    getExampleTypes().put(Byte.TYPE, 1);
    getExampleTypes().put(Short.TYPE, 1);
    getExampleTypes().put(Character.TYPE, 'a');
    getExampleTypes().put(Integer.TYPE, 1);
    getExampleTypes().put(Long.TYPE, 1L);
    getExampleTypes().put(Float.TYPE, 1.0f);
    getExampleTypes().put(Double.TYPE, 1.0);
    getExampleTypes().put(String.class, "foo");
    getExampleTypes().put(Date.class, new Date());

  }


  /**
   * Ensure that getters and setters for simple properties have simple behavior. Getters and
   * setters of Properties to be tested should be annotated with {@link SimpleProperty}. It
   * is perfectly acceptable to have simple setter, and have a non-simple getter or
   * vice-a-versa.
   *
   * @param o An instance of the bean to test.
   */
  public void testBean(final Object o) {
    AnnotatedElementAction invokeGetterSetter = new AnnotatedElementAction() {

      @Override
      public void doTo(Method m, Annotation a) {
        m.setAccessible(true);
        try {
          if (m.getName().startsWith("get")) {
            testGetter(m, o, getTestInstance(a));
          }
          if (m.getName().startsWith("set")) {
            testSetter(m, o, getTestInstance(a));
          }
          if (m.getName().startsWith("is")) {
            testBooleanGetter(m, o, getTestInstance(a));
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }

    };
    AnnotationUtil.doToAnnotatedElement(o, invokeGetterSetter, SimpleProperty.class);
  }

  private void testSetter(Method m, Object target, Object value) throws Exception {
    Object val = value;
    if (val == null) {
      Class<?>[] parameterTypes = m.getParameterTypes();
      if (parameterTypes.length == 1) {
        Class<?> pclass = parameterTypes[0];
        val = findTestValue(pclass);
      }
    }
    if (val == null) {
      throw new IllegalArgumentException("Unable to find or create test instance for " + m);
    }
    m.invoke(target, val);
    String propName = propName(m, 3);
    Field f = findField(target, propName);
    f.setAccessible(true);
    Object object = f.get(target);
    assertEquals(val, object);
  }

  String propName(Method m, int prefixLen) {
    String propName = m.getName().toLowerCase().substring(prefixLen, prefixLen + 1);
    propName += m.getName().substring(prefixLen + 1);
    return propName;
  }

  private void testGetter(Method m, Object target, Object value) throws Exception {
    String propName = propName(m, 3);
    doTestGetter(m, target, value, propName);
  }

  private void testBooleanGetter(Method m, Object target, Object value) throws Exception {
    String propName = propName(m, 2);
    doTestGetter(m, target, value, propName);
  }

  private void doTestGetter(Method m, Object target, Object value, String propName)
      throws Exception {
    Object val = value;
    if (val == null) {
      Class<?> returnType = m.getReturnType();
      if (returnType != Void.TYPE) {
        val = findTestValue(returnType);
      }
    }

    if (val == null) {
      throw new IllegalArgumentException("Unable to find or create test instance for " + m);
    }
    Field f = findField(target, propName);
    f.setAccessible(true);
    f.set(target, val);
    Object invoke = m.invoke(target, (Object[]) null);
    assertEquals(val, invoke);
  }

  Object findTestValue(Class<?> pclass) throws IllegalAccessException, NoSuchFieldException {
    Object test;
    test = getExampleTypes().get(pclass);
    if (test == null) {
      if (Number.class.isAssignableFrom(pclass) || Boolean.class.isAssignableFrom(pclass)
          || Date.class.isAssignableFrom(pclass) || String.class.isAssignableFrom(pclass)) {
        @SuppressWarnings("rawtypes")
        Class primEquiv = (Class) pclass.getField("TYPE").get(null);
        test = getExampleTypes().get(primEquiv);
      } else if (pclass.isEnum()) {
        test = pclass.getEnumConstants()[1];
      }
    }
    return test;
  }

  private Object getTestInstance(Annotation a) throws InstantiationException,
      IllegalAccessException {
    if (!(a instanceof SimpleProperty)) {
      return null;
    }
    Class<?> instanceType = ((SimpleProperty) a).testInstance();
    if (instanceType == Void.class) {
      return null;
    } else {
      return instanceType.newInstance();
    }

  }

  Field findField(Object target, String propName) throws NoSuchFieldException {
    Field result = null;
    @SuppressWarnings("rawtypes")
    Class clazz = target.getClass();
    NoSuchFieldException ex = null;
    do {
      try {

        result = clazz.getDeclaredField(propName);
      } catch (NoSuchFieldException e) {
        if (ex == null) {
          ex = e;
        }
        clazz = clazz.getSuperclass();
      }
    } while (result == null && clazz != Object.class);

    if (result == null) {
      throw ex;
    }

    return result;
  }


  /**
   * @return the exampleTypes
   */
  @SuppressWarnings("rawtypes")
  public Map<Class, Object> getExampleTypes() {
    return exampleTypes;
  }


  /**
   * @param exampleTypes the exampleTypes to set
   */
  @SuppressWarnings("rawtypes")
  public void setExampleTypes(Map<Class, Object> exampleTypes) {
    this.exampleTypes = exampleTypes;
  }
}
