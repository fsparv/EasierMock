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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes an object that is the focus of a unit test. This annotation should be used in conjunction
 * with {@link EasierMocks} when the test author desires to test one and only one method on an
 * object. EasierMocks will populate the field marked with this annotation with a special type of
 * mock that allows a single call to pass through to the underlying type.
 *
 * <p>
 * Method calls issued before the call to {@link EasierMocks#replay()} will be recorded as
 * expectations (and should be prepared with {@link EasyMock#expect(Object)} and a return value
 * where appropriate). The first method invoked after replay() will be executed by the underlying
 * class for the mock. For this reason ONLY fields with CONCRETE CLASS TYPES should be annotated.
 * All subsequent method calls will once again be delegated to the mock infrastructure, and will
 * throw unexpected method call exceptions if they were not called during the record phase.
 *
 * @author gheck
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ObjectUnderTest {
  // just an annotation (comment to quite down ide)
}
