package cn.edu.nju.moon.redos.tester;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
//import cn.edu.nju.moon.redos.attackers.GeneticAttackerWithoutIncubating;
//import cn.edu.nju.moon.redos.attackers.GeneticAttackerWithoutPumping;
//import cn.edu.nju.moon.redos.attackers.GeneticAttackerWithoutSeeding;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.utils.RegexFormatter;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

@RunWith(Parameterized.class)
public class RedosTester {
	private RedosAttacker attacker;
	
	public static void main(String[] args) {
		ArgumentParser parser = ArgumentParsers.newFor("ReScue").build();
		parser.defaultHelp(true);
		parser.description("ReScue is a tool to auto detect ReDoS vulnerabilities in regexes.");
		parser.addArgument("-q", "--quiet").action(Arguments.storeTrue()).help("Quiet mode, hide input tips.");
		parser.addArgument("-ut", "--unittest").action(Arguments.storeTrue()).help("Start unittest.");
		parser.addArgument("-v", "--visual").action(Arguments.storeTrue()).help("Show e-NFA of the input regex.");
		parser.addArgument("-ml", "--maxLength").setDefault(128).help("Maximum string length.");
		parser.addArgument("-pz", "--popSize").setDefault(200).help("Maximum population size.");
		parser.addArgument("-g", "--generation").setDefault(200).help("Maximum generations.");
		parser.addArgument("-cp", "--crossPossibility").setDefault("10").help("The crossover possibility, default is 10, means 10%.");
		parser.addArgument("-mp", "--mutatePossibility").setDefault("10").help("The mutation possibility, default is 10, means 10%.");
		
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
		
		if (ns.getBoolean("unittest")) {
			JUnitCore junit = new JUnitCore();
			junit.run(RedosTester.class);
		} else {
			if (ns.getBoolean("visual")){
				TempTester.testView();
			} else {
				long start_time = System.nanoTime();
				// These values are used for test
				int sl = 64; // The deprecated seed length
				int ml = 128;
				int pz = 200;
				int g = 200;
				int mp = 10;
				int cp = 5;
				
				// Read input arguments
				boolean quiet = ns.getBoolean("quiet");
				boolean vision = ns.getBoolean("visual");
				sl = ns.getInt("maxLength");
				ml = ns.getInt("maxLength");
				pz = ns.getInt("popSize");
				g = ns.getInt("generation");
				cp = ns.getInt("crossPossibility");
				mp = ns.getInt("mutatePossibility");
				
				if (!quiet) System.out.print("Input regex: ");
				
				Scanner input = new Scanner(System.in);
				String regex = input.hasNextLine() ? input.nextLine() : null;
				input.close();
				
				if (regex == null || regex.length() < 1) {
					System.out.println("Please check your regex.");
					return ;
				}
				
				RedosAttacker atk = new GeneticAttacker(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
//				RedosAttacker atk = new GeneticAttackerWithoutSeeding(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
//				RedosAttacker atk = new GeneticAttackerWithoutIncubating(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
//				RedosAttacker atk = new GeneticAttackerWithoutPumping(sl, ml, pz, g, (double)mp / (double)100, (double)cp / (double) 100);
				try {
//					ReScuePattern p = RegexFormatter.formatRegex(regex);
					ReScuePattern p = ReScuePattern.compile(regex);
					if (vision) p.paintRegex();
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
	}
	
	public RedosTester(Class<?> attacker) {
		try {
			this.attacker = (RedosAttacker) attacker.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Parameterized.Parameters
	public static Collection<Object> getAllAttackers() {
		return Arrays.asList(new Object[] {
//				RandomStringAttacker.class,
//				CollisionAttacker.class,
				GeneticAttacker.class
		});
	}
	
	@Test
	public void testAttacker() {
		// read regex from test/data/regex.txt & test all attacker
		List<String> regs = null;
		try {
			regs = Files.readAllLines(new File("test/data/pumpable.txt").toPath());
		} catch (IOException e1) {
			System.out.println("Error: Open regex file");
		}
		
		long tStart = System.nanoTime();
		
		// test report
		int can_attack = 0;
		int attacked = 0;
		int total = regs.size();
		// random string attacker
		for (int i = 0; i < regs.size(); i++) {
			String r = regs.get(i);
			System.out.println(r);
			try {
				ReScuePattern p = RegexFormatter.formatRegex(r);
				System.out.print(i + ": ");
				System.out.println(RegexFormatter.deleteFlag(r));
				Trace trace = attacker.attack(p);
				if (trace != null && trace.attackSuccess()) attacked++;
			} catch (PatternSyntaxException e) {
				System.out.print(i + ": ");
				System.out.println(RegexFormatter.deleteFlag(r));
				System.out.println(i + ": regex compile error");
			}
		}
		System.out.println("---Attack Report---\nCan Attack: " + can_attack + "\nFirst Attacked: " + attacked + "\nTotal: " + total + "\n");
		
		long tEnd = System.nanoTime();
		double elapsedSeconds = (tEnd - tStart) / 1.0e9;
		System.out.println("Elapsed seconds: " + elapsedSeconds);
	}
}
