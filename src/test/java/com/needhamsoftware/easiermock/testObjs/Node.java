package com.needhamsoftware.easiermock.testObjs;

/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 6/14/13
 * Time: 12:16 PM
 */

public class Node implements INode {

  @Override
  public void setParent(INode node) {
  }

  @Override
  public void addNode(INode child) {
    child.setParent(this);
  }

}
