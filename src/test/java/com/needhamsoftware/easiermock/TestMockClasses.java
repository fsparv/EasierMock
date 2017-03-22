package com.needhamsoftware.easiermock;

import com.needhamsoftware.easiermock.testObjs.Node;
import com.needhamsoftware.easiermock.testObjs.NodeUsingThing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.needhamsoftware.easiermock.EasierMocks.*;
import static org.easymock.EasyMock.expect;

/**
 * Class to make sure we don't break mocking of objects
 */
public class TestMockClasses {

  @ObjectUnderTest
  private NodeUsingThing thing;
  @Mock
  private Node nodeObjectMock;
  @Mock
  private Node nodeObjectRootMock;

  public TestMockClasses() {
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

  @Test
  public void testExtendChain() {
    expect(thing.getChainRoot()).andReturn(nodeObjectRootMock).anyTimes();
    nodeObjectMock.addNode(nodeObjectRootMock);
    nodeObjectRootMock.setParent(nodeObjectMock);
    thing.setChainRoot(nodeObjectMock);
    replay();
    thing.extendChain(nodeObjectMock);
  }
}
