package org.processmining.alphaminer.abstractions;

import java.util.HashSet;

public class Node {
	public String activity;
	public HashSet<Node> connections;
	public boolean visited;
	public boolean partitioned;
	
	public Node(String activity) {
		this.activity = activity;
		connections = new HashSet<>();
		visited = false;
	}
}
