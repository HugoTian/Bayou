package com.cs380d.command;

/**
 * Client Command for Bayou
 * @author zhangtian
 */
public class ClientCommand extends Command {
    /**
	 *  Default ID 
	 */
  private static final long serialVersionUID = 1L;
  public String song;
  public String url;

  // constructor with only song
  public ClientCommand (String s) {
    song = s;
    url = "";
  }

  // constructor with song and url
  public ClientCommand(String s, String u) {
    song = s;
    url = u;
  }
}
