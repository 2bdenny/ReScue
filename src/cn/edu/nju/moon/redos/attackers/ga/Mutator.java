package cn.edu.nju.moon.redos.attackers.ga;

import java.util.List;
import java.util.Random;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * The Mutation factor of genetic programming
 */
public interface Mutator {	
	// Some signal constant
	public static final int MUTATE_ATTACK_FOUND = 0; // Find the attack string when mutate (First Success)
	public static final int MUTATE_SUCCESS = 1; // Mutation success
	public static final int MUTATE_FAILED = 2; // Mutation success
	public static Random rnd = new Random();
	
	public int mutate(Population pop, ReScuePattern pattern, List<String> slices);
	public Trace multiMutate(Trace c, ReScuePattern pattern, List<String> slices, Population pop, String alpha);
}
