package com.cs380d.message;

import com.cs380d.utility.ReplicaID;

/**
 * Reply message for create of a process
 * @author Tian Zhang
 */
public class CreateReplyMessage extends Message {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public ReplicaID rid;

   // Constructor
  public CreateReplyMessage(int src, int dst, ReplicaID newId) {
    super(src, dst);
    rid = newId;
  }

  // convert message to string
  public String toString () {
    return super.toString() + "CreateReply(" + rid + ")";
  }
}
