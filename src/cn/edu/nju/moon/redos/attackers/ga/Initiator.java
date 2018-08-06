package cn.edu.nju.moon.redos.attackers.ga;

import java.util.List;
import java.util.Random;

import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * The initiator factor of genetic programming
 */
public interface Initiator {
	static final String lowers = "abcdefghijklmnopqrstuvwxyz";
	static final String uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final String numbers = "0123456789";
	static final String visibles = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
	static final String escapes = "\t\n\b\r";
	static final String spaces = " \t\r\n";
	static final String alls = lowers + uppers + numbers + visibles + escapes + spaces;
	
	// Some signal constants
	public static final int INIT_POP_SIZE = 200; // Population size of seeds
	public static final int INIT_ATTACK_FOUND = 1;
	public static final int INIT_SUCCESS = -1; // Initiate population success
	public static final int INIT_FAILED = -2; // Initiate population failed
	public static Random rnd = new Random();
	
	public int initiate(Population pop, ReScuePattern pattern, List<String> slices, List<String> prefixes);
}
