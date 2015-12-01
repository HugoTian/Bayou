package com.cs380d.application;

import java.util.HashSet;

import com.cs380d.framework.Config;
import com.cs380d.framework.NetController;

public class Client extends Node{
	
	Server server;
	
	public Client(int id, int srv_id) {
		super(id);
		HashSet<Integer> srv = new HashSet<Integer>();
		srv.add(id); 					//add myself for loopback/listener
		srv.add(srv_id);
		config = new Config(pid, srv);
		nc = new NetController(config);
		nc.add(srv_id);		

	}

}
