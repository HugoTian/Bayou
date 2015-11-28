package com.cs380d.application;

import java.util.HashSet;
import java.util.Set;

public class Server extends Node {

	private static boolean paused = false; // Master pauses all anti-entropy
	
	public Server(int id, Set<Integer> set) {
		super(id, set);		
	}
	
/**
 * AntiEntropyReply and AntiEntropyRequest should be blocked if paused.
 */
	
	public void shutdown() {
		nc.shutdown();
	}
	
	public static void pauseAnti_Entropy() {
		paused = true;
	}

	public static void startAnti_Entropy() {
		paused = false;
	}

}
