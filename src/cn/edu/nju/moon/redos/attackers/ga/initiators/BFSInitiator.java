package cn.edu.nju.moon.redos.attackers.ga.initiators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Initiator;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.regex.ReScuePattern.Node;
import cn.edu.nju.moon.redos.utils.StringUtils;

/**
 * BFS Initiator
 */
public class BFSInitiator implements Initiator {
	private double INIT_THRESHOLD = RedosAttacker.FIRST_THRESHOLD;
//	private boolean isSeeding = false;
	
	public BFSInitiator(boolean isSeeding){
//		this.isSeeding = isSeeding;
	}
	
	public BFSInitiator(double init_threshold) {
		this.INIT_THRESHOLD = init_threshold;
	}
	
	@Override
	public int initiate(Population pop, ReScuePattern pattern, List<String> slices, List<String> prefixes) {
		// All of the appending strings
		List<String> apds = new ArrayList<String>();
		apds.addAll(slices);
		for (int i = 0; i < StringUtils.allChars.length(); i++)
			apds.add("" + StringUtils.allChars.charAt(i));

		// Generate roots
		for (String s : apds) {
			ReScueMatcher m = pattern.matcher(s,  new Trace(INIT_THRESHOLD));
			Trace c = m.find();
			if (c.attackSuccess()) {
				pop.add(c);
				return Initiator.INIT_ATTACK_FOUND;
			}
			if (c.effectiveStr.length() > 0 && c.effectiveStr.length() < GeneticAttacker.INIT_STR_LEN && !pop.contains(c) && !pop.isContainAllNodesof(c)) {
				pop.add(c);
			}
		}

		Queue<Node> remainNodes = new LinkedList<Node>(); // To be searched
		Set<Node> coveredNodes = pop.allNodes(); // Currently covered
		Set<Node> visitedNodes = new HashSet<Node>();

		// Initiate search list
		if (pattern.root != null)
			remainNodes.add(pattern.root);
		// BFS
		while (!remainNodes.isEmpty()) {
			Node cur = remainNodes.peek(); // Pick a node from the list
			if (visitedNodes.contains(cur)) // Searched yet
				remainNodes.poll();
			else {
				// Haven't been searched
				if (!coveredNodes.contains(cur)) {
					Trace target = null;
					// For all chromosome in the population
					for (int i = 0; i < pop.size(); i++) {
						Trace c = pop.get(i);
						// Append all appendix string for each
						for (String s : apds) {
							ReScueMatcher m = pattern.matcher(c.effectiveStr + s,  new Trace(INIT_THRESHOLD));
							Trace nc = m.find();
							if (nc.attackSuccess()) {
								pop.add(nc);
								return Initiator.INIT_ATTACK_FOUND;
							}
							if (nc.effectiveStr.length() > 0
									&& nc.effectiveStr.length() < GeneticAttacker.INIT_STR_LEN && !pop.contains(nc) && !pop.isContainAllNodesof(nc)) {
								if (nc.getAllNodes().contains(cur)) {
									target = nc;
									break;
								}
							}
						}
						if (target != null)
							break;
					}
					if (target != null) {
						pop.add(target);
						coveredNodes = pop.allNodes(); // TODO: To be increasingly
					}
				}

				// BFS List
				remainNodes.poll();
				List<Node> children = pattern.getChildNodes(cur);
				if (!children.isEmpty())
					for (Node n : children)
						if (n != null)
							remainNodes.add(n);
				// Should not revisit
				visitedNodes.add(cur);
			}
		}
		return Initiator.INIT_SUCCESS;
	}

}
