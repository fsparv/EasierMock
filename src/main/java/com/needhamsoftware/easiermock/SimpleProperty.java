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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a getter or setter as being part of a simple property. Simple is defined as a one line
 * implementation setting or returning a field that has the proper name to correspond to the getter
 * or setter. Properties that cannot be get or set in one line (i.e. properties that provide change
 * listener support, or perform validation of input are not considered simple. Properties backed by
 * fields with names that do not match the getter or setter are not simple either.
 * 
 * @author gheck
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleProperty {

    public Class<?> testInstance() default Void.class;
}
