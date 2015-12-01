package com.cs380d.application;

import com.cs380d.framework.Config;
import com.cs380d.framework.NetController;

public class Node {

	public int pid;
	protected NetController nc;
	protected Config config;
	
	public Node(int id) {
		pid = id;	
	}		  

	public Node() {
		
	}

	public void shutdown() {
		nc.shutdown();
	}
	
}
