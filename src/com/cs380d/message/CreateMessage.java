package com.cs380d.message;

/**
 * MEssage for the creation of a server
 * @author zhangtian 
 */
public class CreateMessage extends Message {

  /*
   * 
   */
	private static final long serialVersionUID = 1L;

   // Default constructor
   public CreateMessage(int src, int dst) {
     super(src, dst);
   }

   // convert message to stringw
  public String toString () {
    return super.toString() + "Create()";
  }
}
