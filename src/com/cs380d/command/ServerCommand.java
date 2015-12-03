package com.cs380d.command;

import com.cs380d.utility.ReplicaID;



/**
 * Server command
 * @author zhangtian
 */
public class ServerCommand extends Command {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   public ReplicaID rid;
  public int acceptTime;

  public ServerCommand (ReplicaID rid, int acptTime) {
    this.rid = rid;
    acceptTime = acptTime;
  }

  // convert tp string
  public String toString() {
    return  name() + "(" + rid + ")";
  }

  // return the command's name to distinguish the command type
  public String name () {
    return "cmd";
  }
}
