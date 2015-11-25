package com.cs380d.command;


/*
 *  Client command Class
 *  @author Tian Zhang
 */
public class ClientCommand  extends Command{

	/**
	 *  Serial Version ID
	 */
	private static final long serialVersionUID = 1L;
    
    public String song;
    public String url;

    // Constructor
	public ClientCommand (String s) {
	    song = s;
	    url = "";
	}

	// Constructor
	public ClientCommand(String s, String u) {
	    song = s;
	    url = u;
	}
}
