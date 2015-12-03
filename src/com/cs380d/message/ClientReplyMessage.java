package com.cs380d.message;

import com.cs380d.command.ClientCommand;
import com.cs380d.command.Delete;
import com.cs380d.command.Get;
import com.cs380d.command.Put;
import com.cs380d.utility.Writes;


/**
 * Reply for client message from server
 * @author zhangtian
 */


public class ClientReplyMessage extends Message {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// whether the command is successful
public boolean suc;
  public ClientCommand cmd;
  public Writes write;
  public String url;

  /**
   * failure write/read
   */
  public ClientReplyMessage(ClientMessage msg) {
    super(msg.dst, msg.src);
    suc = false;
    cmd = msg.cmd;
    write = null;
    url = "";
  }

  /**
   * success write
   */
  public ClientReplyMessage(ClientMessage msg, Writes w) {
    this(msg);
    suc = true;
    write = w;
  }

  /**
   * success read
   */
  public ClientReplyMessage(ClientMessage msg, String u, Writes w) {
    this(msg);
    suc = true;
    url = u;
    write = w;
  }

  public String toString() {
    String rst = super.toString();
    if (cmd instanceof Put) {
      rst += "PutReply(" + suc + ", " + write + ")";
    } else if (cmd instanceof Delete) {
      rst += "DelReply(" + suc + ", " + write + ")";
    } else if (cmd instanceof Get) {
      rst+= "GetReply(" + suc + ", " + url + ", " + write + ")";
    } else {
      rst += "";
    }
    return rst;
  }
}
