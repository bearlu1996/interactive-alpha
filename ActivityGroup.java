package org.processmining.alphaminer.abstractions;

import java.util.HashSet;
public class ActivityGroup {
	
	public HashSet<Node> nodes;
	public int subgraphIndex;
	
	public ActivityGroup() {
		nodes = new HashSet<>();
	}
	
	public void addNode(Node node) {
		nodes.add(node);
	}
	
	public boolean verify() {
		
		for(Node a:nodes) {
			
			for(Node b:nodes) {
				if(a.connections.contains(b) == true) {
					return false;
				}
			}
				
		}
		
		return true;
		
	}
	
	public boolean equals(ActivityGroup group) {
		return this.nodes.equals(group.nodes);
	}
}