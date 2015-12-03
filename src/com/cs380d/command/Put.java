package com.cs380d.command;

/**
 * put client command
 * @author zhangtian
 */
public class Put extends ClientCommand {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  //  Constructor
  public Put(String s, String u) {
    super(s, u);
  }

  // convert command to string
  public String toString () {
    return "Put(" + song + "@" + url + ")";
  }
}
