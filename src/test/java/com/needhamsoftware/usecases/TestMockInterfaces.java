package com.needhamsoftware.usecases;

import com.copyright.easiertest.Mock;
import com.copyright.easiertest.ObjectUnderTest;
import com.needhamsoftware.usecases.testObjs.INode;
import com.needhamsoftware.usecases.testObjs.INodeUser;
import com.needhamsoftware.usecases.testObjs.Node;
import com.needhamsoftware.usecases.testObjs.NodeUsingThing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.copyright.easiertest.EasierMocks.*;
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
