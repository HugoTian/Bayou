package com.cs380d.command;

/**
 * Get Client Command
 * @author zhangtian
 */

public class Get extends ClientCommand {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor
  public Get(String s) {
    super(s);
  }

  // Convert command to string
  public String toString() {
    return "Get(" + song + ")";
  }
}
