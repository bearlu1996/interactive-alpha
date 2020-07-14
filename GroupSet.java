package org.processmining.alphaminer.abstractions;

import java.util.LinkedList;

public class GroupSet {
	public LinkedList<ActivityGroup> groups;
	
	public GroupSet() {
		groups = new LinkedList<>();
	}
	
	public void removeIdentical() {
		
		for(int a = 0;a < groups.size();a ++) {
			ActivityGroup group1 = groups.get(a);
			for(int b = a + 1;b < groups.size();b ++) {
				ActivityGroup group2 = groups.get(b);
				if(group1.equals(group2)) {
					groups.remove(group2);
					b --;
				}
			}
			
		}
		
	}
	
}