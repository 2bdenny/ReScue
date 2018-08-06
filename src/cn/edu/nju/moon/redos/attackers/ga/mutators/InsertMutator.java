package cn.edu.nju.moon.redos.attackers.ga.mutators;

import java.util.List;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Mutator;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.utils.StringUtils;

/**
 * Mutate by randomly insert a string
 */
public class InsertMutator implements Mutator{
	private static final int MAX_MUTATE_INSERT = 10;
	private boolean isSeeding = false;
	
	public InsertMutator(boolean isSeeding){
		this.isSeeding = isSeeding;
	}

	@Override
	public int mutate(Population pop, ReScuePattern pattern, List<String> slices) {
		return Mutator.MUTATE_SUCCESS;
	}
	
	@Override
	public Trace multiMutate(Trace c, ReScuePattern pattern, List<String> slices, Population pop, String alpha) {
		int seq_len = GeneticAttacker.MAX_STR_LEN - c.effectiveStr.length();
		seq_len = seq_len > 0 ? seq_len : 10;
		for (int i = 0; i < MAX_MUTATE_INSERT; i++) {
			// Generate a random string from alphabet
			String tmp = StringUtils.randomStringFromAlphabet(rnd.nextInt(seq_len), alpha);
			int point = rnd.nextInt(c.effectiveStr.length());
			String ns = c.effectiveStr.substring(0, point) + tmp
					+ c.effectiveStr.substring(point, c.effectiveStr.length());
			ReScueMatcher m = pattern.matcher(ns, new Trace(GeneticAttacker.FIRST_THRESHOLD));
			Trace t = m.find();
			if (t.attackSuccess())
				return t;
			if (!t.tooLong() && t.effectiveStr.length() > 0) {
				if (isSeeding) {
					if (t.score(pop) > 0) return t;
				} else {
					if (t.effectiveStr.length() > c.effectiveStr.length() || t.score(null) > c.score(null)) return t;
				}
			}

			// Generate a random string from slices
			tmp = slices.size() > 0 ? slices.get(rnd.nextInt(slices.size())) : "";
			point = rnd.nextInt(c.effectiveStr.length());
			ns = c.effectiveStr.substring(0, point) + tmp
					+ c.effectiveStr.substring(point, c.effectiveStr.length());
			m = pattern.matcher(ns, new Trace(GeneticAttacker.FIRST_THRESHOLD));
			t = m.find();
			if (t.attackSuccess())
				return t;
			if (!t.tooLong() && t.effectiveStr.length() > 0) {
				if (isSeeding) {
					if (t.score(pop) > 0) return t;
				} else {
					if (t.effectiveStr.length() > c.effectiveStr.length() || t.score(null) > c.score(null)) return t;
				}
			}
		}
		return c;
	}
}
