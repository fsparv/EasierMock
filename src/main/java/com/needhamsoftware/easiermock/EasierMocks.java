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

package com.needhamsoftware.easiermock;

import org.easymock.EasyMock;
import org.easymock.cglib.proxy.Callback;
import org.easymock.cglib.proxy.Enhancer;
import org.easymock.cglib.proxy.Factory;
import org.easymock.cglib.proxy.MethodInterceptor;
import org.easymock.cglib.proxy.MethodProxy;
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
  - Java 6 compatible syntax changes - Copyright Needham Software 2013
  - moved most of contents of this class to EasyMockHelper (2017) further changes recorded there.
 */

@SuppressWarnings("WeakerAccess")
public class EasierMocks {

  private static final MockHelper EASY_MOCK = new EasyMockHelper();
  private static final MockHelper MOCKITO = new MockitoHelper();

  private EasierMocks() {
  }

  public static void prepareMocks(Object o) {
    chooseHelper().prepareMocks(o);
  }

  public static void reset() {
    chooseHelper().reset();
  }

  public static void replay() {
    chooseHelper().replay();
  }

  public static void verify() {
    chooseHelper().verify();
  }

  private static MockHelper chooseHelper() {
    // todo: actual logic to determine which one we should be using.
    if (1 == 1) {
      return EASY_MOCK;
    } else {
      return MOCKITO;
    }
  }

  enum MockStates {
    AWAIT_EXPECTATIONS,
    AWAIT_METHOD_UNDER_TEST,
    TESTING
  }

}
