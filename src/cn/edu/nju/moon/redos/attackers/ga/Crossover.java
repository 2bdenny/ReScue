package cn.edu.nju.moon.redos.attackers.ga;

import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * The crossover factor of genetic programming
 */
public interface Crossover {
	// Some signal constants
	public static final int CROSS_ATTACK_FOUND = 0; // Find the attack string when cross (First Judge)
	public static final int CROSS_FAILED = 1; // Failed when cross (cannot generate enough chromosomes)
	public static final int CROSS_SUCCESS = 2; // Crossover successfully
	
	public int cross(Population pop, ReScuePattern pattern);
}
