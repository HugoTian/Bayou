package com.cs380d.utility;

import java.util.Hashtable;
import java.util.Map;

import com.cs380d.command.ClientCommand;
import com.cs380d.command.Delete;
import com.cs380d.command.Put;

/*  Play list class that manage the add, delete, and get of song url pair
 *  @author Tian Zhang
 */

public class PlayList {
  // the play list
  public Hashtable<String, String> pl;

  // Default constructor
  public PlayList () {
    pl = new Hashtable<String, String>();
  }

  // Whether contains a song
  public boolean containsSong(String song) {
    return pl.containsKey(song);
  }
  
  //add song to play list
  public boolean add(String song, String url){
    pl.put(song, url);
    return true;
  }

  // delete a song from play list
  public boolean delete(String song){
    if(pl.containsKey(song)){
      pl.remove(song);
      return true;
    } else {
      return false;
    }
  }

  // get a song from play list
  // if nothing, then return not exist
  public String get(String song) {
    if (pl.containsKey(song)) {
      return pl.get(song);
    } else {
      return "NOT-EXIST";
    }
  }

  // Edit or update the son in playlist
  // In terms of client command
  public boolean update (ClientCommand cmd) {
    if (cmd instanceof Put) {
      return add(cmd.song, cmd.url);
    } else if (cmd instanceof Delete) {
      return delete(cmd.song);
    } else {
      return pl.containsKey(cmd.song);
    }
  }

  public void print() {
    System.out.println();
    System.out.println("=========================PlayList====================");
    int i = 1;
    for (Map.Entry<String, String> item : pl.entrySet()) {
      System.out.println(i + ". Song Name: " + item.getKey() + "\tURL: " + item.getValue());
      i++;
    }
    System.out.println("======================== End  =======================");
    System.out.println();
  }


}
