package cn.edu.nju.moon.redos.attackers.ga.mutators;

import java.util.LinkedList;
import java.util.List;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.ga.Mutator;
import cn.edu.nju.moon.redos.attackers.ga.Population;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.utils.StringUtils;

/**
 * Merged mutator
 */
public class MultipleMutator implements Mutator{	
	public static final String[] alphas = {
			StringUtils.lower, 
			StringUtils.upper, 
			StringUtils.digit, 
			StringUtils.punct,
			StringUtils.hex, 
			StringUtils.alpha, 
			StringUtils.graph, 
			StringUtils.word, 
			StringUtils.visibleChars, 
			StringUtils.escapeChars, 
			StringUtils.spaceChars,
			StringUtils.allChars };
	private int[] alphaUtils = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
	private int currentAlpha = -1;
	
	private Mutator[] mutators = null;
	private boolean isSeeding = false;
	
	private double MUT_POSSIBILITY = 0.1;
	
	public MultipleMutator(boolean isSeeding, double MUT){
		this.isSeeding = isSeeding;
		this.MUT_POSSIBILITY = MUT;
		this.mutators = new Mutator[]{
				new AppendMutator(isSeeding), 
				new InsertMutator(isSeeding), 
				new DeleteMutator(isSeeding), 
				new RepeatMutator(isSeeding), 
				new ReverseMutator(isSeeding)};
	}
	
	@Override
	public int mutate(Population pop, ReScuePattern pattern, List<String> slices) {
		LinkedList<Trace> backup = new LinkedList<Trace>();
		
		// For each string, has MUT_POSSIBILITY to mutate
		for (int traceIndex = 0; traceIndex < pop.size(); traceIndex ++) {
			if (rnd.nextDouble() > MUT_POSSIBILITY) continue;
			
			Trace c = pop.get(traceIndex);
			if (c.effectiveStr.length() == 0) continue;
			
			Trace mutated = c;
			
			// Do mutate
			// select an operator
			int mutateIndex = rnd.nextInt(mutators.length);
			if (mutateIndex <= 1) {
				
				// select an alphabet
				int utilSum = 0;
				for (int u : alphaUtils) utilSum += u;
				
				int utilRnd = rnd.nextInt(utilSum);
				int tmpSum = 0;
				for (int i = 0; i < alphaUtils.length; i++) {
					if (tmpSum <= utilRnd && utilRnd < tmpSum + alphaUtils[i]) {
						currentAlpha = i;
						break;
					}
					tmpSum += alphaUtils[i];
				}
				
				// the selected alphabet
				String inputAlpha = currentAlpha == -1 ? alphas[11] : alphas[currentAlpha];
				if (rnd.nextInt(5) < 1) inputAlpha += pattern.pattern();
				
				// The mutated
				mutated = mutators[mutateIndex].multiMutate(c, pattern, slices, pop, inputAlpha);
			} else {
				currentAlpha = -1;
				mutated = mutators[mutateIndex].multiMutate(c, pattern, slices, pop, null);
			}
			
			// back up all mutated strings
			if (mutated == null) return Mutator.MUTATE_FAILED;
			else if (mutated.attackSuccess()) {
				pop.add(mutated);
				return Mutator.MUTATE_ATTACK_FOUND;
			} else if (mutated != c) {
				if (isSeeding) {
					if (mutated.score(pop) > c.score(pop)) addUtils();
				} else {
					if (mutated.score(null) > c.score(null)) addUtils();
				}
				backup.add(mutated);
			}
		}
		// add to population
		for (Trace mutated : backup) {
			if (mutated == null) return Mutator.MUTATE_FAILED;
			else {
				pop.add(mutated);
				if (mutated.attackSuccess()) return Mutator.MUTATE_ATTACK_FOUND;
			}
		}
		
		return Mutator.MUTATE_SUCCESS;
	}
	
	public void addUtils(){
		if (currentAlpha != -1) alphaUtils[currentAlpha]++;
	}

	@Override
	public Trace multiMutate(Trace c, ReScuePattern pattern, List<String> slices, Population pop, String alpha) {
		return null;
	}
}
