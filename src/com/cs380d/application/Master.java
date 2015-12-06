package com.cs380d.application;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import com.cs380d.utility.Constants;
/*
 *  Master class for Bayou project 
 *  @author Tian Zhang
 */

public class Master {

  public static void main(String [] args) {
	  // assume there is max number of node
	  // allow for static congif 
	  Node[] nodes = new Node[Constants.MAX_NODE];
	  // matain a list of current server
	  ArrayList<Integer> curServers = new ArrayList<Integer>();


	  Scanner scan = new Scanner(System.in);
	  while (scan.hasNextLine()) {
      String [] inputLine = scan.nextLine().split(" ");
      int clientId, serverId, id1, id2;
      String songName, URL;
      if (Constants.debug) {
        System.out.println("\n\nCommand: " + Arrays.toString(inputLine));
      }


      if (inputLine[0].equals("joinServer")) {
        if (Constants.debug) {
          System.out.print("Current Servers" + curServers);
        }
        serverId = Integer.parseInt(inputLine[1]);
        /**
         * Start up a new server with this id and connect it to all servers
         */
        if (curServers.isEmpty()) {
          nodes[serverId] = new Server(serverId);
        } else {
          nodes[serverId] = new Server(serverId, curServers.get(0));
        }
        // add server to current server
        curServers.add(serverId);
        
        for (int i: curServers) {
          // debug 
          // check whether all server is valid node
          assert nodes[serverId] != null;
          assert nodes[serverId] instanceof Server;
          // config this newly joined server
          Server s = (Server)nodes[i];
          s.updateConnected(curServers);
        }
        allclear(Constants.SLEEP);

      } else if (inputLine[0].equals("retireServer")) {
        serverId = Integer.parseInt(inputLine[1]);
	      /**
	       * Retire the server with the id specified. This should block until
	       * the server can tell another server of its retirement
         */
        // debug 
        // assert if it is not a valid server
        assert nodes[serverId] != null;
        assert nodes[serverId] instanceof Server;
        Server s = (Server)nodes[serverId];
        s.toRetire();
        //update current server list
        curServers.remove((Integer)serverId);

      } else if (inputLine[0].equals("joinClient")) {
        clientId = Integer.parseInt(inputLine[1]);
        serverId = Integer.parseInt(inputLine[2]);
         /**
          * Start a new client with the id specified and connect it to
	        * the server
          */
        if (nodes[clientId] == null)
        	nodes[clientId] = new Client(clientId, serverId);
        else
        	nodes[clientId] = ((Client)nodes[clientId]).switchServer(serverId);
      } else if (inputLine[0].equals("breakConnection")) {
        id1 = Integer.parseInt(inputLine[1]);
        id2 = Integer.parseInt(inputLine[2]);
         /**
          * Break the connection between a client and a server or between
	        * two servers
          */
        assert nodes[id1] != null;
        assert nodes[id2] != null;
        // specify the cases
        // Both of them are server
        if (nodes[id1] instanceof Server && nodes[id2] instanceof Server) {
          Server s1 = (Server)nodes[id1];
          s1.disconnectWith(id2);
          Server s2 = (Server)nodes[id2];
          s2.disconnectWith(id1);
        
        //  first one is client and second one is server
        } else if (nodes[id1] instanceof Client) {
          assert nodes[id2] instanceof Server;
          Client c = (Client)nodes[id1];
          c.disConnect();
       // first one is server , and second one is client
        } else if (nodes[id2] instanceof Client) {
          assert nodes[id1] instanceof Server;
          Client c = (Client)nodes[id2];
          c.disConnect();
          
        // disconnect 2 client will be illegal operation
        } else {
          System.out.println("Invalid Operation, disconnecting two clients");
        }
        
        
        
        
      } else if (inputLine[0].equals("restoreConnection")) {
        id1 = Integer.parseInt(inputLine[1]);
        id2 = Integer.parseInt(inputLine[2]);
         /**
          * Restore the connection between a client and a server or between
	        * two servers
          */
        assert nodes[id1] != null;
        assert nodes[id2] != null;
        //cases specify
        // connect 2 server
        if (nodes[id1] instanceof Server && nodes[id2] instanceof Server) {
          Server s1 = (Server)nodes[id1];
          s1.connectTo(id2);
          Server s2 = (Server)nodes[id2];
          s2.connectTo(id1);
        // connect a client to servers
        } else if (nodes[id1] instanceof Client) {
          assert nodes[id2] instanceof Server;
          Client c = (Client)nodes[id1];
          c.connectTo(id2);
        //connect a server to client
        } else if (nodes[id2] instanceof Client) {
          assert nodes[id1] instanceof Server;
          Client c = (Client)nodes[id2];
          c.connectTo(id1);
        //connect 2 client is invalid operation
        } else {
          System.out.println("Invalid Operation, connecting two clients");
        }

      } else if (inputLine[0].equals("pause")) {
         /**
          * Pause the system and don't allow any Anti-Entropy messages to
	        * propagate through the system
          */
          for (int id : curServers) {
            nodes[id].pause();
          }

      } else if (inputLine[0].equals("start")) {
         /**
          * Resume the system and allow any Anti-Entropy messages to
	        * propagate through the system
	        */
        for (int id : curServers) {
          nodes[id].unPause();
        }

      } else if (inputLine[0].equals("stabilize")) {
          /**
           *  Block until there are enough Anti-Entropy messages for all values to
           * propagate through the currently connected servers. In general, the
           * time that this function blocks for should increase linearly with the
	         * number of servers in the system.
	         */
    	// just wait for enough time
        allclear(curServers.size() * Constants.SLEEP);

      } else if (inputLine[0].equals("printLog")) {
        serverId = Integer.parseInt(inputLine[1]);
         /**
          * Print out a server's operation log in the format specified in the
	        * handout.
	        */
        // use assert to debug
        assert nodes[serverId] != null;
        assert nodes[serverId] instanceof Server;
        Server s = (Server)nodes[serverId];
        s.printLog();

      } else if (inputLine[0].equals("put")) {
        clientId = Integer.parseInt(inputLine[1]);
        songName = inputLine[2];
        URL = inputLine[3];
         /**
          * Instruct the client specified to associate the given URL with the given
	        * songName. This command should block until the client communicates
          * with one server.
          */
        assert nodes[clientId] != null;
        assert nodes[clientId] instanceof Client;
        Client c = (Client)nodes[clientId];
        c.put(songName, URL);
       // takeSnap(Constants.SLEEP);
      } else if (inputLine[0].equals("get")) {
        clientId = Integer.parseInt(inputLine[1]);
        songName = inputLine[2];
         /**
          * Instruct the client specified to attempt to get the URL associated with
	        * the given songName. The value should then be printed to standard
	        * out of the master script in the format specified in the handout.
	        * This command should block until the client communicates with one server.
	        */
        assert nodes[clientId] != null;
        assert nodes[clientId] instanceof Client;
        Client c = (Client)nodes[clientId];
        c.get(songName);
        allclear(Constants.SLEEP);
      } else if (inputLine[0].equals("delete")) {
        clientId = Integer.parseInt(inputLine[1]);
        songName = inputLine[2];
         /**
          * Instruct the client to delete the given songName from the playlist.
          * This command should block until the client communicates with one server.
          */
        assert nodes[clientId] != null;
        assert nodes[clientId] instanceof Client;
        Client c = (Client)nodes[clientId];
        c.delete(songName);
        //takeSnap(Constants.SLEEP);
      }
    }
    // all done
    allclear(500);
    System.exit(0);
  }

  public static void allclear(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
