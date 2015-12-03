package com.cs380d.utility;

import java.util.Iterator;
import java.util.PriorityQueue;

import com.cs380d.command.ClientCommand;
import com.cs380d.command.Delete;
import com.cs380d.command.Put;


/**
 * Log of all the Writes on a Server
 * ordered by CSN..
 * commmitable
 * @author Tian Zhang
 */
public class WriteLog {
  PriorityQueue<Writes> log;

  // Default constructor
  public WriteLog() {
    log = new PriorityQueue<Writes>();
  }
  
  //   get the iterator for the linked list
  public Iterator<Writes> getIterator () {
    return new PqIter(log);
  }
 
  //add a log entry
  public void add (Writes entry) {
    if (!contains(entry)) {
      log.offer(entry);
    }
  }

  // see whether it contasins a log entry
  public boolean contains(Writes entry) {
    Iterator<Writes> it = getIterator();
    while (it.hasNext()) {
      Writes w = it.next();
      if (w.sameAs(entry) ) {
        return true;
      }
    }
    return false;
  }

  /**
   * Commit a tentative write
   * remove from original location, insert again.
   * @param entry
   * @return
   */
  public boolean commit (Writes entry) {
    Iterator<Writes> it = getIterator();
    while (it.hasNext()) {
      Writes w = it.next();
      if (w.sameAs(entry) && w.csn != entry.csn) {
        log.remove(w);
        log.offer(entry);
        return true;
      }
    }
    return false;
  }

  /**
   * Get last relevant write for a Read command
   * todo: update datastructure to doublely linked list
   */
  public Writes lastRelevantWrite(String song) {
    Writes last = null;
    Iterator<Writes> it = getIterator();
    while (it.hasNext()) {
      Writes cur = it.next();
      if (cur.command instanceof ClientCommand) {
        String name = ((ClientCommand)cur.command).song;
        if (name.equals(song)) {
           last = cur;
        }
      }
    }
    return last;
  }

  // print the write log
  public void print() {
    Iterator<Writes> it = getIterator();
    while (it.hasNext()) {
      Writes cur = it.next();
      if (cur.command instanceof Put) {
        Put cmd = (Put) cur.command;
        System.out.print("PUT:(");
        System.out.print(cmd.song + ", " + cmd.url);
      } else if (cur.command instanceof Delete) {
        Delete cmd = (Delete) cur.command;
        System.out.print("DELETE:(");
        System.out.print(cmd.song);
      } else {
        continue;
       
      }
      System.out.println("):" + (cur.csn == Integer.MAX_VALUE ?
                                                  "FALSE" : "TRUE"));
    }
  }

  // convert the write log to string
  public String toString() {
    StringBuilder sb = new StringBuilder();
    Iterator<Writes> it = getIterator();
    sb.append("=============== Write Log ===============\n");
    while (it.hasNext()) {
      Writes cur = it.next();
      sb.append(cur.toString());
      sb.append('\n');
    }
    sb.append("=============== Write End ===============\n");
    return sb.toString();
  }

  /**
   * Build iterator on a new copy of queue
   */
  class PqIter implements Iterator<Writes> {
    final PriorityQueue<Writes> pq;
    public PqIter(PriorityQueue <Writes> source) {
      pq = new PriorityQueue(source);
    }

    @Override
    public boolean hasNext() {
      return pq.peek() != null;
    }

    @Override
    public Writes next() {
      return pq.poll();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("");
    }
  }
}
