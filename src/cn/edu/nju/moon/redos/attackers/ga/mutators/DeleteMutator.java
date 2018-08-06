package cn.edu.nju.moon.redos.attackers.ga.mutators;

import java.util.List;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Mutator;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * Mutate by randomly delete a substring from the original string
 */
public class DeleteMutator implements Mutator {
	private boolean isSeeding = false;
	
	public DeleteMutator(boolean isSeeding) {
		this.isSeeding = isSeeding;
	}
	
	@Override
	public Trace multiMutate(Trace c, ReScuePattern pattern, List<String> slices, Population pop, String alpha) {
		for (int i = 0; i < c.effectiveStr.length(); i ++) {
			for (int j = i+1; j < c.effectiveStr.length(); j ++) {
				String seq = c.effectiveStr.substring(0, i) + c.effectiveStr.substring(j, c.effectiveStr.length());
				ReScueMatcher m = pattern.matcher(seq, new Trace(GeneticAttacker.FIRST_THRESHOLD));
				Trace t = m.find();
				
				if (isSeeding) {
					if (t.score(pop) > 0) return t;
				} else {
					if (t.score(null) > c.score(null)) return t;
				}
			}
		}
		return c;
	}

	@Override
	public int mutate(Population pop, ReScuePattern pattern, List<String> slices) {
		return Mutator.MUTATE_SUCCESS;
	}

}
