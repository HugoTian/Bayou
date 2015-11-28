package com.cs380d.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.cs380d.framework.Config;
import com.cs380d.framework.NetController;

public class Node {

	public int pid;
	public HashSet<Integer> serverIds;
	protected NetController nc;
	protected Config config;

	public Node(int id, Set<Integer> IDset) {
		pid = id;
		serverIds = new HashSet<Integer>();
		for (Integer i : IDset)
			this.serverIds.add(i);
		serverIds.add(pid);

		config = new Config(pid,serverIds);
		nc = new NetController(config);
	
	}		  

	public Node() {
		
	}

}
