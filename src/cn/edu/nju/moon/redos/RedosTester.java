package cn.edu.nju.moon.redos;

import java.util.Scanner;
import java.util.regex.PatternSyntaxException;
import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
import cn.edu.nju.moon.redos.attackers.*;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.utils.RegexFormatter;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import com.google.gson.Gson; // For --regexFile

public class RedosTester {
	protected RedosAttacker atk;
	protected String regex;
	
	public static void testView() {
		Scanner input = new Scanner(System.in);
		System.out.print("Input regex: ");
		String regex = input.hasNextLine() ? input.nextLine() : null;
		input.close();
		
//		^(([a-zA-Z]:)|(\\{2}\w+)\$?)\2(\\(\w[\w ]*.*))+\.((html|HTML)|(htm|HTM))$
		ReScuePattern p  = ReScuePattern.compile(regex);
		p.paintRegex();
	}
	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor("ReScue").build();
		parser.defaultHelp(true);
		parser.description("ReScue is a tool to auto detect ReDoS vulnerabilities in regexes. (noSeeding, noIncubating and noPumping are mutex arguments, and only used for testing)");
		parser.addArgument("-q", "--quiet").action(Arguments.storeTrue()).help("Quiet mode, hide input tips.");
		parser.addArgument("-v", "--visual").action(Arguments.storeTrue()).help("Show e-NFA of the input regex.");
		parser.addArgument("-f", "--regexFile").setDefault("NONE").help("File containing JSON object with key: regex");
		parser.addArgument("-ml", "--maxLength").setDefault(128).help("Maximum string length.");
		parser.addArgument("-pz", "--popSize").setDefault(200).help("Maximum population size.");
		parser.addArgument("-g", "--generation").setDefault(200).help("Maximum generations.");
		parser.addArgument("-cp", "--crossPossibility").setDefault("10").help("The crossover possibility, default is 10, means 10%.");
		parser.addArgument("-mp", "--mutatePossibility").setDefault("10").help("The mutation possibility, default is 10, means 10%.");
		
		parser.addArgument("-ns", "--noSeeding").action(Arguments.storeTrue()).help("No seeding version of ReScue, only used for evaluation & testing.");
		parser.addArgument("-ni", "--noIncubating").action(Arguments.storeTrue()).help("No incubating version of ReScue, only used for evaluation & testing.");
		parser.addArgument("-np", "--noPumping").action(Arguments.storeTrue()).help("No pumping version of ReScue, only used for evaluation & testing.");
		
		if (args.length == 0) {
			parser.printHelp();
			return ;
		}
		
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
			System.out.println(ns.getBoolean("unittest"));
		} catch (ArgumentParserException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
		
		if (ns.getBoolean("visual")){
			RedosTester.testView();
		} else {
			// These values are used for test
			int sl = 64; // The deprecated seed length
			int ml = 128;
			int pz = 200;
			int g = 200;
			int mp = 10;
			int cp = 5;
			String rf = "NONE";
			
			// Read input arguments
			boolean quiet = ns.getBoolean("quiet");
			sl = ns.getInt("maxLength");
			ml = ns.getInt("maxLength");
			pz = ns.getInt("popSize");
			g = ns.getInt("generation");
			cp = ns.getInt("crossPossibility");
			mp = ns.getInt("mutatePossibility");
			rf = ns.getString("regexFile");

			String regex = getRegex(rf, quiet);
			if (regex == null || regex.length() < 1) {
				System.out.println("Please check your regex.");
				return ;
			}
			
			RedosAttacker atk = null;
			if (ns.getBoolean("noSeeding")) atk = new GeneticAttackerWithoutSeeding(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
			else if (ns.getBoolean("noIncubating")) atk = new GeneticAttackerWithoutIncubating(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
			else if (ns.getBoolean("noPumping")) atk = new GeneticAttackerWithoutPumping(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
			else atk = new GeneticAttacker(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
			
			RedosTester rt = new RedosTester(atk, regex);
			rt.attackAndPrint();
		}
	}
	
	public RedosTester(RedosAttacker atk, String regex) {
		this.atk = atk;
		this.regex = regex;
	}
	
	/**
	 * The default attack and print function,
	 * You can print as you like by override this function.
	 */
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

  private static String getRegex(String regexFile, boolean quiet) {
    String regex = null;
    if (regexFile == null || regexFile == "NONE") {
      // No regexFile, get it from prompt
      if (!quiet) System.out.print("Input regex: ");

      Scanner input = new Scanner(System.in);
      regex = input.hasNextLine() ? input.nextLine() : null;
      input.close();
    } else {
      // regexFile: load, parse, extract
      // TODO
    }
    return regex;
  }
}
