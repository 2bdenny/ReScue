package cn.edu.nju.moon.redos.attackers.pp;

import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;

public class Pumper {
  // NB This prevents the discovery of non-exponential behavior.
  // For example, quadratic regexes like /a+a+$/ blow up after ~50K pumps.
	private int MAX_LEN = 128; // Max repeat times of the effective sub-string

	public Pumper(int max_len){
		this.MAX_LEN = max_len;
	}
	
	/**
    * Repeating the most effective sub-string of the attack string
    * @param p
    * @param t
    * @return Trace (with the attackString features set)
    */
    public Trace reRepeat(ReScuePattern p, Trace t) {
    	String reg = p.pattern();
    	System.err.println("reRepeat: un-trimmed trace string: " + t.str);
    	
    	// Init
    	ReScuePattern vp = ReScuePattern.compile(reg);
    	ReScueMatcher vm = null;
    	int efi = 0, efj = 0;
    	double maxScore = t.score(null);
    	System.err.println(t.getMatchSteps() + " : " + t.score(null) + " : " + t.str);
    	
    	// Trimming
    	Trace fstResult = t;
    	String fstStr = t.str;
    	for (int i = fstStr.length() - 1; i > 0; i--) {
			String attackString = "";
			if (i + 1 >= fstStr.length())
				attackString = fstStr.substring(0, i - 1);
			else
				attackString = fstStr.substring(0, i - 1) + fstStr.substring(i + 1);
			ReScueMatcher vmp = vp.matcher(attackString, new Trace(RedosAttacker.FIRST_THRESHOLD));
			Trace tp = vmp.find();
			if (tp.attackSuccess()) {
				fstStr = tp.str;
				fstResult = tp;
			}
    	}
    	t = fstResult;
    	System.err.println("Trimmed attack string: " + t.getMatchSteps() + " steps (score " + t.score(null) + ")\n  " + t.str);
    	
    	// Find the most effective sub-string by double-loop (1e6)
    	boolean found = false;
    	for (int i = 4; i <= t.effectiveStr.length(); i += 2) {  // Length of the sub-string (to be deleted)
    		for (int j = 0; j < t.effectiveStr.length(); j ++) { // Start index of the sub-string
    			if (j + i > t.effectiveStr.length()) break;
    			
    			String prefix = t.str.substring(0, j);
    			String repeat = t.effectiveStr.substring(j, j + i);
    			String suffix = t.str.substring(j);

    			String attackString = prefix + repeat + suffix;
    			vm = vp.matcher(attackString, new Trace(RedosAttacker.SECOND_THRESHOLD));
    			Trace tp = vm.find();
    			if (tp.score(null) > maxScore) {
    				maxScore = tp.score(null);
    				efi = j;
    				efj = j + i;
    				if (tp.attackSuccess()) {
              System.err.println("Found most effective sub-string by double-loop");
    					t = tp;
    					t.setAttackString(prefix, repeat, suffix);
    					found = true;
    					break;
    				}
    			}
    		}
    		if (found) break;
    	}
    	System.err.println(t.getMatchSteps() + " : " + t.str);
    	
    	int nPumps = 0;
    	// If double-repeating cannot pass the Second Judge, then repeat MAX_LEN times
      // JD: TODO Not sure what the "Second Judge" is.
      //     It looks like we are pumping the effective substring found in the preceding double-loop?
      if (efj > 0) {
        String prefix = t.str.substring(0, efi);
        String suffix = t.str.substring(efi);

        int cur_len = prefix.length() + suffix.length();

        String pumpKernel = t.str.substring(efi, efj);
        String repeatedPump = "";
        if (pumpKernel.length() > 0) {
          // Pump until adding another would exceed MAX_LEN
          while (cur_len + pumpKernel.length() <= MAX_LEN) {
            repeatedPump += pumpKernel;
            cur_len = prefix.length() + repeatedPump.length() + suffix.length();
            nPumps++;
          }
        }
        String attackString = prefix + repeatedPump + suffix;
        vm = vp.matcher(attackString, new Trace(RedosAttacker.SECOND_THRESHOLD));
        Trace tp = vm.find();
        if (tp.attackSuccess()) {
          System.err.println("Found effective attack string by many-pump\n  prefix " + prefix + " pump " + pumpKernel + " suffix " + suffix + "\n  attackString: " + attackString);
          t = tp;
          t.setAttackString(prefix, pumpKernel, suffix);
        }
      }
    	System.err.println(t.getMatchSteps() + " : " + nPumps + " : " + t.str);

    	// Can we really attack ? (1e8)
    	vm = vp.matcher(t.str, new Trace(RedosAttacker.SUCCESS_THRESHOLD));
    	Trace final_t = vm.find();
      final_t.setAttackString(t.getAttackPrefix(), t.getAttackPump(), t.getAttackSuffix());
    	System.err.println("Attempted attack: " + t.getMatchSteps() + " steps : " + t.str);
    	return final_t;
    }
}
