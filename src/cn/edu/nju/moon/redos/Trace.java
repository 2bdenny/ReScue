package cn.edu.nju.moon.redos;

import java.util.HashSet;
import java.util.Set;

import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScuePattern.Node;

/*
 * The trace of match detail.
 */
//public class Trace implements Comparable<Trace>{
public class Trace{
	public double threshold = 1e5; 	// First judge: it's vulnerable when the number of match steps larger than CATASTROPHIC_THRESHOLD
	
	public boolean matchSuccess; 	// String accepted or not
	public String str; 				// The Input string
	public String effectiveStr; 	// The effective substring of str
	private Set<Node> allNodes; 	// All Nodes
	private int logHash;
	private int logSize;
	private int finishIndex; 		// The index of effective string's end
	private int[] eachStep;
	
	public Trace(double threshold) {
		this.matchSuccess = false;
		this.str = null;
		this.effectiveStr = null;
		this.allNodes = new HashSet<Node>();
		this.finishIndex = -1;
		
		this.logSize = 0;
		this.logHash = -1;
		
		this.threshold = threshold;
	}
	
	@Override
	public boolean equals(Object c) {
		if (c instanceof Trace) {
			Trace tmp = (Trace) c;
			if (tmp.logSize == this.logSize) {
				return tmp.logHash == this.logHash;
			}
		}
		return false;
	}
	
	/**
	 * Log the next match() calling
	 * @param node Current node
	 * @param idx Current index in the input string
	 * @return Whether current match steps larger than the First Judge's threshold
	 */
	public boolean logMatch(Node node, int idx) {
		finishIndex = finishIndex > idx ? finishIndex : idx;
		this.eachStep[idx] ++;
		
		if (this.logSize == 0) this.logHash = node.hashCode();
		else this.logHash = this.logHash ^ node.hashCode();
		
		this.logSize++;
		
		this.allNodes.add(node);
		
    	return logSize <= this.threshold;
	}
	
	/**
	 * Log the next match() calling
	 * This log step is working for the Second Judge
	 * @param idx Current index in the input string
	 * @return Whether current match steps larger than the Second Judge's threshold
	 */
	public boolean logValidate(int idx) {
		finishIndex = finishIndex > idx ? finishIndex : idx;
		
		this.eachStep[idx] ++;
		
		this.logSize++;
		
    	return logSize <= this.threshold;
	}
	
	/**
	 * If pop is not null, calculate the coverage
	 * else calculate the ratio
	 * @param pop
	 * @return
	 */
	public double score(Population pop) {
		if (pop == null) {
			return (double) logSize / ((double) effectiveStr.length() + 1);
		} else {
			if (!pop.contains(this) && !pop.isContainAllNodesof(this)) return 1.0;
			return -1.0;
		}
	}
	
	/**
	 * Judge
	 * @return
	 */
	public boolean attackSuccess() {
		return this.logSize > this.threshold;
	}	
	
	/**
	 * Item of the log (for the First Judge)
	 */
	public class MatchStep {
		public Node node; // Current node
		public int index; // Current index in the string
		
		public MatchStep(Node node, int idx) {
			this.node = node;
			this.index = idx;
		}
	}
	
	public void printStep() {
		for (int i = 0; i < this.str.length(); i++) {
			System.out.println(i + ": " + this.str.charAt(i) + ": " + this.eachStep[i]);
		}
	}
	
	/**
	 * Store the total match string
	 * @param input
	 */
	public void setStr(String input) {
		this.str = new String(input);
		this.eachStep = new int[input.length() + 1];
		for (int i = 0; i < this.eachStep.length; i++) this.eachStep[i] = 0;
	}
	
	/**
	 * Store the effective match string
	 * @param input
	 */
	public void setEffectiveStr(String input) {
		if (this.str == null) this.str = new String(input);
		this.effectiveStr = str.substring(0, finishIndex);
	}
	
	/**
	 * Is the string too long? (compared with {@GeneticAttacker}.MAX_STR_LEN})
	 * @return
	 */
	public boolean tooLong() {
		return effectiveStr.length() > GeneticAttacker.MAX_STR_LEN;
	}
	
	/**
	 * Get match step (work for {@ValidatePattern} & (@ValidateMatcher})
	 * @return
	 */
	public int getMatchSteps() {
		return this.logSize;
	}
	
	public Set<Node> getAllNodes() {
		return this.allNodes;
	}
}

