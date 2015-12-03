package com.cs380d.utility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Version Vector, like  Vector Clock
 * @author zhangtian
 */
public class VersionVector implements Serializable {

  
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  Map<String, Integer> vector = new HashMap<String, Integer>();

  // whether it has key
  public boolean hasKey(String rid) {
    return vector.containsKey(rid);
  }

  // get the key value
  public int get(String rid) {
	// Debug check 
	// whether the vector clock has the key
    assert vector.containsKey(rid);
    return vector.get(rid);
  }

  // add the value to vector clock
  public void put(String rid, int time) {
    vector.put(rid, time);
  }

  // remove the value in the vector clock
  public void remove(String rid) {
	// Debug check 
    // whether the vector clock has the key
    assert vector.containsKey(rid);
    vector.remove(rid);
  }

  // convert vector clock to string
  public String toString() {
	//build the string 
    StringBuilder sb = new StringBuilder();
    Map<String, Integer> map = new HashMap<String, Integer>(vector);
    sb.append("\n================Version Vector ======================\n");
    for (String key : map.keySet()) {
      sb.append(key);
      sb.append(": ");
      sb.append(map.get(key));
      sb.append("\n");
    }
    sb.append("==================    End     =========================\n");
    return sb.toString();
  }
}
