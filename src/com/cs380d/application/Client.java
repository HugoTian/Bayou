package com.cs380d.application;

import com.cs380d.command.ClientCommand;
import com.cs380d.command.Delete;
import com.cs380d.command.Get;
import com.cs380d.command.Put;
import com.cs380d.message.ClientMessage;
import com.cs380d.message.ClientReplyMessage;
import com.cs380d.message.Message;
import com.cs380d.utility.SessionManager;


/**
 * Client code
 * send request to server, controlled by Master
 * Receive server's reply, update session manager
 * @author zhangtian
 */

public class Client extends Node {
  int serverId;
  SessionManager sm;

  public Client (int pid, int sid) {
    super(pid);
    serverId = sid;
    sm = new SessionManager();
    start();
  }

  public String name () {
    return "Client";
  }

  /**
   * Handle received message
   */
  public void run() {
	//debug
    if (debug) {
      print(" Client Started");
    }
    while (true){
      Message msg = receive();
      
      // if the message is a client reply message
      if (msg instanceof ClientReplyMessage) {
        ClientReplyMessage rqstRply = (ClientReplyMessage) msg;
        
        // the command is put
        if (rqstRply.cmd instanceof Put) {
          if (rqstRply.suc) {
            sm.updateWrite(rqstRply.write);
          } else {
            // todo
            System.out.print("Put failed, to be updated");
          }
        }
        // the command is delete
        if (rqstRply.cmd instanceof Delete) {
          if (rqstRply.suc) {
            sm.updateWrite(rqstRply.write);
          } else {
            // todo
            System.out.print("Del failed, to be updated");
          }
        }
        
        // the command is get
        if (rqstRply.cmd instanceof Get) {
          if (rqstRply.suc) {
            if (!rqstRply.url.equals("NOT_FOUND")) {
              sm.updateRead(rqstRply.write);
            }
            System.out.println(rqstRply.cmd.song + ":" + rqstRply.url);
          } else {
            System.out.println(rqstRply.cmd.song + ":ERR_DEP");
          }
        }
      }
    }
  }

  // list of user command
  /**
   * Put command, Write
   * @param name key
   * @param url  value
   */
  public void put (String name, String url) {
    if (serverId < 0) {
      System.out.println("disconnected with Server!");
      return;
    }
    ClientCommand cmd = new Put(name, url);
    ClientMessage rqst = new ClientMessage(pid, serverId, cmd, sm);
    send(rqst);
  }

  /**
   * Delete command, Write
   * @param name key
   */
  public void delete (String name) {
	 // if server id is negative
	 // then it is disconnect ot that server, which means error happens
    if (serverId < 0) {
      System.out.println("disconnected with Server!");
      return;
    }
    ClientCommand cmd = new Delete(name);
    ClientMessage rqst = new ClientMessage (pid, serverId, cmd, sm);
    send(rqst);
  }

  /**
   * Get command, Read
   * @param name key
   */
  public void get (String name) {
    if (serverId < 0) {
      System.out.println("disconnected with Server!");
      return;
    }
    ClientCommand cmd = new Get(name);
    ClientMessage rqst = new ClientMessage (pid, serverId, cmd, sm);
    send(rqst);
  }

  // a client connect to server
  public void connectTo(int id) {
    serverId = id;
  }

  // a client disconnect to a server
  public void disConnect() {
    serverId = -1;
  }
}



