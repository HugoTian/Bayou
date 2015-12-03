package com.cs380d.utility;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * SessionManager as specified in
 * "Session Guarantees for Weakly Consistent Replicated Data" Paper
 * @author zhangtian 
 */
public class SessionManager implements Serializable {
  

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  public Map<String, Integer> readVector = new HashMap<String, Integer> ();
  public Map<String, Integer> writeVector = new HashMap<String, Integer> ();

  /**
   * Update Write Vector based on reply for Write
   */
  public void updateWrite(Writes w) {
    writeVector.put(w.replicaId.toString(), w.acceptTime);
  }


  /**
   * Update Read Vector (the Writes relevant to latest Read)
   * based on reply
   */
  public void updateRead(Writes w) {
    readVector.put(w.replicaId.toString(), w.acceptTime);
  }

  /**
   * Check ReadVector and WriteVector against Version Vector on server
   */
  public boolean isDominatedBy(VersionVector vv) {
    return dominates(vv.vector, readVector) && dominates(vv.vector, writeVector);
  }

  /**
   * Whether vector a dominates vector b
   */
  public boolean dominates(Map<String, Integer> supV,Map<String, Integer> subV ) {
    for (String rid : subV.keySet()) {
      if (!supV.containsKey(rid)) {
        if (subV.get(rid) != 0) {
          return false;
        }
      } else {
        if (supV.get(rid) < subV.get(rid)) {
          return false;
        }
      }
    }
    return true;
  }

}
