package com.cs380d.utility;

import java.io.Serializable;

import com.cs380d.command.Command;

/**
 * Write: log entry
 * @author zhangtian
 */
public class Writes implements Comparable<Writes>, Serializable {
  

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  // committed sequence number
  public int csn;
  // accept time
  public int acceptTime;
  // the replica id
  public ReplicaID replicaId;
  // its command
  public Command command;

  //Default constructor
  public Writes(int commitSequenceNumber, int time, ReplicaID rid, Command cmd) {
    csn = commitSequenceNumber;
    acceptTime = time;
    replicaId = rid;
    command = cmd;
  }

  // Compare between 2 writes
  @Override
  public int compareTo(Writes other) {
    if (this.csn != other.csn) {
      return this.csn - other.csn;
    } else if (this.acceptTime != other.acceptTime) {
      return this.acceptTime - other.acceptTime;
    } else {
      // todo
      return 0;
    }
  }

  // To see whether the two write are same
  public boolean sameAs(Writes other) {
    return replicaId.toString().equals(other.replicaId.toString()) &&
        acceptTime == other.acceptTime;
  }

  // Convert write to string
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("W(");
    sb.append(csn);
    sb.append(", ");
    sb.append(acceptTime);
    sb.append(", ");
    sb.append(replicaId.toString());
    sb.append(", ");
    sb.append(command.toString());
    sb.append(") ");
    return sb.toString();
  }
}
