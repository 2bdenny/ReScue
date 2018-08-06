package cn.edu.nju.moon.redos.attackers.ga.crossovers;

import java.util.Random;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Crossover;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * Random Crossover
 */
public class RollCrossover implements Crossover{	
	private static Random rnd = new Random();
//	private boolean isSeeding = false;
	private double CROSS_POSSIBILITY = 0.05;
	
	public RollCrossover(boolean isSeeding, double CrossPossibility) {
//		this.isSeeding = isSeeding;
		this.CROSS_POSSIBILITY = CrossPossibility;
	}
	
	@Override
	public int cross(Population pop, ReScuePattern pattern) {
		for (int i = 0; i < pop.size(); i++){
			if (rnd.nextDouble() > this.CROSS_POSSIBILITY) continue;
		
			// Parents
			Trace ca = pop.getByRoll(rnd.nextInt(pop.rollSum()) + 1);
			Trace cb = pop.getByRoll(rnd.nextInt(pop.rollSum()) + 1);
	
			// Get parents
			int pa = rnd.nextInt(ca.effectiveStr.length());
			int pb = rnd.nextInt(cb.effectiveStr.length());
			
			// Generate children str
			String sa = ca.effectiveStr.substring(0, pa)
					+ cb.effectiveStr.substring(pb, cb.effectiveStr.length());
			String sb = cb.effectiveStr.substring(0, pb)
					+ ca.effectiveStr.substring(pa, ca.effectiveStr.length());
			
			// Only length > 0 strings will be checked
			if (sa.length() > 0) {
				ReScueMatcher ma = pattern.matcher(sa, new Trace(GeneticAttacker.FIRST_THRESHOLD));
				Trace ta = ma.find();
				
				// Effective string's length should > 0
				if (ta.effectiveStr.length() > 0) {				
					if (ta.attackSuccess()) {
						pop.add(ta);
						return Crossover.CROSS_ATTACK_FOUND;
					}
					// And the string should be short enough
					if (!ta.tooLong()) {
						pop.add(ta);
					}
				}
			}
			
			if (sb.length() > 0) {
				ReScueMatcher mb = pattern.matcher(sb, new Trace(GeneticAttacker.FIRST_THRESHOLD));
				Trace tb = mb.find();
				if (tb.effectiveStr.length() > 0) {
					if (tb.attackSuccess()) {
						pop.add(tb);
						return Crossover.CROSS_ATTACK_FOUND;
					}
					if (!tb.tooLong()){
						pop.add(tb);
					}
				}
			}
		}
		
		return Crossover.CROSS_SUCCESS;
	}
}
