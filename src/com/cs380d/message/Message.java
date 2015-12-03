package com.cs380d.message;

import java.io.Serializable;

/**
 * Super Message class
 */
public class Message implements Serializable {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  public int src;
  public int dst;

  public Message (int s, int d) {
    src = s;
    dst = d;
  }

  public String toString() {
    return src + " -> " + dst + ": ";
  }

  public boolean isAntiEntroymessage () {
    return true;
  }
  
}

