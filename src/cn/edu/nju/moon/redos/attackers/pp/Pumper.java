package cn.edu.nju.moon.redos.attackers.pp;

import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.regex.ReScueMatcher;
import cn.edu.nju.moon.redos.regex.ReScuePattern;

public class Pumper {
	private int MAX_LEN = 128; // Max repeat times of the effective sub-string
	
	public Pumper(int max_len){
		this.MAX_LEN = max_len;
	}
	
	/**
    * Repeating the most effective sub-string of the attack string
    * @param p
    * @param t
    * @return
    */
    public Trace reRepeat(ReScuePattern p, Trace t) {
    	String reg = p.pattern();
//    	String atk = t.str;
    	System.out.println("Vulnerable: " + t.str);
    	
    	// Init
    	ReScuePattern vp = ReScuePattern.compile(reg);
    	ReScueMatcher vm = null;
    	int efi = 0, efj = 0;
    	double maxScore = t.score(null);
    	System.out.println(t.getMatchSteps() + " : " + t.score(null) + " : " + t.str);
    	
    	// Trimming
    	Trace fstResult = t;
    	String fstStr = t.str;
    	for (int i = fstStr.length() - 1; i > 0; i--) {
			String ns = "";
			if (i + 1 >= fstStr.length())
				ns = fstStr.substring(0, i - 1);
			else
				ns = fstStr.substring(0, i - 1) + fstStr.substring(i + 1);
			ReScueMatcher vmp = vp.matcher(ns, new Trace(RedosAttacker.FIRST_THRESHOLD));
			Trace tp = vmp.find();
			if (tp.attackSuccess()) {
				fstStr = tp.str;
				fstResult = tp;
			}
    	}
    	t = fstResult;
    	System.out.println(t.getMatchSteps() + " : " + t.score(null) + " : " + t.str);
    	
    	// Find the most effective sub-string by double-loop (1e6)
    	boolean found = false;
    	for (int i = 4; i <= t.effectiveStr.length(); i += 2) {  // Length of the sub-string (to be deleted)
    		for (int j = 0; j < t.effectiveStr.length(); j ++) { // Start index of the sub-string
    			if (j + i > t.effectiveStr.length()) break;
    			
    			String repeat = t.effectiveStr.substring(j, j + i);
    			String ns = t.str.substring(0, j) + repeat + t.str.substring(j);
    			vm = vp.matcher(ns, new Trace(RedosAttacker.SECOND_THRESHOLD));
    			Trace tp = vm.find();
    			if (tp.score(null) > maxScore) {
    				maxScore = tp.score(null);
    				efi = j;
    				efj = j + i;
    				if (tp.attackSuccess()) {
    					t = tp;
    					found = true;
    					break;
    				}
    			}
    		}
    		if (found) break;
    	}
    	System.out.println(t.getMatchSteps() + " : " + t.str);
    	
    	int pump_time = 0;
    	// If double-repeating cannot pass the Second Judge, then repeat MAX_PUMPING times
    	if (efj > 0) {
    		String nprefix = t.str.substring(0, efi);
    		String nsuffix = t.str.substring(efi);
    		
    		int cur_len = nprefix.length() + nsuffix.length();
    		
    		String npump = t.str.substring(efi, efj);
    		String npumps = "";
    		if (npump.length() > 0) {
	    		while (cur_len + npump.length() <= MAX_LEN) {
	    			npumps = npumps + npump;
	    			cur_len = cur_len + npump.length();
	    			pump_time ++;
	    		}
    		}
    		String ns = nprefix + npumps + nsuffix;
    		vm = vp.matcher(ns, new Trace(RedosAttacker.SECOND_THRESHOLD));
			Trace tp = vm.find();
			if (tp.attackSuccess()) {
				t = tp;
			}
    	}
    	System.out.println(t.getMatchSteps() + " : " + pump_time + " : " + t.str);

    	// Can we really attack ? (1e8)
    	vm = vp.matcher(t.str, new Trace(RedosAttacker.SUCCESS_THRESHOLD));
    	t = vm.find();
    	System.out.println(t.getMatchSteps() + " : " + t.str);
    	return t;
    }
}
