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

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.easymock.EasyMock;
import org.easymock.internal.ClassExtensionHelper;
import org.easymock.internal.ClassProxyFactory.MockMethodInterceptor;
import org.junit.After;
import org.junit.Before;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Automates creation and checking of mock objects. Normal usage is as follows:
 * 
 * <ol>
 * <li>Annotate one or more fields with {@link Mock}
 * <li>Call {@link #prepareMocks(Object)} in the constructor of the test class, and pass 'this' as
 * the argument
 * <li>Call {@link #reset()} in the {@link Before} method for the test class.
 * <li>Call {@link #verify()} in the {@link After} method for the test class.
 * <li>In the test method itself, set expectations then call {@link #replay()}, followed by the
 * invocation of the method under test.
 * </ol>
 * 
 * @author gheck
 */
public class EasierMocks {

    private static ThreadLocal<List<Object>> sMocks = new ThreadLocal<>();

    private static ThreadLocal<List<Object>> sNiceMocks = new ThreadLocal<>();

    private static ThreadLocal<MockStates> sState = new ThreadLocal<>();

    private static ThreadLocal<Field> sObjectUnderTest = new ThreadLocal<>();

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
            } catch (IllegalArgumentException | IllegalAccessException e) {
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
                handler = ClassExtensionHelper.getInterceptor(mock).getHandler();
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
            } catch (IllegalArgumentException | IllegalAccessException e) {
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
        private InvocationHandler handler;

        public Interceptor(Callback callback, InvocationHandler handler) {
            super(handler);
            this.callback = (MethodInterceptor) callback;
            this.handler = handler;
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

        @Override
        public InvocationHandler getHandler() {
            return this.handler;
        }

    }

    private enum MockStates {
        AWAIT_EXPECTATIONS,
        AWAIT_METHOD_UNDER_TEST,
        TESTING
    }

}
