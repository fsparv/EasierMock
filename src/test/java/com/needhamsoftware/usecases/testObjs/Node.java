package com.needhamsoftware.usecases.testObjs;

/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 6/14/13
 * Time: 12:16 PM
 */

public class Node {

  public void setParent(Node node) {
  }

  public void addNode(Node child) {
    child.setParent(this);
  }

}
