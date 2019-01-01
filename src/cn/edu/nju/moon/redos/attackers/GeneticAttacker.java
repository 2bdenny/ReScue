package cn.edu.nju.moon.redos.attackers;

import java.util.List;

import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.ga.Crossover;
import cn.edu.nju.moon.redos.attackers.ga.Initiator;
import cn.edu.nju.moon.redos.attackers.ga.Mutator;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.attackers.ga.crossovers.RollCrossover;
import cn.edu.nju.moon.redos.attackers.ga.initiators.GeneticInitiator;
import cn.edu.nju.moon.redos.attackers.ga.mutators.MultipleMutator;
import cn.edu.nju.moon.redos.attackers.pp.Pumper;
import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * Genetic Attacker
 * Generate attack string by genetic programming
 */
public class GeneticAttacker extends RedosAttacker {	
	// Some local variables here
	private List<String> slices; // All pure substrings extracted from the regex
	private ReScuePattern pattern; // The compiled regex
	private Population pop; // The population
	private List<String> prefixes; // All feasible prefixes
	
	// Some configurations here
	public static int MAX_POP_SIZE = 200; // Max size of population
	public static int INIT_STR_LEN = 64; // String length when init the population
	public static int MAX_STR_LEN = 128; // Max string length of attack strings
	private int MAX_GENERATIONS = 200; // Max generations
	public static double MUT_POSSIBILITY = 0.1;
	public static double CROSS_POSSIBILITY = 0.05;
	
	public GeneticAttacker() {
		pop = new Population();
	}
	
	/**
	 * Configuration can be modified by users
	 * @param sl
	 * @param ml
	 * @param pz
	 * @param g
	 */
	public GeneticAttacker(int sl, int ml, int pz, int g, double mp, double cp) {
		this();
		
		GeneticAttacker.INIT_STR_LEN = sl;
		GeneticAttacker.MAX_STR_LEN = ml;
		GeneticAttacker.MAX_POP_SIZE = pz;
		GeneticAttacker.MUT_POSSIBILITY = mp > 1 || mp < 0? 0.10 : mp;
		GeneticAttacker.CROSS_POSSIBILITY = cp > 1 || cp < 0? 0.05 : cp;
		this.MAX_GENERATIONS = g;
	}	

	@Override
	public Trace attack(ReScuePattern jdkPattern) {
		// Initiate variables
		pattern = jdkPattern;
		slices = pattern.getAllSlices();
		pop.clear();
		
		// Result state
		Trace slowest = null; // Result of attack
		boolean attack_finish = false; // State of attack
		
		// Designate factors of the Genetic Algorithm
		Initiator initiator = new GeneticInitiator(GeneticAttacker.MAX_POP_SIZE);
		Mutator mutator = new MultipleMutator(false, GeneticAttacker.MUT_POSSIBILITY);
		Crossover crossover = new RollCrossover(false, GeneticAttacker.CROSS_POSSIBILITY);
		
		Pumper pumper = new Pumper(GeneticAttacker.MAX_STR_LEN);
		
		// Init population
		int init_result = initiator.initiate(pop, jdkPattern, slices, prefixes);
		pop.print();
		System.err.println("===Initiate End===");
		System.err.println("Node Coverage: " + pop.coverage() + "/" + pattern.getAllNodes().size());
		
		// Init state check
		if (init_result == Initiator.INIT_ATTACK_FOUND) {
			System.err.println("find attack string when init");
			slowest = pop.get(pop.size() - 1);
			attack_finish = true;
		} else if (init_result == Initiator.INIT_FAILED) {
			System.err.println("failed when init");
			attack_finish = true;
		}
		
		// Start ga
		if (!attack_finish) {
			
			// Generation, MAX_GENERATIONS generations
			for (int trials = 0; trials < MAX_GENERATIONS; trials++) {
				
				// crossover
				int crossRes = crossover.cross(pop, pattern);
				if (crossRes == Crossover.CROSS_FAILED) {
					System.err.println("failed when cross");
					break;
				} else if (crossRes == Crossover.CROSS_ATTACK_FOUND) {
					System.err.println("find attack string when cross");
					slowest = pop.get(pop.size()-1);
					break;
				}

				// mutate
				int mutateRes = mutator.mutate(pop, pattern, slices);
				if (mutateRes == Mutator.MUTATE_FAILED) {
					System.err.println("failed when mutate");
					break;
				} else if (mutateRes == Mutator.MUTATE_ATTACK_FOUND) {
					System.err.println("find attack string when mutate");
					slowest = pop.get(pop.size()-1);
					break;
				}
				
				pop.cleanIncubating();
			}
		}
		
		// Output node coverage of the regex (state coverage of the automata)
		System.err.println("===Genetic Algorithm End===");
		System.err.println("Node Coverage: " + pop.coverage() + "/" + pattern.getAllNodes().size());
		
		// If attack failed: find the max-score trace as the result
		if (slowest == null) {
			for (int i = 0; i < pop.size(); i++) {
				if (slowest == null) slowest = pop.get(i);
				else slowest = slowest.score(null) < pop.get(i).score(null) ? pop.get(i) : slowest;
			}
		}
		
		// Must return a trace unless init population error
		if (slowest != null && slowest.attackSuccess()) {			
			slowest = pumper.reRepeat(pattern, slowest);
			System.err.println("Got 'em: <" + slowest.getAttackPrefix() + "> <" + slowest.getAttackPump() + "> <" + slowest.getAttackSuffix() + ">");
		} else System.err.println("Normal fail");
		
		return slowest;
	}
}
