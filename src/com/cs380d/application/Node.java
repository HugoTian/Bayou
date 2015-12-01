package com.cs380d.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
