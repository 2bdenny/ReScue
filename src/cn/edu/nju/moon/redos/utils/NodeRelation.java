package cn.edu.nju.moon.redos.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.moon.redos.regex.ReScuePattern.Node;

public class NodeRelation {
	private Map<Node, Set<Node>> nodeParents = new HashMap<Node, Set<Node>>();
	public void addParent(Node child, Node parent) {
		if (parent == null) return; // No parent for root node
		
		if (nodeParents.containsKey(child)) nodeParents.get(child).add(parent);
		else {
			Set<Node> parents = new HashSet<Node>();
			parents.add(parent);
			nodeParents.put(child, parents);
		}
	}
	public void clearParents() {
		nodeParents.clear();
	}
	public boolean haveSameParent(Node n1, Node n2) {
		if (n1 == null || n2 == null) return false;
		if (nodeParents.containsKey(n1) && nodeParents.containsKey(n2)) {
			Set<Node> intersection = new HashSet<Node>(nodeParents.get(n1));
			intersection.retainAll(nodeParents.get(n2));
			if (intersection.size() > 0) return true;
		}
		return false;
	}
	
	public void print() {
		System.out.println(nodeParents.size());
		for (Map.Entry<Node, Set<Node>> e : nodeParents.entrySet()) {
			System.out.print(e.getKey().toString().split("\\$")[1] + ": ");
			for (Node n : e.getValue()) {
				System.out.print(n.toString().split("\\$")[1] + " ");
			}
			System.out.println();
		}
	}
}
