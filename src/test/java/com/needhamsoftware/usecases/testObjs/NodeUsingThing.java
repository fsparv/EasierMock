package com.needhamsoftware.usecases.testObjs;

/**
 * A class that does some random (not necessarily sensible) stuff with INode's
 * This class is used to reproduce issue #3
 */
public class NodeUsingThing implements INodeUser {

  private INode chainRoot;

  public NodeUsingThing(INode chainRoot) {
    this.chainRoot = chainRoot;
  }

  @Override
  public INode getChainRoot() {
    return chainRoot;
  }

  @Override
  public void setChainRoot(INode newRoot) {

  }

  @Override
  public void extendChain(INode newRoot) {
    getChainRoot().setParent(newRoot);
    newRoot.addNode(getChainRoot());
    setChainRoot(newRoot);
  }

}
