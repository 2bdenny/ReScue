package cn.edu.nju.moon.redos;

import cn.edu.nju.moon.redos.regex.ReScuePattern;

abstract public class RedosAttacker {
	public static final double SUCCESS_THRESHOLD = 1e8;
	
	public static final double FIRST_THRESHOLD = 1e5;
	public static final double SECOND_THRESHOLD = 1e6;
//	public static final double THIRD_THRESHOLD = 1e7;
	
	/**
	 * Try to attack a regex.
	 * @param compiled regex
	 * @return the trace of match process
	 */
	abstract public Trace attack(ReScuePattern jdkPattern);
}
