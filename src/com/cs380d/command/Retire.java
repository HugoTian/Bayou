package com.cs380d.command;

import com.cs380d.utility.ReplicaID;


/**
 *  replica retires by sending retirement write to itself
 *  must remain alive until at least one anti-entropy step.
 *  @author zhangtian
 */
public class Retire extends ServerCommand {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  
  // Constructor
  public Retire(ReplicaID rid, int acptTime) {
    super(rid, acptTime);
  }

  // Command name
  public String name () {
    return "Retire";
  }
}
