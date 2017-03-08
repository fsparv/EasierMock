package com.needhamsoftware.usecases;

import com.copyright.easiertest.Mock;
import com.copyright.easiertest.ObjectUnderTest;
import com.needhamsoftware.usecases.testObjs.Node;
import com.needhamsoftware.usecases.testObjs.NodeUsingThing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.copyright.easiertest.EasierMocks.*;
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
