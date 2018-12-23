package cn.edu.nju.moon.redos.tester;

import java.util.regex.PatternSyntaxException;
import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.RedosTester;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.utils.RegexFormatter;

public class MyTester extends RedosTester{
	public MyTester(RedosAttacker atk, String regex) {
		super(atk, regex);
	}

	@Override
	public void attackAndPrint() {
		long start_time = System.nanoTime();
		try {
//			ReScuePattern p = RegexFormatter.formatRegex(regex);
			ReScuePattern p = ReScuePattern.compile(regex);
			System.out.println(RegexFormatter.deleteFlag(regex));
			
			Trace trace = atk.attack(p);
			if (trace != null && trace.attackSuccess()) {
				long elapsed_time = System.nanoTime() - start_time;
				System.out.println("TIME: " + ((double)elapsed_time / 1e9) + " (s)");
				System.out.println("Attack success, attack string is:");
				System.out.println(trace.str);
			} else {
				long elapsed_time = System.nanoTime() - start_time;
				System.out.println("TIME: " + ((double)elapsed_time / 1e9) + " (s)");
				System.out.println("Attack failed");
			}
		} catch (PatternSyntaxException e) {
			System.out.println(RegexFormatter.deleteFlag(regex));
			long elapsed_time = System.nanoTime() - start_time;
			System.out.println("TIME: " + ((double)elapsed_time / 1e9) + " (s)");
			System.out.println("Regex compile error");
		}
	}
}
