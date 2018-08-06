package cn.edu.nju.moon.redos.attackers.ga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.regex.ReScuePattern.Node;

/**
 * Population of the genetic programming
 */
public class Population {	
	private List<Trace> pop;
	private Map<Node, Integer> nodes; // Population covered nodes
	
 	// ================Assistant methods section==================
	public Population() {
		pop = new ArrayList<Trace>();
		nodes = new HashMap<Node, Integer>();
	}
	
	/**
	 * Collect population covered nodes
	 * @param pop
	 * @return <node, count of node occurrence in chromosome>
	 */
	private Map<Node, Integer> collectNodes() {
		Map<Node, Integer> nodes = new HashMap<Node, Integer>();
		for (Trace c : pop) {
			Set<Node> curNodes = c.getAllNodes();
			for (Node n : curNodes) {
				if (nodes.containsKey(n)) {
					int count = nodes.get(n);
					nodes.put(n, count + 1);
				} else nodes.put(n, 1);
			}
		}
		return nodes;
	}

	// ================Operation on the population section==================
	/**
	 * Is population contains chromosome c?
	 * @param c
	 * @return
	 */
	public boolean contains(Trace c) {
		if (pop.isEmpty()) return false;
		else return pop.contains(c);
	}
	
	/**
	 * Can we remove chromosome c from population without declining the node coverage of the population
	 * @param c
	 * @return
	 */
	public boolean canDeleteWithoutCoverageDecline(Trace c) {
		if (contains(c)) {
			Set<Node> cNodes = c.getAllNodes();
			for (Node n : cNodes) {
				if (!nodes.containsKey(n)) { // If c in pop, should not print
					System.out.println("BUG: Population doesn't cover node!");
					return false;
				}
				if (nodes.containsKey(n) && nodes.get(n) <= 1) return false; // Cover a node by itself
			}
			return true;
		} else return false; // c is not in pop
	}
	
	/**
	 * Are all nodes of the chromosome covered by population? 
	 * @param c
	 * @return
	 */
	public boolean isContainAllNodesof(Trace c) {
		for (Node n : c.getAllNodes()) {
			if (!nodes.containsKey(n)) return false;
		}
		return true;
	}
	
	/**
	 * Clear population
	 */
	public void clear() {
		pop.clear();
		nodes.clear();
	}
	
	/**
	 * Get chromosome from population by index
	 * @param index
	 * @return
	 */
	public Trace get(int index) {
		if (index >= 0 && index < pop.size()) return pop.get(index);
		return null;
	}
	
	/**
	 * Add a chromosome to population
	 * @param c
	 */
	public void add(Trace c) {
		if (!contains(c) && c.effectiveStr.length() > 0) {
			pop.add(c);
			
			nodes = collectNodes();
		}
	}
	
	public int rollSum() {
		double score_ave = averageScore();
		int roll_sum = 0;
		for (Trace c : pop) {
			if (c.effectiveStr.length() <= 0)
				continue; // 0-length attack string should be removed
			int copies = (int) Math.ceil(c.score(null) / score_ave);
			roll_sum += copies;
		}
		return roll_sum;
	}
	
	public Trace getByRoll(int roll) {
		double score_ave = averageScore();
		int cur_roll = 0;
		for (Trace c : pop) {
			if (c.effectiveStr.length() <= 0)
				continue; // 0-length attack string should be removed
			int copies = (int) Math.ceil(c.score(null) / score_ave);
			
			if (cur_roll < roll && roll <= cur_roll + copies) return c;
			cur_roll += copies;
		}
		return null;
	}
	
	/**
	 * Remove redundant chromosomes without declining node coverage of the population
	 * (Minimum population cannot be promised)
	 */
	public void cleanSeeding() {
		nodes = collectNodes();
		
		for (Iterator<Trace> ic = pop.iterator(); ic.hasNext();) {
			Trace c = ic.next();
			if (c.effectiveStr.length() == 0 || canDeleteWithoutCoverageDecline(c)) {
				ic.remove();
				nodes = collectNodes();
			}
		}
	}
	
	/**
	 * Remove chromosomes whose score is less than threshold, without declining node coverage of the population
	 * @param threshold
	 */
	public void cleanIncubating() {
		nodes = collectNodes();
		double avg = popScore();
		
		for (Iterator<Trace> ic = pop.iterator(); ic.hasNext();) {
			Trace c = ic.next();
			if (c.effectiveStr.length() == 0 || (canDeleteWithoutCoverageDecline(c) && c.score(null) < avg)) {
				if (pop.size() <= GeneticAttacker.MAX_POP_SIZE) break;
				ic.remove();
				nodes = collectNodes();
			}
		}
	}
	
	// ================Information of the population==================
	/**
	 * Size of the population
	 * @return
	 */
	public int size() {
		return pop.size();
	}
	
	/**
	 * Covered nodes of the population
	 * @return
	 */
	public Set<Node> allNodes() {
		return nodes.keySet();
	}
	
	/**
	 * Count covered nodes of the population
	 * @return
	 */
	public int coverage() {
		Set<Node> curNodes = new HashSet<Node>();
		for (Trace c : pop) curNodes.addAll(c.getAllNodes());
		return curNodes.size();
	}
	
	/**
	 * Calculate average score of the population
	 * @param pop
	 * @return
	 */
	public double averageScore() {
		if (pop.isEmpty()) {
			System.out.println("pop is empty.");
			return 0.0;
		}
		
		double sum = 0.0;
		for (Trace c : pop) sum += c.score(null);
		return sum / pop.size();
	}
	
	public double popScore() {
		if (pop.size() <= GeneticAttacker.MAX_POP_SIZE) return averageScore();
		return findThreshold(GeneticAttacker.MAX_POP_SIZE, 0, pop.size()-1);
	}
	
	private double findThreshold(int ith, int start, int finish){
		if (start >= finish || start == ith) return pop.get(ith).score(null);
		
		int index = partitionPop(start, finish);
		
		if (index == ith) return pop.get(ith).score(null);
		else if (index > ith) return findThreshold(ith, start, index - 1);
		else return findThreshold(ith, index+1, finish);
	}
	
	private int partitionPop(int start, int finish) {
		if (start >= finish) return start;
		
		Trace pivot = pop.get(start);
		while (start < finish) {			
			while (finish > start && pop.get(finish).score(null) < pivot.score(null)) finish--;
			pop.set(start, pop.get(finish));
			while (finish > start && pop.get(start).score(null) >= pivot.score(null)) start++;
			pop.set(finish, pop.get(start));
		}
		pop.set(start, pivot);
		return start;
	}
	
	/**
	 * TODO Optimized to O(n)
	 * Calculate mid score of the population (deprecated)
	 * @param pop
	 * @return
	 */
//	@Deprecated
//	public static double midScore(List<Trace> pop) {
//		if (pop.isEmpty()) {
//			System.out.println("pop is empty");
//			return 0.0;
//		}
//		if (pop.size() == 1) return pop.get(0).score(null);
//		
//		List<Double> vals = new ArrayList<Double>();
//		for (int i = 0; i < pop.size(); i++) vals.add(pop.get(i).score(null));
//		Collections.sort(vals);
//		return vals.get(vals.size() / 2);
//	}

	/**
	 * Is population empty?
	 * @return
	 */
	public boolean isEmpty() {
		return pop.isEmpty();
	}
	
	// ================Print methods section==================
	
	/**
	 * Print all chromosomes in the population
	 */
	public void print() {
		System.out.print(pop.size() + ": ");
		for (Trace c : pop) {
			String out = "<" + c.getAllNodes().size() + " : " + c.score(null) + " : " + c.effectiveStr + ">";
			System.out.print(out);
		}
		System.out.println();
	}
	
	public void printNodes() {
		System.out.println(nodes.size() + ": ");
		for (Map.Entry<Node, Integer> item : nodes.entrySet()) {
			System.out.println(item.getKey().toString() + ": " + item.getValue());
		}
	}
}
