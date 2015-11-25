package com.cs380d.command;


/*  
 *  Get Client Command follow Notes
 *  @author Tian Zhang
 */
public class Get extends ClientCommand{
	

    /**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	
	// Defalut constructor
	public Get(String s) {
	    super(s);
	}

	// Convert command to string
	public String toString() {
	    return "Get(" + song + ")";
	}

}
