package com.needhamsoftware.usecases;

import com.copyright.easiertest.Mock;
import com.copyright.easiertest.ObjectUnderTest;
import com.needhamsoftware.usecases.testObjs.Node;
import com.needhamsoftware.usecases.testObjs.Node3;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.copyright.easiertest.EasierMocks.prepareMocks;
import static com.copyright.easiertest.EasierMocks.replay;
import static com.copyright.easiertest.EasierMocks.reset;
import static com.copyright.easiertest.EasierMocks.verify;
import static org.easymock.EasyMock.expect;

/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 7/9/14
 */
public class TestNgTest {

  private static final String NODE_ADDED = "add";
  @ObjectUnderTest private Node3 cutnode;
  private Node node = new Node();
  @Mock private Node child;
  @Mock private Node3 child3;

  public TestNgTest() {
    prepareMocks(this);
  }

  @Test
  public void testAddNode() {
    reset();
    child.setParent(node);
    replay();
    node.addNode(child);
    verify();
  }


  @Test(expectedExceptions = IOException.class)
  public void testAddNode3() throws IOException {
    reset();
    child3.setParent(cutnode);
    expect(child3.getId()).andReturn(42);
    expect(cutnode.invokeMessaging(NODE_ADDED, 42)).andThrow(new IOException());
    replay();
    cutnode.addNode(child3);
    verify();
  }

}
