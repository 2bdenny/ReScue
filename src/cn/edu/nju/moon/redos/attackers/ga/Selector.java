package cn.edu.nju.moon.redos.attackers.ga;

/**
 * The Selector factor of genetic programming
 */
public interface Selector {
	public static final int SEL_FAILED = 0;
	public static final int SEL_SUCCESS = 1;
	
	public int select(Population pop);
}
