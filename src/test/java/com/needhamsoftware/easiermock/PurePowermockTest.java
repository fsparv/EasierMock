package com.needhamsoftware.easiermock;
/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 6/17/13
 * Time: 10:39 AM
 */

import com.google.common.collect.ArrayListMultimap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//import static org.easymock.EasyMock.expect;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ArrayListMultimap.class)
@Ignore // test case for http://code.google.com/p/powermock/issues/detail?id=449&thanks=449&ts=1371519268
public class PurePowermockTest {

  @Test
  public void testPowerMockVsGuava() {
//    ArrayListMultimap map = PowerMock.createMock(ArrayListMultimap.class);
//    expect(map.put("foo", "bar")).andReturn(true);
//    PowerMock.replay(map);
//    map.put("foo", "bar");
  }
}
