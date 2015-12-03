package com.cs380d.message;

/**
 * First of the three message for anti-entropy process
 * Ask for receiver's information
 * @author zhangtian 
 */
public class AntiEntroyRequestMessage extends Message{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

  public AntiEntroyRequestMessage (int s, int d) {
    super(s, d);
  }

  public String toString() {
    return super.toString() + "Anti_Entropy_Request()";
  }
}

