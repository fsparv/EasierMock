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

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.nameMatches;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.DefaultMethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import org.easymock.EasyMock;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

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
  Support for Easymock 3.4+
  Support for Java 8 Default interfaces.
 */

public class EasierMocks {

  private static final ThreadLocal<List<Object>> sMocks = new ThreadLocal<>();

  private static final ThreadLocal<List<Object>> sNiceMocks = new ThreadLocal<>();

  private static final ThreadLocal<MockStates> sState = new ThreadLocal<>();

  private static final ThreadLocal<Field> sObjectUnderTest = new ThreadLocal<>();

  private EasierMocks() {
  }

  public static void prepareMocks(Object o) {
    sState.set(MockStates.AWAIT_EXPECTATIONS);

    List<Object> mockList = new ArrayList<>();
    List<Object> niceMockList = new ArrayList<>();
    sMocks.set(mockList);
    sNiceMocks.set(niceMockList);
    sObjectUnderTest.remove();

    final List<Field> niceFields = new ArrayList<>();
    final List<Field> fields = new ArrayList<>();
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


      // Now let EasyMock do its thing, which will *ALWAYS* be a cglib enhanced subclass of the above
      // dynamic implementation produced by ByteBuddy
      Object mock = EasyMock.createMock(testObjField.getType());

      Interceptor target = new Interceptor(mock);
      Class<?> type = dynamicSubclass(testObjField.getType(), target);

      Objenesis objenesis = new ObjenesisStd();
      ObjectInstantiator<?> thingyInstantiator = objenesis.getInstantiatorOf(type);
      Object easierMock = thingyInstantiator.newInstance();

      mockList.add(mock);
      try {
        testObjField.setAccessible(true);
        testObjField.set(o, easierMock);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private static Class<?> dynamicSubclass(Class<?> type1, Interceptor target) {
    ByteBuddy byteBuddy = new ByteBuddy();
    DynamicType.Builder<?> subclass;
    // this dispatch on isInterface has not helped...  it seems that bytebuddy is not intercepting interface methods.
    if (type1.isInterface()) {
      subclass = byteBuddy.subclass(Object.class).implement(type1).name("dynImplOf$$"+type1.getName());
    } else {
      subclass = byteBuddy.subclass(type1);
    }

    DynamicType.Builder.MethodDefinition.ImplementationDefinition<?> method = subclass
        .method(ElementMatchers.noneOf(not(isMethod()), nameMatches("\\$jacocoInit")));
    DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> intercept;
    if (type1.isInterface()) {
      intercept = method.intercept(DefaultMethodCall.unambiguousOnly());
    } else {
      intercept = method.intercept(MethodDelegation.to(target));
    }

    return intercept
        .make()
        .load(type1.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
        .getLoaded();
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
    sObjectUnderTest.remove();
  }

  private static Object[] mocks() {
    return sMocks.get().toArray();
  }

  private static Object[] niceMocks() {
    return sNiceMocks.get().toArray();
  }

  public static class Interceptor implements InvocationHandler {

    public Interceptor(Object originalMock) {
      this.originalMock = originalMock;
    }

    private final Object originalMock;

    @RuntimeType
    @BindingPriority(BindingPriority.DEFAULT * 2)
    public Object intercept(@This Object self,
                            @Origin Method method,
                            @AllArguments Object[] args,
                            @SuperMethod Method superMethod) throws Throwable {
      System.out.println("intercepting");
      if (sState.get() == MockStates.AWAIT_METHOD_UNDER_TEST) {
        sState.set(MockStates.TESTING);
        return superMethod.invoke(self, args);
      } else {
        return method.invoke(originalMock, args);
      }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      // todo: use as invocation handler, but legal ways of invoking default method in java 8/9/16 vary?
      //  could imply MR jar, but ugh.
      return null;
    }
  }

  private enum MockStates {
    AWAIT_EXPECTATIONS,
    AWAIT_METHOD_UNDER_TEST,
    TESTING
  }

}
