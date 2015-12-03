package com.cs380d.message;

import com.cs380d.command.ClientCommand;
import com.cs380d.command.Delete;
import com.cs380d.command.Get;
import com.cs380d.command.Put;
import com.cs380d.utility.SessionManager;

/**
 * Client Request Message
 * @author zhangtian
 */
public class ClientMessage extends Message {
  /**
	 * 
	 */
 private static final long serialVersionUID = 1L;
  public ClientCommand cmd;
  public SessionManager sm;

  public ClientMessage(int src, int dst, ClientCommand cmd, SessionManager sm) {
    super(src, dst);
    this.cmd = cmd;
    this.sm = sm;
  }

  // convert message to string
  public String toString () {
    return super.toString() + "ClientRequest(" + cmd + ")";
  }

  // whether the message is a write messagge
  public boolean isWrite() {
    return cmd instanceof Put || cmd instanceof Delete;
  }
  
  // whether it is a read
  public boolean isRead() {
    return cmd instanceof Get;
  }
}
