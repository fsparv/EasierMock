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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for handling common operations with annotations
 *
 * @author gheck
 */
public class AnnotationUtil {

  private AnnotationUtil() {
  }

  static void doToAnnotatedElement(Object o, AnnotatedElementAction action,
                                   Class<? extends Annotation> annotation) {
    List<Class<?>> classes = new ArrayList<>();
    Class<?> clazz = o.getClass();
    while (clazz != Object.class) {
      classes.add(clazz);
      clazz = clazz.getSuperclass();
    }

    // for simplicity sake we just run through both rather than checking the @Target of the
    // annotation we've been given. Optimize later.
    for (Class<?> c : classes) {
      for (Field f : c.getDeclaredFields()) {
        for (Annotation a : f.getDeclaredAnnotations()) {
          if (annotation == a.annotationType()) {
            action.doTo(f, a);
          }
        }
      }
    }
    for (Class<?> c : classes) {
      for (Method m : c.getDeclaredMethods()) {
        for (Annotation a : m.getDeclaredAnnotations()) {
          if (annotation == a.annotationType()) {
            action.doTo(m, a);
          }
        }
      }
    }
  }
}
