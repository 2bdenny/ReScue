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
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

// For --regexFile
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class InputRegex {
  private String regex;

  InputRegex(String regex) {
    this.regex = regex;
  }

  public String getRegex() {
    return regex;
  }
}

class AttackResult {
  class PumpPair {
    String prefix;
    String pump;

    PumpPair(String prefix, String pump) {
      this.prefix = prefix;
      this.pump = pump;
    }
  }

  class EvilInput {
    PumpPair pumpPairs[];
    String suffix;

    EvilInput(PumpPair pumpPairs[], String suffix) {
      this.pumpPairs = pumpPairs;
      this.suffix = suffix;
    }
  }

  private String regex;
  private boolean canAnalyze;
  private boolean isSafe;
  private EvilInput evilInputs[];

  AttackResult(String regex, boolean canAnalyze, boolean isSafe, Trace trace) {
    this.regex = regex;
    this.canAnalyze = canAnalyze;
    this.isSafe = isSafe;

    if (isSafe) {
      // No evilInputs -- empty array
      this.evilInputs = new EvilInput[0];
    } else {
      // Build an EvilInput[] with one element
      PumpPair _pumpPairs[] = new PumpPair[1];
      _pumpPairs[0] = new PumpPair(trace.getAttackPrefix(), trace.getAttackPump());
      this.evilInputs = new EvilInput[1];
      this.evilInputs[0] = new EvilInput(_pumpPairs, trace.getAttackSuffix());
    }
  }
}

public class RedosTester {
	protected RedosAttacker atk;
	protected String regex;
	
	public static void testView() {
		Scanner input = new Scanner(System.in);
		System.out.print("Input regex: ");
		String regex = input.hasNextLine() ? input.nextLine() : null;
		input.close();
		
//		^(([a-zA-Z]:)|(\\{2}\w+)\$?)\2(\\(\w[\w ]*.*))+\.((html|HTML)|(htm|HTM))$
		ReScuePattern p = ReScuePattern.compile(regex);
		p.paintRegex();
	}
	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor("ReScue").build();
		parser.defaultHelp(true);
		parser.description("ReScue is a tool to auto detect ReDoS vulnerabilities in regexes. (noSeeding, noIncubating and noPumping are mutex arguments, and only used for testing)");
		parser.addArgument("-q", "--quiet").action(Arguments.storeTrue()).help("Quiet mode, hide input tips.");
		parser.addArgument("-v", "--visual").action(Arguments.storeTrue()).help("Show e-NFA of the input regex.");
		parser.addArgument("-r", "--regex").type(String.class).setDefault("NONE").help("Regex to test. Enclose with single-quotes, e.g. '(a+)+$'");
		parser.addArgument("-f", "--regexFile").type(String.class).setDefault("NONE").help("File containing JSON object with key: regex");
		parser.addArgument("-ml", "--maxLength").type(Integer.class).setDefault(128).help("Maximum string length.");
		parser.addArgument("-pz", "--popSize").type(Integer.class).setDefault(200).help("Maximum population size.");
		parser.addArgument("-g", "--generation").type(Integer.class).setDefault(200).help("Maximum generations.");
		parser.addArgument("-cp", "--crossPossibility").type(Integer.class).setDefault(10).help("The crossover possibility, default is 10, means 10%.");
		parser.addArgument("-mp", "--mutatePossibility").type(Integer.class).setDefault(10).help("The mutation possibility, default is 10, means 10%.");
		
		parser.addArgument("-ns", "--noSeeding").action(Arguments.storeTrue()).help("No seeding version of ReScue, only used for evaluation & testing.");
		parser.addArgument("-ni", "--noIncubating").action(Arguments.storeTrue()).help("No incubating version of ReScue, only used for evaluation & testing.");
		parser.addArgument("-np", "--noPumping").action(Arguments.storeTrue()).help("No pumping version of ReScue, only used for evaluation & testing.");
		
    // TODO: "{ --interactive | --regex R | --regexFile F }"
		if (args.length == 0) {
			parser.printHelp();
			return;
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
      String r = "NONE";
			String rf = "NONE";
			
			// Read input arguments
			boolean quiet = ns.getBoolean("quiet");
			sl = ns.getInt("maxLength");
			ml = ns.getInt("maxLength");
			pz = ns.getInt("popSize");
			g = ns.getInt("generation");
			cp = ns.getInt("crossPossibility");
			mp = ns.getInt("mutatePossibility");
			r = ns.getString("regex");
			rf = ns.getString("regexFile");

			InputRegex inputRegex = getRegex(r, rf, quiet);
      String regex = inputRegex.getRegex();
			if (regex == null || regex.length() < 1) {
				System.out.println("Please check your regex.");
				return;
			}
      System.out.println("Regex: <" + regex + ">");
			
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
      AttackResult ar = null;
			if (trace != null && trace.attackSuccess()) {
				long elapsed_time = System.nanoTime() - start_time;
				System.out.println("TIME: " + ((double)elapsed_time / 1e9) + " (s)");
				System.out.println("Attack success, attack string is:");
				System.out.println(trace.str);

        ar = new AttackResult(regex, true, false, trace);
			} else {
				long elapsed_time = System.nanoTime() - start_time;
				System.out.println("TIME: " + ((double)elapsed_time / 1e9) + " (s)");
				System.out.println("Attack failed");
        ar = new AttackResult(regex, true, true, trace);
			}
      Gson gson = new Gson();
      System.out.println("AttackResult: " + gson.toJson(ar));

		} catch (PatternSyntaxException e) {
			System.out.println(RegexFormatter.deleteFlag(regex));
			long elapsed_time = System.nanoTime() - start_time;
			System.out.println("TIME: " + ((double)elapsed_time / 1e9) + " (s)");
			System.out.println("Regex compile error");

      AttackResult ar = new AttackResult(regex, false, true, null);
      Gson gson = new Gson();
      System.out.println("AttackResult: " + gson.toJson(ar));
		}
	}

  private static InputRegex getRegex(String regex, String regexFile, boolean quiet) {
    InputRegex ret = null;
    if (regex != null && regex != "NONE") {
      ret = new InputRegex(regex);
    }
    else if (regexFile != null && regexFile != "NONE") {
      // regexFile: load, parse, extract
      try {
        Gson gson = new Gson();
        ret = gson.fromJson(new FileReader(regexFile), InputRegex.class);
        System.out.println("From file <" + regexFile + ">, read regex <" + ret.getRegex() + ">");
      } catch (FileNotFoundException e) {
        System.out.println("Error, invalid file <" + regexFile + ">");
			  System.exit(-1);
      }
    } else {
      // No regexFile, get it from prompt
      if (!quiet) System.out.print("Enter your regex: ");

      Scanner input = new Scanner(System.in);
      String _regex = input.hasNextLine() ? input.nextLine() : null;
      input.close();
      ret = new InputRegex(_regex);
    }
    return ret;
  }
}
