package com.needhamsoftware.usecases.testObjs;

public interface INode {
  void setParent(INode node);

  void addNode(INode child);
}
