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
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Config {
  /**
   * Array of addresses of other hosts.  All hosts should have identical info here.
   */
  public ArrayList<InetAddress> addresses;


  /**
   * Array of listening port of other hosts.  All hosts should have identical info here.
   */
  public ArrayList<Integer> ports;

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
  public Config(int index, int numServers) {

    logger = Logger.getLogger("NetFramework");
    FileHandler fh;
    try {
      // This block configure the logger with handler and formatter
      fh = new FileHandler("netLog.txt");
      logger.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);

      // the following statement is used to log any messages
      logger.setUseParentHandlers(false);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }


    numProcesses = numServers;
    procNum = index;
    addresses = new ArrayList<InetAddress>(numProcesses);
    ports = new ArrayList<Integer>(numProcesses);

    for (int i = 0; i < numProcesses; i++) {
      ports.set(i,(PORTBASE + i));
      try {
        addresses.set(i,InetAddress.getByName(ADDR));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
      //System.out.printf("%d: %d @ %s\n", i, ports[i], addresses[i]);
    }
  }

  /**
   * Default constructor for those who want to populate config file manually
   */
  public Config() {
  }

  public void addNode() {

	ports.add(numProcesses,(PORTBASE + numProcesses));
	try {
	  addresses.set(numProcesses,InetAddress.getByName(ADDR));
	} catch (UnknownHostException e) {
	  e.printStackTrace();
	}
	numProcesses++;
	//for (int i = 0; i < numProcesses; i++)
	//  System.out.printf("%d: %d @ %s\n", i, ports.get(i), addresses.get(i));  
  }  
}
