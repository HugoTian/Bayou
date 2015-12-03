package com.cs380d.application;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import com.cs380d.command.ClientCommand;
import com.cs380d.command.Command;
import com.cs380d.command.Create;
import com.cs380d.command.Delete;
import com.cs380d.command.Get;
import com.cs380d.command.Put;
import com.cs380d.command.Retire;
import com.cs380d.command.ServerCommand;
import com.cs380d.message.AntiEntroyAckMessage;
import com.cs380d.message.AntiEntroyReplyMessage;
import com.cs380d.message.AntiEntroyRequestMessage;
import com.cs380d.message.ClientMessage;
import com.cs380d.message.ClientReplyMessage;
import com.cs380d.message.CreateMessage;
import com.cs380d.message.CreateReplyMessage;
import com.cs380d.message.Message;
import com.cs380d.message.PrimaryMessage;
import com.cs380d.utility.PlayList;
import com.cs380d.utility.ReplicaID;
import com.cs380d.utility.VersionVector;
import com.cs380d.utility.WriteLog;
import com.cs380d.utility.Writes;


/**
 * Server/Replica class
 */
public class Server extends Node { 
  // logic clock
  int timeStamp = 1;
  //CSN
  int csnStamp = 1;
  
  //whether the server is a primary server
  boolean isPrimary = false;

  // maximum CSN
  int maxCSN = 0;

  // the replica ID
  public ReplicaID rid = null;
  // the connected list
  public ArrayList<Integer> connected = new ArrayList<Integer>();
  // play list, which is the Database
  public PlayList playList = new PlayList();
  // the write log
  public WriteLog writeLog = new WriteLog();
  // version vector 
  public VersionVector vv = new VersionVector();
  // ready to retire
  boolean toRetire = false;
  // can be retire after anti entropy message sent
  boolean retired = false;
 
  /**
   * Primary server, start with id = 0
   * @param id
   */
  public Server(int id) {
    super(id);
    rid = new ReplicaID(id);
    isPrimary = true;
    start();
  }

  /**
   * Non-primary server, no need to remember the primary ID.
   * @param id
   * @param primary
   */
  public Server(int id, int primary) {
    super(id);
    start();
    send(new CreateMessage(pid, primary));
  }

  // get the name of server
  public String name () {
    return "Server";
  }

  // whether it is primary
  public boolean isPrimary() {
    return isPrimary;
  }

  // get the next time stamp
  public int nextTimeStamp () {
    if (debug) {
      print("nextTimeStamp: " + timeStamp);
    }
    return ++timeStamp;
  }

  // get the current time stamp
  public int currTimeStamp () {
    return timeStamp;
  }

  // get csn
  public int getCSN() {
    maxCSN = csnStamp;
    if (debug) {
      print("Get new CSN: " + csnStamp);
    }
    return csnStamp++;
  }

  /**
   * Server Thread, message handler
   */
  public void run() {
	// debug mode
    if (debug) {
      print("Started");
    }
    while (!retired){
      Message msg = receive();

      if (debug) {
        print("Received" + msg.toString());
      }
      // case specified by the type of commmand
      
      /* request from client 
       * 
       */
      if (msg instanceof ClientMessage) {
        ClientMessage rqst = (ClientMessage) msg;
        firstTimeClientCmd(rqst);
      }

      /* create write from other connected server 
       * 
       */
      else if (msg instanceof CreateMessage) {
        CreateMessage create = (CreateMessage) msg;
        firstTimeCreateCmd(create);
      } 
      
      else if (msg instanceof CreateReplyMessage) {
        CreateReplyMessage cReply = (CreateReplyMessage) msg;
        setUp(cReply);
      }

      /* anti-entropy messages */
      else if (msg instanceof AntiEntroyRequestMessage) {
        send(new AntiEntroyReplyMessage(pid, msg.src, vv, maxCSN));
      } 
      
      else if (msg instanceof AntiEntroyReplyMessage) {
        AntiEntroyReplyMessage m = (AntiEntroyReplyMessage) msg;
        anti_entropy(m);
        if (toRetire) {
          retire(msg.src);
        }
      } 
      
      else if (msg instanceof AntiEntroyAckMessage) {
        AntiEntroyAckMessage ackMsg = (AntiEntroyAckMessage) msg;
        antiEntropyACKHandler(ackMsg);
      }

      /* primary handoff Message */
      else if (msg instanceof PrimaryMessage) {
        PrimaryMessage pho = (PrimaryMessage) msg;
        isPrimary = true;
        csnStamp = pho.curCSN;
      }
    }
  }


  /**
   * Initialization upon receiving reply from create write
   * set up the server , and ready to work
   * @param cReply
   */
  public void setUp(CreateReplyMessage cReply) {
    if (debug) {
      print("Set up Server!");
    }
    rid = cReply.rid;
    timeStamp = rid.acceptTime + 1;
    vv.put(rid.toString(), 0);
  }


  /**
   *  initiate anti-entropy process with every connected node
   */
  public void spreadGossip() {
    for (int node : connected) {
      send(new AntiEntroyRequestMessage(pid, node));
    }
  }

  /**
   *  initiate anti-entropy process with every connected node
   *  Get the message from src, no need to send it back again
   */
  public void spreadGossip(int src) {
    for (int node : connected) {
      if (node != src) {
        send(new AntiEntroyRequestMessage(pid, node));
      }
    }
  }
  
  
  /**
   * Get write from client for the first time,
   * no need to roll-back as I am the first one to have this Write
   */
  public void firstTimeClientCmd (ClientMessage rqst) {
	// check domination
    if (!rqst.sm.isDominatedBy(vv)) {
      send(new ClientReplyMessage(rqst));
      return;
    }
    // if a write
    if (rqst.isWrite()) {
    	
      
      if (playList.update(rqst.cmd)) {   // update database
    	//generate write entry
        int acceptTime = nextTimeStamp();
        int csn = isPrimary() ? getCSN() : Integer.MAX_VALUE;
        Writes write = new Writes(csn, acceptTime, rid, rqst.cmd);
        //update write log
        writeLog.add(write);
        //update version vector
        vv.put(rid.toString(), currTimeStamp());
        // send reply to client
        send(new ClientReplyMessage(rqst, write));
        // spread the gossip
        spreadGossip();
      } else {
    	// send a failure reply
        send(new ClientReplyMessage(rqst));
      }
    } else if (rqst.isRead()) {
      String song = rqst.cmd.song;
      String url = playList.get(song);
      Writes w = writeLog.lastRelevantWrite(song);
      send(new ClientReplyMessage(rqst, url, w));
    }
  }


  /**
   * Get Create from server for the first time,
   * no need to roll-back as I am the first one to have this Write
   *
   */
  public void firstTimeCreateCmd (CreateMessage msg) {
    int acceptTime = nextTimeStamp();
    ReplicaID newId = new ReplicaID(acceptTime, rid, msg.src);
    int csn = isPrimary() ? getCSN() : Integer.MAX_VALUE;
    Create create = new Create(newId, acceptTime);
    Writes entry = new Writes(csn, acceptTime, rid, create);
    writeLog.add(entry);

    vv.put(rid.toString(), currTimeStamp());
    vv.put(newId.toString(), currTimeStamp() + 1);

    // send ack to server
    // some new server would ingore the first message,
    // send a dummy message here to refresh, no side effect
    send(new Message(pid, msg.src));
    send(new CreateReplyMessage(pid, msg.src, newId));
    // update with neighbors, anti-entropy
    spreadGossip();
  }


  /**
   * Propagating committed writes upon receiving reply from R
   */
  public void anti_entropy(AntiEntroyReplyMessage msg) {
	//debug mode
    if (debug) {
      System.out.println("\n\n");
      System.out.println("\n\n");
      print("Anti_Entropy:");
      System.out.println("My VV: " + vv.toString());
      System.out.println("Server " + msg.src + "'s VV: " + msg.vv.toString());
      System.out.println(writeLog);

      System.out.println("\n\n");
      System.out.println("\n\n");
    }

    //iterate through the write log
    Iterator<Writes> it = writeLog.getIterator();
    while (it.hasNext()) {
      Writes w = it.next();
      String rjID = w.replicaId.toString();         // R_j, owner of the write
      String rkID = w.replicaId.parent.toString();  // R_k
      //first compare the write csn
      if (w.csn <= msg.CNS) {
        // already committed in R
        continue;
      }  else if (w.csn < Integer.MAX_VALUE) {   // > msg.CNS
        //committed write unknown to R 
        if (msg.hasKey(rjID)) {
          if ( w.acceptTime <= msg.getTime(rjID)) {
            // R has the write, but doesn't know it is committed  
            send(new AntiEntroyAckMessage(pid, msg.src, w, true));
          } else {
            // R don't have the write, add committed write  
            send(new AntiEntroyAckMessage(pid, msg.src, w));
          }
        } else {
          /*  the Missing VV entry, don't know of rjID ...  */
          int riVrk = msg.hasKey(rkID) ? msg.getTime(rkID) : -1;
          int TSkj = w.replicaId.acceptTime;
          if (riVrk < TSkj) {
            send(new AntiEntroyAckMessage(pid, msg.src, w));
          }
        }
      } else {
        /* all tentative writes */
        if (msg.hasKey(rjID)) {
          if (msg.getTime(rjID) < w.acceptTime) {
            send(new AntiEntroyAckMessage(pid, msg.src, w));
          }
        } else {
          /*  the Missing VV entry, don't know of rjID ...  */
          int riVrk = msg.hasKey(rkID) ? msg.getTime(rkID) : -1;
          int TSkj = w.replicaId.acceptTime;
          if (riVrk < TSkj) {
            send(new AntiEntroyAckMessage(pid, msg.src, w));
          }
        }
      }
    }
  }


  /**
   * handler for write updates through anti-entropy process
   * @param ackMsg
   */
  public void antiEntropyACKHandler(AntiEntroyAckMessage ackMsg) {
    if (ackMsg.commit) {
      /* I have the write, just need to commit it
       */
      if (writeLog.commit(ackMsg.write)) {
        // successfully updated 
        maxCSN = Math.max(maxCSN, ackMsg.write.csn);
        updatePlayList();
        spreadGossip();
      }
    } else {
     // I do not have this write
      boolean newCommitted = false;
      Writes w = ackMsg.write;
      if (isPrimary() && ackMsg.write.csn == Integer.MAX_VALUE) {
        w.csn = getCSN();
        newCommitted = true;
      }
      // add to playlist
      writeLog.add(w);
      updatePlayList();

      vv.put(rid.toString(), currTimeStamp());
      vv.put(w.replicaId.toString(), w.acceptTime);

      // update maxCSN;
      if (!isPrimary() && w.csn != Integer.MAX_VALUE) {
        maxCSN = Math.max(maxCSN, w.csn);
      }

      if (newCommitted) {
        spreadGossip();
      } else {
        spreadGossip(ackMsg.src);
      }
    }
  }


  /**
   * update playlist on every new committed write,
   * as there might be some potential roll back.
   */
  public void updatePlayList() {
    Hashtable<String, String> tmp = new Hashtable<String, String> ();
    Iterator<Writes> it = writeLog.getIterator();
    while (it.hasNext()) {
      Command cmd = it.next().command;
      
      // operation on the tmp hash table
      // based on the client command
      if (cmd instanceof ClientCommand) {
        if (cmd instanceof Put) {
          Put put = (Put) cmd;
          tmp.put(put.song, put.url);
        } else if (cmd instanceof Delete) {
          Delete del = (Delete) cmd;
          tmp.remove(del.song);
        } else if (cmd instanceof Get) {
          // no change here
        }
      } 
      // the command is a server command
      else if (cmd instanceof ServerCommand) {
        ServerCommand scmd = (ServerCommand) cmd;
        if (scmd instanceof Create) {
          String id = scmd.rid.toString();
          if (!vv.hasKey(id)) {
            vv.put(id, scmd.acceptTime);
          }
        } else if (scmd instanceof Retire) {
          String id = scmd.rid.toString();
          if (vv.hasKey(id)) {
            vv.remove(id);
          }
          if (connected.contains(scmd.rid.pid)) {
            connected.remove(((Integer)scmd.rid.pid));
          }
        }
      }
    }
    playList.pl = tmp;
  }

  // A server connected to server

  public void connectTo(int id) {
   if (!connected.contains(id)) {
     connected.add(id);
     send(new AntiEntroyRequestMessage(pid, id));
     if (debug) {
       print("Reconnected with " + id);
       print(connected.toString());
     }
   }
  }

  // a server disconnect to server
  public void disconnectWith(int id) {
    if (connected.contains(id)) {
      connected.remove((Integer)id);
      if (debug) {
        print("Disconnected with " + id);
        print(connected.toString());
      }
    } else {
      print("Disconnected with " + id + " no connection found...");
      print(connected.toString());
    }
  }

  // update the info for connected node
  public void updateConnected(ArrayList<Integer> serverList) {
    connected = new ArrayList<Integer>(serverList);
    if (connected.contains(pid)) {
      connected.remove((Integer) pid);
    }
    if (debug) {
      print("New Server joined: ");
      print(connected.toString());
    }
  }

  // print the log
  public void printLog() {
    writeLog.print();
  }


  /**
   * Receive retire command from master
   * Issue retire write to itself
   * must be called by Master thread for blocking.
   */
  public synchronized void toRetire() {
    int acceptTime = nextTimeStamp();
    int csn = isPrimary() ? getCSN() : Integer.MAX_VALUE;
    Retire rcmd = new Retire(rid, acceptTime);
    Writes entry = new Writes(csn, acceptTime, rid, rcmd);
    writeLog.add(entry);
    vv.put(rid.toString(), currTimeStamp());

    /* assume has at least one neighbor right now (piazza @86)
     * otherwise need keep gossiping periodically.
     */
    assert connected.size() != 0;
    spreadGossip();
    toRetire = true;
    while (!retired) {
      try {
        wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Finishing the retirement protocol
   *  if primary, hand off duty to other server
   *  stop looping
   *  unblock master thread
   *  release all resources
   */
  public synchronized void retire(int src) {
    if (isPrimary) {
      // set up new primary with current CNS counter, globally unique
      send(new PrimaryMessage(pid, src, csnStamp));
    }
    retired =true;
    shutdown = true;
    nc.shutdown();
    // wake up the blocked master
    notifyAll();
  }
}
