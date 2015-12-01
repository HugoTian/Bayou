/**
 * This code may be modified and used for non-commercial 
 * purposes as long as attribution is maintained.
 * 
 * @author: Isaac Levy
 */

/**
* The sendMsg method has been modified by Navid Yaghmazadeh to fix a bug regarding to send a message to a reconnected socket.
*/

package com.cs380d.framework;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

/**
 * Public interface for managing network connections.
 * You should only need to use this and the Config class.
 * @author ilevy
 *
 */
public class NetController {
	private final Config config;
	private final List<IncomingSock> inSockets;
	private final HashMap<Integer,OutgoingSock> outSockets;
	private final HashMap<Integer, Boolean> upConnections;
	private final ListenServer listener;
	
	public NetController(Config config) {
		this.config = config;
		inSockets = Collections.synchronizedList(new ArrayList<IncomingSock>());
		listener = new ListenServer(config, inSockets);
		outSockets = new HashMap<Integer,OutgoingSock>();
		upConnections = new HashMap<Integer,Boolean>();
		listener.start();
	}
	
	/**
	 * Adding a node will
	 *   add to the ports, addresses, and numProcesses in the config file
	 *   update the listener insockets (may not be necessary)
	 *   grow the outSockets to account for the new Node
	 *   
	 *   BGB
	 */
	public void add(int id) {
		config.addNode(id);
		upConnections.put(id, true);
		try {
			initOutgoingConn(id);
		} catch (IOException e) {
			config.logger.info(String.format("Server %d: Cannot connect socket to %d. Cannot add.",
                    config.procNum, id));
		}
		
	}
	
	public void breakConnection(int id) {
		if (!upConnections.containsKey(id)) {
			config.logger.info(String.format("Server %d: Invalid connection state to %d. Cannot break.",
                    config.procNum, id));
		} else {
			upConnections.put(id, false);
		}
	}

	public void restoreConnection(int id) {
		if (!upConnections.containsKey(id)) {
			config.logger.info(String.format("Server %d: Invalid connection state to %d. Cannot restore.",
                    config.procNum, id));
		} else {
			upConnections.put(id, true);
		}
	}
	
	// Establish outgoing connection to a process
	private synchronized void initOutgoingConn(int proc) throws IOException {
		
		if (outSockets.get(proc) == null) {
			//removed exception because inits happen on dynamic add now ---BGB
			//	throw new IllegalStateException("proc " + proc + " not null");
			outSockets.put(proc, new OutgoingSock(new Socket(config.addresses.get(proc), config.ports.get(proc))));
			config.logger.info(String.format("Server %d: Socket to %d established", 
					config.procNum, proc));
		}
	}
	
	/**
	 * Send a msg to another process.  This will establish a socket if one is not created yet.
	 * Will fail if recipient has not set up their own NetController (and its associated serverSocket)
	 * @param process int specified in the config file - 0 based
	 * @param msg Do not use the "&" character.  This is hardcoded as a message separator. 
	 *            Sends as ASCII.  Include the sending server ID in the message
	 * @return bool indicating success
	 */
	public synchronized boolean sendMsg(int process, String msg) {
		
		if (!upConnections.containsKey(process)) {
			config.logger.info(String.format("Server %d: Invalid connection state to %d.",
                    config.procNum, process));
			return false;
		} else if (!upConnections.get(process)) {
			config.logger.info(String.format("Server %d: Not sending message to %d: Broken Connection.",
                    config.procNum, process));
			return false;
		}
				
		try {
			if (outSockets.get(process) == null) 
				initOutgoingConn(process);
			outSockets.get(process).sendMsg(msg);
		} catch (IOException e) { 
			if (outSockets.containsKey(process)) {
				outSockets.get(process).cleanShutdown();
				outSockets.remove(process);
				try{
					initOutgoingConn(process);
                        		outSockets.get(process).sendMsg(msg);	
				} catch(IOException e1){
					if (outSockets.containsKey(process)) {
						outSockets.get(process).cleanShutdown();
	                	outSockets.remove(process);
					}
					config.logger.info(String.format("Server %d: Msg to %d failed.",
                        config.procNum, process));
        		    config.logger.log(Level.FINE, String.format("Server %d: Socket to %d error",
                        config.procNum, process), e);
                    return false;
				}
				return true;
			}
			config.logger.info(String.format("Server %d: Msg to %d failed.", 
				config.procNum, process));
			config.logger.log(Level.FINE, String.format("Server %d: Socket to %d error", 
				config.procNum, process), e);
			return false;
		}
		return true;
	}
	
	/**
	 * Return a list of msgs received on established incoming sockets
	 * @return list of messages sorted by socket, in FIFO order. *not sorted by time received*
	 */
	public synchronized List<String> getReceivedMsgs() {
		List<String> objs = new ArrayList<String>();
		synchronized(inSockets) {
			ListIterator<IncomingSock> iter  = inSockets.listIterator();
			while (iter.hasNext()) {
				IncomingSock curSock = iter.next();
				try {
					objs.addAll(curSock.getMsgs());
				} catch (Exception e) {
					config.logger.log(Level.INFO, 
							"Server " + config.procNum + " received bad data on a socket", e);
					curSock.cleanShutdown();
					iter.remove();
				}
			}
		}
		
		return objs;
	}

	/**
	 * Shuts down threads and sockets.
	 */
	public synchronized void shutdown() {
		listener.cleanShutdown();
        if(inSockets != null) {
		    for (IncomingSock sock : inSockets)
			    if(sock != null)
                    sock.cleanShutdown();
        }
		if(outSockets != null) {
            for (OutgoingSock sock : outSockets.values())
			    if(sock != null)
                    sock.cleanShutdown();
        }
		
	}
	
	/**
	 * Remove server from outsocket list. 
	 */
	public void removeServer (int s) {
		outSockets.remove(s);
	}

}
