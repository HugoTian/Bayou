package com.cs380d.application;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.cs380d.framework.Config;
import com.cs380d.framework.NetController;
import com.cs380d.message.Message;
import com.cs380d.utility.Constants;
import com.cs380d.utility.MessageInbox;

/**
 * General node for client and server
 * Provide communication abstraction for client/servers
 */


public class Node extends Thread {
  //debug info
  public boolean debug = Constants.debug;
  // whether to shut down this node
  public boolean shutdown = false;
  // whether to pause this node
  private volatile boolean pause = false;
  
  public int pid;
  // the inbox for message
  public MessageInbox messageInbox;
  // Configuration file for socket communication
  public Config config;
  public NetController nc;
  // private pause message queue
  private ArrayList<Message> pauseList = new ArrayList<Message>();


  // Default constructor
  public Node(int id) {
    pid = id;
    messageInbox = new MessageInbox();

    config = new Config(id, Constants.MAX_NODE);
    nc = new NetController(config);
    new Listener().start();
  }

  // pause the server
  public void pause() {
    pause = true;
  }

  /**
   * unpause the server
   */
  public void unPause() {
    pause = false;
    for(Message m : pauseList){
    	send(m);
    }
    pauseList.clear();
  }

  /**
   * Send message
   * @param msg
   */
  public void send(Message msg) {
    if (debug) {
      print("Sent the message: " + msg.toString());
    }
    // if pause, then 
    if (pause && msg.isAntiEntroymessage()) {
      pauseList.add(msg);
      return;
    }
    nc.sendMsg(msg.dst, toSendingString(msg));
  }

  public void deliver(Message msg) {
    if (pause && msg.isAntiEntroymessage()) {
      return;
    }
    messageInbox.enqueueMessage(msg);
  }

  public Message receive() {
    Message msg = messageInbox.dequeueMessage();
    return msg;
  }

  // print the message
  public void print(String msg) {
    System.out.println(name() + " " + pid + ": " + msg);
  }

  public String name() {
    return "Node";
  }

  /**
   * Translate the Message to a string to transmit through socket
   * Don't want to modify existing socket framework
   */
  public String toSendingString(Message msg) {
    String rst = "";
    try {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(msg);
      so.flush();

      //rst = bo.toString();
      rst = new String(Base64.encodeBase64(bo.toByteArray()));
    } catch (Exception e) {
      System.out.println(e);
    }
    return rst;
  }

  /**
   * Translate String to a Message upon receiving from socket
   * Don't want to modify existing socket framework
   */
  public Message translateStringMessage(String str) {
    Message msg = null;
    try {
      
      byte b[] = Base64.decodeBase64(str.getBytes());
      ByteArrayInputStream bi = new ByteArrayInputStream(b);
      ObjectInputStream si = new ObjectInputStream(bi);
      msg = (Message) si.readObject();
    } catch (Exception e) {
      System.out.println(e);
    }
    return msg;
  }


  /**
   * Inner listener thread for receive message
   */
  class Listener extends Thread {
    public void run() {
      while (!shutdown) {
        List<String> buffer = nc.getReceivedMsgs();
        for (String str : buffer) {
          Message msg = translateStringMessage(str);
          if (msg != null) {
            deliver(msg);
          }
        }
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
