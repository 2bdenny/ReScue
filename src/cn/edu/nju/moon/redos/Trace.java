package cn.edu.nju.moon.redos;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScuePattern.Node;

/*
 * The trace of match detail.
 */
//public class Trace implements Comparable<Trace>{
public class Trace{
	public double threshold = 1e5; // First judge: it's vulnerable when the number of match steps larger than CATASTROPHIC_THRESHOLD
	
	public boolean matchSuccess; // String accepted or not
	public String str; // The Input string
	public String effectiveStr; // The effective substring of str
	private Set<Node> allNodes; // All Nodes
	private int logHash;
	private int logSize;
	private int finishIndex; // The index of effective string's end

	// prefix + pump * N_PUMPS + suffix
	public String prefix;
	public String pump;
	public String suffix;

	private int[] eachStep;
	private boolean isDetail = false;
	private List<Node> logNode; // The path of match process
	public List<Node> getLogNode() {
		return logNode;
	}

	public List<Integer> getLogIdx() {
		return logIdx;
	}

	private List<Integer> logIdx;
	
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
	
	public Trace(double threshold, boolean isDetail) {
		this(threshold);
		this.logNode = new LinkedList<Node>();
		this.logIdx = new LinkedList<Integer>();
		this.isDetail = isDetail;
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
		if (this.isDetail) {
			this.logNode.add(node);
			this.logIdx.add(idx);
		}
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
			System.err.println(i + ": " + this.str.charAt(i) + ": " + this.eachStep[i]);
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
	 * Store the attack string components
	 * @param input
	 */
	public void setAttackString(String prefix, String pump, String suffix) {
    System.err.println("Trace: prefix <" + prefix + ">, pump <" + pump + ">, suffix <" + suffix + ">");
		this.prefix = prefix;
		this.pump = pump;
		this.suffix = suffix;
	}

	/**
	 * Get the attack string components: prefix
	 */
	public String getAttackPrefix() {
		return this.prefix;
	}

	/**
	 * Get the attack string components: pump
	 */
	public String getAttackPump() {
		return this.pump;
	}

	/**
	 * Get the attack string components: suffix
	 */
	public String getAttackSuffix() {
		return this.suffix;
	}

	/**
	 * Is the string too long? (compared with {@GeneticAttacker}.MAX_STR_LEN})
	 * @return
	 */
	public boolean tooLong() {
		return effectiveStr.length() > GeneticAttacker.MAX_STR_LEN;
	}
	
	/**
	 * Get match steps
	 * @return
	 */
	public int getMatchSteps() {
		return this.logSize;
	}
	
	public Set<Node> getAllNodes() {
		return this.allNodes;
	}
}

