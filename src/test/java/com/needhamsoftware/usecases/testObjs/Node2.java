package com.needhamsoftware.usecases.testObjs;

/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 6/14/13
 * Time: 12:16 PM
 */

import com.google.common.collect.ArrayListMultimap;

public class Node2 {
  private ArrayListMultimap map;

  public void setParent(Node2 node) {
  }

  public void addNode(Node2 child) {
    child.setParent(this);
    child.getMap().put("foo", "bar");
  }

  public ArrayListMultimap getMap() {
    return map;
  }
}
