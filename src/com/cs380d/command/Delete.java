package com.cs380d.command;


/*
 *  Delete Client Command follow command
 *  @author Tian ZHang
 */
public class Delete extends ClientCommand{
	 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Default Constructor
	public Delete(String s) {
		    super(s);
    }
    
	// Convert command to string
	public String toString() {
		   return "Del(" + song + ")";
    }
}
