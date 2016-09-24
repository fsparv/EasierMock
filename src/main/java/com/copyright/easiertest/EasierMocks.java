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

import org.easymock.EasyMock;
import org.easymock.cglib.proxy.Callback;
import org.easymock.cglib.proxy.Enhancer;
import org.easymock.cglib.proxy.Factory;
import org.easymock.cglib.proxy.MethodInterceptor;
import org.easymock.cglib.proxy.MethodProxy;
import org.easymock.internal.ClassProxyFactory;
import org.easymock.internal.ClassProxyFactory.MockMethodInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Automates creation and checking of mock objects. Normal usage is as follows:
 * <ol>
 * <li>Annotate one or more fields with {@link Mock}
 * <li>Call {@link #prepareMocks(Object)} in the constructor of the test class, and pass 'this' as
 * the argument
 * <li>Call {@link #reset()} in the @Before method for the test class.
 * <li>Call {@link #verify()} in the @After method for the test class.
 * <li>In the test method itself, set expectations then call {@link #replay()}, followed by the
 * invocation of the method under test.
 * </ol>
 *
 * @author gheck
 */

/*
  Changes vs original Copyright (as per license requirement):
  Java 6 compatible syntax changes - Copyright Needham Software 2013
 */

public class EasierMocks {

  private static ThreadLocal<List<Object>> sMocks = new ThreadLocal<List<Object>>();

  private static ThreadLocal<List<Object>> sNiceMocks = new ThreadLocal<List<Object>>();

  private static ThreadLocal<MockStates> sState = new ThreadLocal<MockStates>();

  private static ThreadLocal<Field> sObjectUnderTest = new ThreadLocal<Field>();

  private EasierMocks() {
  }

  public static void prepareMocks(Object o) {
    sState.set(MockStates.AWAIT_EXPECTATIONS);

    List<Object> mockList = new ArrayList<Object>();
    List<Object> niceMockList = new ArrayList<Object>();
    sMocks.set(mockList);
    sNiceMocks.set(niceMockList);
    sObjectUnderTest.set(null);

    final List<Field> niceFields = new ArrayList<Field>();
    final List<Field> fields = new ArrayList<Field>();
    AnnotatedElementAction record = new AnnotatedElementAction() {

      @Override
      public void doTo(Field f, Annotation a) {
        recordField(niceFields, fields, f, a);
      }
    };

    AnnotationUtil.doToAnnotatedElement(o, record, Mock.class);
    for (Field f : fields) {
      try {
        Object mock = org.easymock.EasyMock.createMock(f.getType());
        if (niceFields.contains(f)) {
          mock = org.easymock.EasyMock.createNiceMock(f.getType());
          niceMockList.add(mock);
        }
        mockList.add(mock);
        f.setAccessible(true);
        f.set(o, mock);
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

    AnnotatedElementAction prepareTestObj = new AnnotatedElementAction() {

      @Override
      public void doTo(Field f, Annotation a) {
        sObjectUnderTest.set(f);
      }

    };

    AnnotationUtil.doToAnnotatedElement(o, prepareTestObj, ObjectUnderTest.class);
    Field testObjField = sObjectUnderTest.get();
    if (testObjField != null) {
      testObjField.setAccessible(true);
      Factory mock = (Factory) org.easymock.EasyMock.createMock(testObjField.getType());
      InvocationHandler handler;

      // if block lifted from easymock ClassExtensionHelper.getControl()
      // We need to be sure that when ClassExtensionHelper runs this and
      // gets our interceptor instead of the regular one it gets the same
      // answer.
      if (Proxy.isProxyClass(mock.getClass())) {
        handler = Proxy.getInvocationHandler(mock);
      } else if (Enhancer.isEnhanced(mock.getClass())) {
        try {
          Field f = MockMethodInterceptor.class.getDeclaredField("handler");
          f.setAccessible(true);
          handler = (InvocationHandler) f.get(mock.getCallback(0));
        } catch (NoSuchFieldException e) {
          throw new RuntimeException("crap handler field changed (probably means you tried to upgrade easymock to a version that is not yet supported)");
        } catch (IllegalAccessException e) {
          throw new RuntimeException("Something blocked us from accessing the handler field.");
        }
      } else {
        throw new IllegalArgumentException("Not a mock: " + mock.getClass().getName());
      }

      // easy mock always sets one method intercepter callback. If they change that
      // this breaks...
      Interceptor customInterceptor = new Interceptor(mock.getCallback(0), handler);
      mock.setCallback(0, customInterceptor);
      mockList.add(mock);
      try {
        testObjField.set(o, mock);
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static void recordField(List<Field> niceFields, List<Field> fields, Field f, Annotation a) {
    fields.add(f);
    if (((Mock) a).nice()) {
      niceFields.add(f);
    }
  }

  public static void reset() {
    sState.set(MockStates.AWAIT_EXPECTATIONS);
    EasyMock.resetToDefault(mocks());
    EasyMock.resetToNice(niceMocks());
  }

  public static void replay() {
    EasyMock.replay(mocks());
    sState.set(MockStates.AWAIT_METHOD_UNDER_TEST);
  }

  public static void verify() {
    EasyMock.verify(mocks());
  }

  private static Object[] mocks() {
    return sMocks.get().toArray();
  }

  private static Object[] niceMocks() {
    return sNiceMocks.get().toArray();
  }

  private static final class Interceptor extends MockMethodInterceptor {

    private static final long serialVersionUID = 1L;

    private MethodInterceptor callback;

    Interceptor(Callback callback, InvocationHandler handler) {
      super(handler);
      this.callback = (MethodInterceptor) callback;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable {
      if (sState.get() == MockStates.AWAIT_METHOD_UNDER_TEST) {
        sState.set(MockStates.TESTING);
        return proxy.invokeSuper(obj, args);
      } else {
        return callback.intercept(obj, method, args, proxy);
      }
    }

  }

  private enum MockStates {
    AWAIT_EXPECTATIONS,
    AWAIT_METHOD_UNDER_TEST,
    TESTING
  }

}
