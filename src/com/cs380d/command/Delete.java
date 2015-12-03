package com.cs380d.command;

/**
 * Delete Command for client Command
 * @author zhangtian
 */

public class Delete extends ClientCommand {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Constructor
  public Delete(String s) {
    super(s);
  }

  // Convert command to string
  public String toString() {
    return "Del(" + song + ")";
  }
}