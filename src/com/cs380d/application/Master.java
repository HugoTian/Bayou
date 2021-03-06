package com.cs380d.application;

import java.util.HashMap;
import java.util.Scanner;

/**
 * CS 380D - Distibuted Computing I
 * Project 3 _ Bayou
 * Master class
 * @author zhangtian
 * @author Bradley Beth
 *
 */
public class Master {

	public static HashMap<Integer, Server> servers = new HashMap<Integer, Server>();
	public static HashMap<Integer, Client> clients = new HashMap<Integer, Client>();
	
	
	public static void main(String [] args) {
		Scanner scan = new Scanner(System.in);
		while (scan.hasNextLine()) {
			String [] inputLine = scan.nextLine().split(" ");
			int clientId, serverId, id1, id2;
			String songName, URL;
			switch (inputLine[0]) {
			case "joinServer":
				serverId = Integer.parseInt(inputLine[1]);
				/*
				 * Start up a new server with this id and connect it to all servers
				 */
				servers.put(serverId, new Server(serverId,servers.keySet()));
				for (Server s : servers.values())  
					s.nc.add(serverId);				
				break;
			case "retireServer":
				serverId = Integer.parseInt(inputLine[1]);
				/*
				 * Retire the server with the id specified. This should block until
				 * the server can tell another server of its retirement
				 */ 
				
				// TODO: BLOCK and TELL
				// This may require removing/altering the removeServer 
				// loop below. Until then, the Master removes the server
				// from all servers' lists. ---BGB
				
				servers.get(serverId).shutdown();
				servers.put(serverId, null);
				servers.remove(serverId);
				
				// Tell all servers I am gone.
				for (Server s : servers.values())
					s.nc.removeServer(serverId);
				break;
			case "joinClient":
				clientId = Integer.parseInt(inputLine[1]);
				serverId = Integer.parseInt(inputLine[2]);
				/*
				 * Start a new client with the id specified and connect it to 
				 * the server
				 */
				if (clients.containsKey(clientId)) {
					clients.get(clientId).shutdown();
					clients.put(clientId, null);
					clients.remove(clientId);
				}			
				clients.put(clientId, new Client(clientId,serverId));
				servers.get(serverId).nc.add(clientId);		
				break;
			case "breakConnection":
				id1 = Integer.parseInt(inputLine[1]);
				id2 = Integer.parseInt(inputLine[2]);
				/*
				 * Break the connection between a client and a server or between
				 * two servers
				 */
				if (servers.containsKey(id1))
					servers.get(id1).nc.breakConnection(id2);
				else if(clients.containsKey(id1))
					clients.get(id1).nc.breakConnection(id2);
				if (servers.containsKey(id2))
					servers.get(id2).nc.breakConnection(id1);
				else if(clients.containsKey(id2))
					clients.get(id2).nc.breakConnection(id1);
					
				break;
			case "restoreConnection":
				id1 = Integer.parseInt(inputLine[1]);
				id2 = Integer.parseInt(inputLine[2]);
				/*
				 * Restore the connection between a client and a server or between
				 * two servers
				 */
				if (servers.containsKey(id1))
					servers.get(id1).nc.restoreConnection(id2);
				else if(clients.containsKey(id1))
					clients.get(id1).nc.restoreConnection(id2);
				if (servers.containsKey(id2))
					servers.get(id2).nc.restoreConnection(id1);
				else if(clients.containsKey(id2))
					clients.get(id2).nc.restoreConnection(id1);
				break;
			case "pause":
				/*
				 * Pause the system and don't allow any Anti-Entropy messages to
				 * propagate through the system
				 */
				Server.pauseAnti_Entropy();
				break;
			case "start":
				/*
				 * Resume the system and allow any Anti-Entropy messages to
				 * propagate through the system
				 */
				Server.startAnti_Entropy();
				break;
			case "stabilize":
				/*
				 * Block until there are enough Anti-Entropy messages for all values to 
				 * propagate through the currently connected servers. In general, the 
				 * time that this function blocks for should increase linearly with the 
				 * number of servers in the system.
				 */
				break;
			case "printLog":
				serverId = Integer.parseInt(inputLine[1]);
				/*
				 * Print out a server's operation log in the format specified in the
				 * handout.
				 */
				break;
			case "put":
				clientId = Integer.parseInt(inputLine[1]);
				songName = inputLine[2];
				URL = inputLine[3];
				/*
				 * Instruct the client specified to associate the given URL with the given
				 * songName. This command should block until the client communicates with
				 * one server.
				 */ 
				break;
			case "get":
				clientId = Integer.parseInt(inputLine[1]);
				songName = inputLine[2];
				/*
				 * Instruct the client specified to attempt to get the URL associated with
				 * the given songName. The value should then be printed to standard out of 
				 * the master script in the format specified in the handout. This command 
				 * should block until the client communicates with one server.
				 */ 
				break;
			case "delete":
				clientId = Integer.parseInt(inputLine[1]);
				songName = inputLine[2];
				/*
				 * Instruct the client to delete the given songName from the playlist. 
				 * This command should block until the client communicates with one server.
				 */ 
				break;
				
		
		/**
		 * These commands are for testing the dynamic socket framework.
		 * 
		 * -->BGB
		 */
			case "sendMsg":				
				/* EX: sendMsg 1 2 Hi_there */
				int fromId = Integer.parseInt(inputLine[1]);
				int toId = Integer.parseInt(inputLine[2]);
				String msg = inputLine[3];
				if (servers.containsKey(fromId))
					servers.get(fromId).nc.sendMsg(toId, msg);
				else if (clients.containsKey(fromId))
					clients.get(fromId).nc.sendMsg(toId, msg);
				break;
				
			case "dumpMsgs":				
				/* EX: dumpMsgs 2 */
				int dumpId = Integer.parseInt(inputLine[1]);
				if (servers.containsKey(dumpId))				
					System.out.println(servers.get(dumpId).nc.getReceivedMsgs());				
				else if (clients.containsKey(dumpId))
					System.out.println(clients.get(dumpId).nc.getReceivedMsgs());
				break;

			case "dumpAll":				
				/* EX: dumpAll */
				System.out.println("\nSERVERS\n=======");
				for (Server s : servers.values())
					System.out.println(s.pid +":\t"+s.nc.getReceivedMsgs());
				System.out.println("\nCLIENTS\n=======");
				for (Client c : clients.values())
					System.out.println(c.pid +":\t"+c.nc.getReceivedMsgs());
				break;
		/**
		 * <--BGB
		 */
				
			}
		}
		scan.close();
	}
}
