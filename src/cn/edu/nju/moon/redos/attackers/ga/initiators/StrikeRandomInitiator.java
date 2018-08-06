package cn.edu.nju.moon.redos.attackers.ga.initiators;

import java.util.ArrayList;
import java.util.List;

import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.ga.Initiator;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.utils.StringUtils;

/**
 * Initiate seeds by random string, make node coverage as high as possible
 */
public class StrikeRandomInitiator implements Initiator {
	private double INIT_THRESHOLD = RedosAttacker.FIRST_THRESHOLD;
	private int INIT_MAX_TRIALS = 500; // Maximum trials of init, default is 500
	private boolean isSeeding = false;
	
	public StrikeRandomInitiator(boolean isSeeding){
		this.isSeeding = isSeeding;
	}
	
	public StrikeRandomInitiator(int init_max_trials, double init_threshold){
		this.INIT_MAX_TRIALS = init_max_trials;
		this.INIT_THRESHOLD = init_threshold;
	}

	@Override
	public int initiate(Population pop, ReScuePattern pattern, List<String> slices, List<String> prefixes) {
		if (prefixes == null) prefixes = new ArrayList<String>();
		String characters = Initiator.alls;
		
		// Generate all seed of seed by slices
		for (String s : slices) {
			String prefix = prefixes.size() == 0 ? "" : prefixes.get(rnd.nextInt(prefixes.size()));
			ReScueMatcher m = pattern.matcher(prefix + s, new Trace(INIT_THRESHOLD));
			Trace c = m.find();
			if (c.attackSuccess()) {
				pop.add(c);
				return Initiator.INIT_ATTACK_FOUND;
			}
			if (c.score(pop) > 0) {
				pop.add(c);
				if (!prefixes.contains(c.effectiveStr))
					prefixes.add(c.effectiveStr);
			}
		}

		// Generate all seed of seed by alphabet
		for (int i = 0; i < characters.length(); i++) {
			String prefix = prefixes.size() == 0 ? "" : prefixes.get(rnd.nextInt(prefixes.size()));
			String s = prefix + new String("" + characters.charAt(i));
			ReScueMatcher m = pattern.matcher(s, new Trace(INIT_THRESHOLD));
			Trace c = m.find();
			if (c.attackSuccess()) {
				pop.add(c);
				return Initiator.INIT_ATTACK_FOUND;
			}
			if (c.score(pop) > 0) {
				pop.add(c);
				if (!prefixes.contains(c.effectiveStr))
					prefixes.add(c.effectiveStr);
			}
		}
		
		/* *
		 * About the initiation strategy
		 * 1. Pop number may less than POP_SIZE (Max trials is set to 1e4 to avoid dead at initiation)
		 * 2. Generate a random string (Generate a length = 100 by random alphabet & slices, then add prefix from feasible prefixes)
		 * 3. One string can contain many identical slices according to the engine transmission and bump-along
		 * 4. Optimize above slices
		 * 5. Find the max score slice and add to the pop
		 * */
		int trials = 0;
		while (trials < INIT_MAX_TRIALS) {
			trials ++;
			
			String randstr = StringUtils.randomStringFromAlphabet(GeneticAttacker.INIT_STR_LEN, StringUtils.allChars);
			randstr = StringUtils.randomInsertSlice(randstr, slices);
			String pfx = "";
			String str = pfx + randstr;
			
			ReScueMatcher matcher = pattern.matcher(str, new Trace(INIT_THRESHOLD));
			Trace t = matcher.find();
			
			// If find attack string
			if (t.attackSuccess()) {
				pop.add(t);
				return Initiator.INIT_ATTACK_FOUND;
			}
			
			if (t.effectiveStr.length() <= 0 || t.tooLong()) continue;
			if (isSeeding) {
				if (t.score(pop) > 0) {
					pop.add(t);
					String prefix = t.effectiveStr;
					if (!prefixes.contains(prefix)) prefixes.add(prefix);
				}
			} else if (!pop.contains(t) && !pop.isContainAllNodesof(t)) {
				pop.add(t);
				String prefix = t.effectiveStr;
				if (!prefixes.contains(prefix)) prefixes.add(prefix);
			}
		}
		if (pop.size() > 0) return Initiator.INIT_SUCCESS;
		else return Initiator.INIT_FAILED;
	}

}
