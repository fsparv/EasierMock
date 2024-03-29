package com.needhamsoftware.easiermock;/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 6/14/13
 * Time: 12:13 PM
 */

import com.needhamsoftware.easiermock.testObjs.Node;
import org.junit.Test;

import static com.needhamsoftware.easiermock.EasierMocks.prepareMocks;
import static com.needhamsoftware.easiermock.EasierMocks.replay;
import static com.needhamsoftware.easiermock.EasierMocks.reset;
import static com.needhamsoftware.easiermock.EasierMocks.verify;

public class SimpleTest {

  private final Node node = new Node();
  @Mock
  private Node child;

  public SimpleTest() {
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

}
