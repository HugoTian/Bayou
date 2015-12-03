package com.cs380d.message;

import com.cs380d.utility.VersionVector;


/**
 * second of the three message for anti-entropy process
 * Reply with current versionVector and highest committed sequence number
 * @author zhangtian
 */
public class AntiEntroyReplyMessage extends Message {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public VersionVector vv;
    public int CNS;
    
  // constructor
  public AntiEntroyReplyMessage(int s, int d, VersionVector vector, int cns) {
    super(s, d);
    vv = vector;
    CNS = cns;
  }

  // whether the version vector has the key
  public boolean hasKey(String rid) {
    return vv.hasKey(rid);
  }

  // get the the value of a version vector entry
  public int getTime(String rid) {
    return vv.get(rid);
  }

  public String toString() {
    return super.toString() + "Anti_entropy_reply(" + CNS + ")";
  }
}
