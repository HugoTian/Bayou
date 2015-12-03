package com.cs380d.utility;

import java.util.Hashtable;
import java.util.Map;

import com.cs380d.command.ClientCommand;
import com.cs380d.command.Delete;
import com.cs380d.command.Put;

/*
 *  Class for playlist
 *  @author Tian Zhang 
 */
public class PlayList {
  public Hashtable<String, String> pl;

  // Constructor
  public PlayList () {
    pl = new Hashtable<String, String>();
  }

  // whether it contain a song
  public boolean containsSong(String song) {
    return pl.containsKey(song);
  }
  
  
  // add a song to the playlist
  public boolean add(String song, String url){
    pl.put(song, url);
    return true;
  }

  // delete a song in playlist
  public boolean delete(String song){
    if(pl.containsKey(song)){
      pl.remove(song);
      return true;
    } else {
      return false;
    }
  }

  //get a song in playlist
  public String get(String song) {
    if (pl.containsKey(song)) {
      return pl.get(song);
    } else {
      return "NOT_FOUND";
    }
  }

  // update a song in playlist
  public boolean update (ClientCommand cmd) {
    if (cmd instanceof Put) {
      return add(cmd.song, cmd.url);
    } else if (cmd instanceof Delete) {
      return delete(cmd.song);
    } else {
      return pl.containsKey(cmd.song);
    }
  }

  // print the platlist
  public void print() {
    System.out.println();
    System.out.println("==================== PlayList =============");
    int i = 1;
    for (Map.Entry<String, String> item : pl.entrySet()) {
      System.out.println(i + ". Song Name: " + item.getKey() + "\tURL: " + item.getValue());
      i++;
    }
    System.out.println("=============== End  ======================");
    System.out.println();
  }


}
