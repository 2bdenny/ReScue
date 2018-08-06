package cn.edu.nju.moon.redos.attackers.ga.initiators;

import java.util.ArrayList;
import java.util.List;
import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Crossover;
import cn.edu.nju.moon.redos.attackers.ga.Initiator;
import cn.edu.nju.moon.redos.attackers.ga.Mutator;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.attackers.ga.crossovers.RollCrossover;
import cn.edu.nju.moon.redos.attackers.ga.mutators.MultipleMutator;
import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * Initiate seeds by genetic algorithm
 */
public class GeneticInitiator implements Initiator {
	private int INIT_MAX_TRIALS = 500; // Maximum generations of initiating seeds of the seeds
	
	/**
	 * We can modify the max trials, the expect init node coverage and the threshold of early exit in init
	 * by this constructor
	 * @param init_max_trial The max trial of init, default if 500
	 * @param init_node_coverage The expect node coverage of init, default is 0.8
	 * @param init_threshold The early exit threshold of init, default is {@link RedosAttacker.FIRST_THRESHOLD}
	 */
	public GeneticInitiator(int init_max_trial){
		this.INIT_MAX_TRIALS = init_max_trial;
	}
	
	/**
	 * Default constructor
	 */
//	public GeneticInitiator(){}

	@Override
	public int initiate(Population pop, ReScuePattern pattern, List<String> slices, List<String> prefixes) {
		// Main initiator
		Initiator init_initiator = new StrikeRandomInitiator(true);
		if (prefixes == null) prefixes = new ArrayList<String>(); // Store all feasible prefix
		
		int ie_result = init_initiator.initiate(pop, pattern, slices, prefixes);
		if (ie_result == Initiator.INIT_ATTACK_FOUND) return Initiator.INIT_ATTACK_FOUND; // Find attack string when init the seeds of the seeds
		
		// If general initiate seed of seed failed, try random initiate
		Initiator assist_initiator = new BFSInitiator(true);
		ie_result = assist_initiator.initiate(pop, pattern, slices, prefixes);
		if (ie_result == Initiator.INIT_ATTACK_FOUND) return Initiator.INIT_ATTACK_FOUND; // Find attack string when init the seeds of the seeds
		if (ie_result == Initiator.INIT_FAILED) return Initiator.INIT_FAILED; // Initiate seeds of seeds error
		
		// Other factors
		Crossover crosser = new RollCrossover(true, 0.1);
		Mutator mutater = new MultipleMutator(true, 0.1);
		
		int trials = 0;
		while (trials < INIT_MAX_TRIALS) {
			trials++;
			
			// If all nodes covered
			if (pattern.getAllNodes().size() <= pop.allNodes().size()) return Initiator.INIT_SUCCESS;
						
			int crossRes = crosser.cross(pop, pattern);
			if (crossRes == Crossover.CROSS_ATTACK_FOUND) {
				return Initiator.INIT_ATTACK_FOUND;
			}
			
			int mutRes = mutater.mutate(pop, pattern, slices);
			if (mutRes == Mutator.MUTATE_ATTACK_FOUND) {
				return Initiator.INIT_ATTACK_FOUND;
			}
			
			pop.cleanSeeding();
			
			if (pop.size() == 0) break;
		}
		
		if (pop.size() > 0)
			return Initiator.INIT_SUCCESS;
		else
			return Initiator.INIT_FAILED;
	}
}
