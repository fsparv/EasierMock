package com.needhamsoftware.easiermock.testObjs;

public interface INodeUser {
  INode getChainRoot();

  void setChainRoot(INode newRoot);

  void extendChain(INode newRoot);
}
