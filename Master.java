import java.util.Scanner;

public class Master {

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
            break;
        case "retireServer":
            serverId = Integer.parseInt(inputLine[1]);
	    /*
	     * Retire the server with the id specified. This should block until
	     * the server can tell another server of its retirement
             */ 
            break;
        case "joinClient":
            clientId = Integer.parseInt(inputLine[1]);
            serverId = Integer.parseInt(inputLine[2]);
            /*
             * Start a new client with the id specified and connect it to 
	     * the server
             */
            break;
        case "breakConnection":
	    id1 = Integer.parseInt(inputLine[1]);
	    id2 = Integer.parseInt(inputLine[2]);
            /*
             * Break the connection between a client and a server or between
	     * two servers
             */
            break;
	case "restoreConnection":
	    id1 = Integer.parseInt(inputLine[1]);
	    id2 = Integer.parseInt(inputLine[2]);
            /*
             * Restore the connection between a client and a server or between
	     * two servers
             */
            break;
        case "pause":
            /*
             * Pause the system and don't allow any Anti-Entropy messages to
	     * propagate through the system
             */
            break;
        case "start":
            /*
             * Resume the system and allow any Anti-Entropy messages to
	     * propagate through the system
	     */
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
      }
    }
  }
}
