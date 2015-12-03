package com.cs380d.utility;

import java.io.Serializable;

/**
 * Class for replica ID
 * @author zhangtian 
 */
public class ReplicaID implements Serializable {
  

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  //accept time
  public int acceptTime;
  // the parent who receive the create command of this replica
  public ReplicaID parent;
  // the process id
  public int pid;

  /**
   * root id
   * @return
   */
  public ReplicaID() {
    acceptTime = -1;
    parent = null;
    pid = -1;
  }

  /**
   * The very first replica
   */
  public ReplicaID (int id) {
    acceptTime = 0;
    parent = new ReplicaID();
    pid = id;
  }

  // copy constructor
  public ReplicaID(int acTime, ReplicaID rid, int id) {
    acceptTime = acTime;
    parent = rid;
    pid = id;
  }

  //convert replica ID to string
  public String toString () {
    if (parent == null) {
      return "rid(root)";
    } else {
      return "rid(" + acceptTime + ", " + parent + ")";
    }
  }
}
