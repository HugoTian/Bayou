package com.cs380d.utility;


import java.util.LinkedList;
import java.util.List;

import com.cs380d.message.Message;

/**
 * Blocking Messsage Queue
 * ref: http://tutorials.jenkov.com/java-concurrency/blocking-queues.html
 * @author zhangtian
 */
public class MessageInbox {
  private List<Message> queue = new LinkedList<Message>();
  private int limit;

  public MessageInbox() {
    limit = 100000;
  }

  public synchronized void enqueueMessage(Message msg) {
    while (this.queue.size() == this.limit) {
      waitQ();
    }
    if (this.queue.size() == 0) {
      notifyAll();
    }
    this.queue.add(msg);
  }

  public synchronized Message dequeueMessage() {
    while (this.queue.size() == 0) {
      waitQ();
    }
    if (this.queue.size() == this.limit) {
      notifyAll();
    }

    return this.queue.remove(0);
  }


  public void waitQ() {
    try {
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
