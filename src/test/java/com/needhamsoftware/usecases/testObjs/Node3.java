package com.needhamsoftware.usecases.testObjs;

/*
 * Created with IntelliJ IDEA.
 * User: gus
 * Date: 6/14/13
 * Time: 12:16 PM
 */

import com.google.common.collect.ArrayListMultimap;

import java.io.IOException;

public class Node3 {
  private ArrayListMultimap map;
  private int id;

  public void setParent(Node3 node) {
  }

  public void addNode(Node3 child) throws IOException {
    child.setParent(this);
    invokeMessaging("add",child.getId());
  }

  public ArrayListMultimap getMap() {
    return map;
  }

  public int getId() {
    return id;
  }

  public boolean invokeMessaging(String nodeAdded, int i) throws IOException{
    return true;
  }
}
