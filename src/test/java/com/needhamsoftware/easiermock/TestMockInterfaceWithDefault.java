/*
 * Copyright 2021 Patrick G. Heck
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

import com.needhamsoftware.easiermock.testObjs.IDefault2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.needhamsoftware.easiermock.EasierMocks.prepareMocks;
import static com.needhamsoftware.easiermock.EasierMocks.replay;
import static com.needhamsoftware.easiermock.EasierMocks.reset;
import static com.needhamsoftware.easiermock.EasierMocks.verify;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

public class TestMockInterfaceWithDefault {

  @ObjectUnderTest
  private IDefault2 interface2;

  public TestMockInterfaceWithDefault() {
    prepareMocks(this);
  }

  @Before
  public void setUp() {
    reset();
  }

  @After
  public void tearDown() {
    verify();
  }

  /**
   * Test to verify that we can mock the default method calling a default method on a parent interface
   */
  @Test
  public void testExtendChainHasDefault1() {
    expect(interface2.hasDefault()).andReturn("foo").anyTimes();
    replay();
    assertEquals("foo", interface2.hasDefault1());
  }

  /**
   * Test to verify that we can mock the default method calling an abstract default method on our own interface
   */
  @Test
  public void testExtendChainHasDefault2() {
    expect(interface2.noDefault2()).andReturn("foo").anyTimes();
    replay();
    assertEquals("foo", interface2.hasDefault2());
  }

  /**
   * Test to verify that we can mock the default method inherited from a parent interface
   */
  @Test
  public void testExtendChainHasDefault() {
    expect(interface2.noDefault()).andReturn("foo").anyTimes();
    replay();
    assertEquals("foo", interface2.hasDefault());
  }

  @Test(expected = AbstractMethodError.class)
  public void testNoDefaultIsAbstract() {
    expect(interface2.noDefault()).andReturn("foo").anyTimes();
    replay();
    assertEquals("foo", interface2.noDefault());
  }

  @Test(expected = AbstractMethodError.class)
  public void testNoDefault2IsAbstract() {
    expect(interface2.noDefault2()).andReturn("foo").anyTimes();
    replay();
    assertEquals("foo", interface2.noDefault2());
  }
}
