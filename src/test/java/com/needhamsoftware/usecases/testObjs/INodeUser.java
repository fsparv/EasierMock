package com.needhamsoftware.usecases.testObjs;

public interface INodeUser {
  INode getChainRoot();

  void setChainRoot(INode newRoot);

  void extendChain(INode newRoot);
}
