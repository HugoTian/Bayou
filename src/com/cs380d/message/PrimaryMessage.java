package com.cs380d.message;

/**
 * Class for primary message 
 * Used when primary retire, and should have a new primary
 * @author Tian ZHang
 */
public class PrimaryMessage extends Message {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public int curCSN;
  public PrimaryMessage(int s, int d, int CSN) {
    super(s, d);
    curCSN = CSN;
  }
  // convert message to string
  public String toString() {
    return super.toString() + "HandOff(" + curCSN + ")";
  }
}
