package com.needhamsoftware.easiermock;

import com.needhamsoftware.easiermock.testObjs.INode;
import com.needhamsoftware.easiermock.testObjs.NodeUsingThing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.needhamsoftware.easiermock.EasierMocks.*;
import static org.easymock.EasyMock.expect;

/**
 * Class to make sure we don't break mocking of interfaces
 */
@SuppressWarnings("Duplicates")
public class TestMockInterfaces {

  // to demonstrate issue #3 uncomment this and comment out the Object based one.
  //  @ObjectUnderTest
  //  private INodeUser thing;
  @ObjectUnderTest
  private NodeUsingThing thing;
  @Mock
  private INode nodeObjectMock;
  @Mock
  private INode nodeObjectRootMock;

  public TestMockInterfaces() {
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
