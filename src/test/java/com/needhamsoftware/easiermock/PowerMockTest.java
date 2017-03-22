package com.needhamsoftware.easiermock;

import com.google.common.collect.ArrayListMultimap;
import com.needhamsoftware.easiermock.testObjs.Node2;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.needhamsoftware.easiermock.EasierMocks.*;
import static org.easymock.EasyMock.expect;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ArrayListMultimap.class)
@Ignore // this was used to find http://code.google.com/p/powermock/issues/detail?id=449&thanks=449&ts=1371519268
public class PowerMockTest {

  private Node2 node = new Node2();
  @Mock private Node2 child;
  @Mock ArrayListMultimap<String,String> multimapMock;

  public PowerMockTest() {
    prepareMocks(this);
  }

  /**
   * This test fails with an stack overflow without power mock support.
   */
  @Test
  public void testAddNode() {
    reset();
    child.setParent(node);
    expect(child.getMap()).andReturn(multimapMock);
    expect(multimapMock.put("foo","bar")).andReturn(true);
    replay();
    node.addNode(child);
    verify();
  }

}
