package com.cs380d.command;

import com.cs380d.value.Constant;;;
/* Client command follow the notes
*  @author Tian ZHang
*/
public class Put extends ClientCommand{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Defalut constructor 
	public Put(String s, String u) {
	    super(s, u);
	}

	// Convert command to string
	public String toString () {
	    return "Put(" + song + Constant.SONGURLDELIMITER + url + ")";
	}

}
