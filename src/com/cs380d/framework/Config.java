/**
 * This code may be modified and used for non-commercial 
 * purposes as long as attribution is maintained.
 *
 * @author: Isaac Levy
 */

package com.cs380d.framework;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Config {
  /**
   * Map of addresses of other hosts.  All hosts should have identical info here.
   */
  public HashMap<Integer,InetAddress> addresses;


  /**
   * Map of listening port of other hosts.  All hosts should have identical info here.
   */
  public HashMap<Integer, Integer> ports;

  /**
   * Total number of hosts
   */
  public int numProcesses;

  /**
   * This hosts number (should correspond to array above).  Each host should have a different number.
   */
  public int procNum;

  /**
   * Logger.  Mainly used for console printing, though be diverted to a file.
   * Verbosity can be restricted by raising level to WARN
   */
  public Logger logger;

  public static final int PORTBASE = 10000;
  public static final String ADDR = "127.0.0.1";


  /**
   * @param numServers, index
   * @throws IOException
   */
  public Config(int id, Set<Integer> servers) {

    logger = Logger.getLogger("NetFramework");
    FileHandler fh;
    try {
      // This block configure the logger with handler and formatter
      fh = new FileHandler("netLog.txt");
      logger.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);

      // the following statement is used to log any messages
      logger.setUseParentHandlers(true);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


    numProcesses = servers.size();
    procNum = id;
    addresses = new HashMap<Integer, InetAddress>();
    ports = new HashMap<Integer, Integer>();

    for (int i : servers) {
      ports.put(i,(PORTBASE + i));
      try {
        addresses.put(i,InetAddress.getByName(ADDR));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
  	//for (int idx = 0; idx < numProcesses; idx++)
  	//  System.out.printf("%d: %d @ %s\n", idx, ports.get(idx), addresses.get(idx)); 
    }
  }

  /**
   * Default constructor for those who want to populate config file manually
   */
  public Config() {
  }

  public void addNode(int id) {

	ports.put(id,(PORTBASE + id));
	try {
	  addresses.put(id,InetAddress.getByName(ADDR));
	} catch (UnknownHostException e) {
	  e.printStackTrace();
	}
	numProcesses++;
	//for (int i = 0; i < numProcesses; i++)
	//  System.out.printf("%d: %d @ %s\n", i, ports.get(i), addresses.get(i));  
  }  
}
