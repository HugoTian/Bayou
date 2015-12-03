package com.cs380d.command;

import com.cs380d.utility.ReplicaID;



/**
 * Create a server command
 * @author zhangtian
 */

public class Create extends ServerCommand {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	// Constructor
	public Create(ReplicaID rid, int acptTime) {
		super(rid, acptTime);
	}

	public String name() {
		return "Create";
	}
}