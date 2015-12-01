package com.cs380d.application;

import java.util.HashSet;
import java.util.Set;

import com.cs380d.framework.Config;
import com.cs380d.framework.NetController;

public class Server extends Node {

	private static boolean paused = false; // Master pauses all anti-entropy
	public HashSet<Integer> serverIds;
	
	public Server(int id, Set<Integer> set) {
		super(id);	
		serverIds = new HashSet<Integer>();
		for (Integer i : set)
			this.serverIds.add(i);
		serverIds.add(pid);
		config = new Config(pid,serverIds);
		nc = new NetController(config);
		for (int s : serverIds) 
			nc.add(s);		
	}
	
	/**
	 * AntiEntropyReply and AntiEntropyRequest should be blocked if paused.
	 */
		
	public static void pauseAnti_Entropy() {
		Server.paused = true;
	}

	public static void startAnti_Entropy() {
		Server.paused = false;
	}

}
