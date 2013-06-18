package com.needhamsoftware.usecases;/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 6/14/13
 * Time: 12:13 PM
 */

import com.copyright.easiertest.Mock;
import com.needhamsoftware.usecases.testObjs.Node;
import org.junit.Test;

import static com.copyright.easiertest.EasierMocks.*;

public class SimpleTest {

  private Node node = new Node();
  @Mock private Node child;

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
