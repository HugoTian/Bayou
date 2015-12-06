package com.cs380d.message;

import com.cs380d.utility.Writes;


/**
 * third of the three message for anti-entropy process
 * Ack with the writes the receiver don't know about
 * @author zhangtian
 */
public class AntiEntropyAckMessage extends Message {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public Writes write;
  /*should R commit the known write or not */
  public boolean commit;

  //constructor
  // the message that no need to commit
  public AntiEntropyAckMessage(int s, int d, Writes w) {
    super (s, d);
    write = w;
    commit = false;
  }
  //constructor
  // the message that need commit
  public AntiEntropyAckMessage(int s, int d, Writes w, boolean c) {
    super (s, d);
    write = w;
    commit = c;
  }

  // convert message to string
  public String toString() {
    return super.toString() + "Anti_entropy_ack: "
            + write.toString() + " C?" + commit;
  }

}
