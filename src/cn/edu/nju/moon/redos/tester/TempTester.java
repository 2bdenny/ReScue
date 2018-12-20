package cn.edu.nju.moon.redos.tester;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;

import cn.edu.nju.moon.redos.RedosAttacker;
import cn.edu.nju.moon.redos.Trace;
//import cn.edu.nju.moon.redos.attackers.AnalyzedGeneticAttacker;
//import cn.edu.nju.moon.redos.attackers.CollisionAttacker;
import cn.edu.nju.moon.redos.attackers.GeneticAttacker;
//import cn.edu.nju.moon.redos.attackers.RandomStringAttacker;
import cn.edu.nju.moon.redos.regex.*;
import cn.edu.nju.moon.redos.utils.NodeRelation;
import cn.edu.nju.moon.redos.utils.RegexFormatter;

/**
 * A test class
 * TODO Should be delete before release
 */
public class TempTester {
	private static Random rnd = new Random();
	private static Scanner input = new Scanner(System.in);
	
	public static void main(String[] args) {
//		slowfuzzBest(args);
//		rexploiterBest(args);
//		validate(args);
//		validateParsable();
//		validateRXXR2(args);
//		validateSlowFuzz(args);
//		dealRegex();
//		testReport();
//		testLog();
//		testCollisionAttacker();
//		testRandomAttacker();
//		testGeneticAttacker();
		testView();
//		testRelation();
//		testAnalyzedGeneticAttacker();
//		testStack();
//		testLittle();
//		testEscaped();
//		testOneEscape();
//		validateSuccess();
		
//		testScript();
//		testFeature(args);
//		fixValidate(args);
		
		input.close();
	}
	
	public static void fixValidate(String[] args) {
		input = new Scanner(System.in);
		String atk = input.nextLine();
		while (input.hasNext()) {
			atk += "\n" + input.nextLine();
		}
		
		try {
			List<String> items = Files.readAllLines(Paths.get(args[0]));
			String regex = items.get(0);
			ReScuePattern vp = ReScuePattern.compile(regex);
			ReScueMatcher vm = vp.matcher(atk, new Trace(1e8));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testFeature(String[] args) {
		if (args.length != 1) {
			System.out.println("Input Error, expect: java -jar Feature.jar feature.txt");
			return ;
		}
		
		try {
			List<String> items = Files.readAllLines(Paths.get(args[0]));
			ReScuePattern jp = null;
			for (String i : items) {
				if (i.length() < 1) continue;
				
				if (i.charAt(0) == '#')
					System.out.println(i);
				else {
					Pattern ps = Pattern.compile("^Success");
					Matcher m = ps.matcher(i);
					if (m.find()) {
						System.out.println(i);
						// If Regex compile error
						if (jp == null) System.out.println("Success: Not Passed");
						else {
							// The Success input
							String input = i.split(":")[1];
							ReScueMatcher jm = jp.matcher(input, new Trace(1e5));
							Trace jt = jm.find();
							if (jt.matchSuccess) {
								System.out.println("Success: Passed");
							} else {
								System.out.println("Success: Not Passed");
							}
						}
					} else {
						Pattern pf = Pattern.compile("^Failed");
						m = pf.matcher(i);
						if (m.find()) {
							System.out.println(i);
							if (jp == null) System.out.println("Failed: Not Passed");
							else {
								// The Failed input
								String input = i.split(":")[1];
								ReScueMatcher jm = jp.matcher(input, new Trace(1e5));
								Trace jt = jm.find();
								if (!jt.matchSuccess) {
									System.out.println("Failed: Passed");
								} else {
									System.out.println("Failed: Not Passed");
								}
							}
						} else {
							// The regex
							try {
								System.out.println(i);
								jp = ReScuePattern.compile(i);
							} catch (PatternSyntaxException e) {
								jp = null;
								e.printStackTrace();
							}
						}
					}
				}
//				System.out.println(i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void testScript() {
		Scanner input = new Scanner(System.in);
		String line = input.nextLine();
		System.out.println(line);
		input.close();
	}
	
	public static void testGeneticAttacker() {
//		if (false) {
//			String r = input.hasNextLine() ? input.nextLine() : null;
//			// ^([A-Z]|[a-z]|[0-9])(([A-Z])*(([a-z])*([0-9])*(%)*(&)*(')*(\+)*(-)*(@)*(_)*(\.)*)|(\ )[^  ])+$
//			
//			ReScuePattern p = RegexFormatter.formatRegex(r);
//			RedosAttacker atk = new GeneticAttacker();
//			Trace t = atk.attack(p);
//			System.out.println(t.getMatchSteps() + ":" + t.str);
//			return ;
//		}
		
		try {
			int attackSuccess = 0;
//			List<String> regs = Files.readAllLines(new File("test/data/uncover_regs.txt").toPath());
			List<String> regs = Files.readAllLines(new File("test/data/total_pumpable.txt").toPath());
			for (int i = 0; i < regs.size(); i++) {
				String r = regs.get(i);
				System.out.println(r);
				try {
					ReScuePattern p = ReScuePattern.compile(r);
					RedosAttacker atk = new GeneticAttacker();
					Trace t = atk.attack(p);
					if (t != null && t.attackSuccess()) attackSuccess++;
				} catch (PatternSyntaxException e) {
				}
			}
			System.out.println(attackSuccess + "/" + regs.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testLog() {		
		String r = input.hasNextLine() ? input.nextLine() : null;
		String s = input.nextLine();
		ReScuePattern jp = ReScuePattern.compile(r);
		Trace t = new Trace(1e5);
		ReScueMatcher jm = jp.matcher(s, t);
		jm.find();
		t.printStep();
		if (true) return;
		
		
//		String a = "\n\naehikEaehikEX\naehikEXY\nikEXY\n";
//		String a = input.hasNextLine() ? input.nextLine() : null;
//		ReScuePattern vp = ReScuePattern.compile("<a(.+?)href=\\\"(.+?)\\\"(.*?)>(.+?)<\\\\/a>");
//		ReScuePattern vp = ReScuePattern.compile(r);
//		Trace tmpt = new Trace(1e5);
//		ReScueMatcher vm = vp.matcher(tmpt.str, new Trace(1e7));
//		Trace vt = vm.find();
//		System.out.println(vt.getMatchSteps());
////		^[A-Z][a-z0-9]*(([0-9]+)|([A-Z][a-z0-9]*))*$
////		AA0A0!
////		"(\\\\|\\"|[^"])*"
////		"\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
//		
////		if (true) return;
//		try {
//			List<String> regs = Files.readAllLines(new File("test/data/rxxr_only_summary.txt").toPath());
//			List<String> atks = Files.readAllLines(new File("test/data/rxxr_only_attack_strings.txt").toPath());
//			for (int i = 0; i < regs.size(); i++) {
//				String regex = regs.get(i);
//				String atk_string = atks.get(i);
//				// RXXR2's node coverage
//				ReScuePattern p = ReScuePattern.compile(regex);
//				// p.paintRegex();
//				ReScueMatcher m = p.matcher(atk_string, new Trace(1e5));
//				Trace t = m.find();
//				// System.out.println(p.getAllNodes().size());
//				// System.out.println(c.getAllNodes().size());
//
//				RedosAttacker atk = new GeneticAttacker();
//				t = atk.attack(p);
////				if (t != null) GeneticAttacker.reRepeat(p, t);
////				System.out.println(c.getAllNodes().size() + "/" + p.getAllNodes().size());
////				System.out.println(regex + "\n===" + c.getTrace().getLog().size());
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public static void validate(String[] args) {
		if (args.length != 2) {
			System.out.println("Input Error, expect: java -jar Validator.jar summaryDir isFormat");
			return ;
		}
		
		try {
			List<String> logs = Files.readAllLines(Paths.get(args[0], "collect_summary.txt"));
			for (int i = 0; i < logs.size(); i++) {
				String log = logs.get(i);
				Pattern p = Pattern.compile("^(\\d+) : ([A-Z]+) : ([a-z]+) : [^:]+ : (.*)$");
				Matcher m = p.matcher(log);
				if (m.find()) {
					String index = m.group(1);
					String act_result = m.group(2);
					String ana_result = m.group(3);
					String regex = args[1].equalsIgnoreCase("true") ? m.group(4) : RegexFormatter.deleteFlag(m.group(4));
					
					try {
						ReScuePattern vp = ReScuePattern.compile(regex);
						log = log.replaceFirst(act_result, "parsable");
						
						if (ana_result.equalsIgnoreCase("success")) {
							String vul = Files.readAllLines(Paths.get(args[0], index + "_vul.txt")).get(0);
							ReScueMatcher vm = vp.matcher(vul, new Trace(RedosAttacker.SUCCESS_THRESHOLD));
							Trace vt = vm.find();
							if (vt.attackSuccess()) {
								log = log.replaceFirst("parsable", "success");
								BufferedWriter bw = new BufferedWriter(
										new FileWriter(Paths.get(args[0], index + "_atk.txt").toFile()));
								bw.write(vul);
								bw.close();
							} else
								log = log.replaceFirst("parsable", "failed");
						}
					} catch (PatternSyntaxException e) {
						log = log.replaceFirst(act_result, "unparsable");
					}
				}
				logs.set(i, log);
			}
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(args[0], "collect_summary.txt").toString()));
			for (String log : logs) {
				bw.write(log + '\n');
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void validateRXXR2(String[] args) {
		if (args.length != 3) {
			System.out.println("Input Error. Expect: regex.txt attack.txt summaryDir");
			return ;
		}
		try {
			List<String> regs = Files.readAllLines(new File(args[0]).toPath());
			List<String> atks = Files.readAllLines(new File(args[1]).toPath());
			assert(regs.size() == atks.size());
			
			File sumDir = new File(args[2]);
			if (!sumDir.exists()) sumDir.mkdir();	
			else for (File f : sumDir.listFiles()) f.delete();
//			if (true) return;
			
			File collect_summary = Paths.get(args[2], "collect_summary.txt").toFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(collect_summary));
			for (int i = 0; i < regs.size(); i++) {
				try {
					ReScuePattern p = ReScuePattern.compile(regs.get(i));
					ReScueMatcher m = p.matcher(atks.get(i), new Trace(1e7));
					Trace t = m.find();
					if (t.attackSuccess()) {
						if (t.attackSuccess()) {
							bw.write((i+1) + " : success : " + regs.get(i) + "\n");
							File atk_file = Paths.get(args[2], (i+1) + "_atk.txt").toFile();
							BufferedWriter abw = new BufferedWriter(new FileWriter(atk_file));
							abw.write(atks.get(i) + "\n");
							abw.close();
						}
						else bw.write((i+1) + " : second failed : " + regs.get(i) + "\n");
					} else bw.write((i+1) + " : failed : " + regs.get(i) + "\n");
				} catch (PatternSyntaxException e) {
					bw.write((i+1) + " : error compile : " + regs.get(i) + "\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void rexploiterBest(String[] args) {
		if (args.length != 1) {
			System.out.println("Input error, expect: resultDir");
			return ;
		}
		
		try {
			// result/reglib_deformat.txt/collect_summary.txt
			List<String> sums = Files.readAllLines(Paths.get(args[0], "collect_summary.txt"));
			for (int i = 0; i < sums.size(); i++) {
				String line = sums.get(i);
				String extractor = "^(\\d+) : ([A-Z]+) : ([a-z]+) : .* \\(s\\) : (.*)$";
				Pattern p = Pattern.compile(extractor);
				Matcher m = p.matcher(line);
				if (m.find()) {
					String index = m.group(1);
					String ana_result = m.group(3);
					String regex = m.group(4);

					try {
						ReScuePattern vp = ReScuePattern.compile(RegexFormatter.deleteFlag(regex));
						// If analyse success, find the best trace
						if ("success".equalsIgnoreCase("ana_result")) {							
							Trace bestTrace = null;

							// result/reglib_deformat.txt/1/
							File vulDir = Paths.get(args[0], index).toFile();
							
							for (File f : vulDir.listFiles()) {
								List<String> atklines = Files.readAllLines(f.toPath());
								String content = String.join("\n", atklines);
								ReScueMatcher vm = vp.matcher(content, new Trace(RedosAttacker.SUCCESS_THRESHOLD));
								Trace tmp = vm.find();

								// Record the best trace
								if (bestTrace == null) {
									bestTrace = tmp;
								} else {
									if (!bestTrace.attackSuccess()) {
										if (tmp.attackSuccess())
											bestTrace = tmp;
										else if (tmp.score(null) > bestTrace.score(null))
											bestTrace = tmp;
									} else {
										if (tmp.attackSuccess()
												&& tmp.effectiveStr.length() < bestTrace.effectiveStr.length()) {
											bestTrace = tmp;
										}
									}
								}
								// Delete the file
								f.delete();
							}

							// Write to file
							if (bestTrace != null) {
								File vul = Paths.get(args[0], index + "_vul.txt").toFile();
								BufferedWriter bw = new BufferedWriter(new FileWriter(vul));
								bw.write(bestTrace.str);
								bw.close();
							} else {
								// Abnormal timeout
								System.out.println("When select best trace, vulDir is empty");
							}
							// Delete the vuls' directory
							vulDir.delete();
						} else {
							// Just delete the empty vul dir
							File vulDir = Paths.get(args[0], index).toFile();
							if (vulDir.exists() && vulDir.isDirectory()) {
								if (!vulDir.delete()) {
									for (File f : vulDir.listFiles()) {
										f.delete();
									}
									vulDir.delete();
								}
							}
						}
					// Cannot parse in java
					} catch (PatternSyntaxException e) {
						// result/reglib_deformat.txt/1/
						File vulDir = Paths.get(args[0], index).toFile();
						if (vulDir.exists() && vulDir.isDirectory()) {
							if (!vulDir.delete()) {
								for (File f : vulDir.listFiles()) {
									f.delete();
								}
								vulDir.delete();
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void slowfuzzBest(String[] args) {
		if (args.length != 2) {
			// result/cp.txt 1
			System.out.println("Input Error. Expect: resultDir index");
			return ;
		}
		
		String regex = "";
		try {
			// The regex: result/cp.txt/1/d1.txt
			List<String> reglines = Files.readAllLines(Paths.get(args[0], args[1], "d" + args[1] + ".txt"));
			regex = String.join("\n", reglines);
			ReScuePattern p = ReScuePattern.compile(RegexFormatter.deleteFlag(regex));
			
			Trace bestTrace = null;
			// Test input of corpus
			File cps = Paths.get(args[0], args[1], "corpus").toFile();
			for (File f : cps.listFiles()) {
				List<String> atklines = Files.readAllLines(f.toPath());
				String content = String.join("\n", atklines);
				ReScueMatcher m = p.matcher(content, new Trace(RedosAttacker.SUCCESS_THRESHOLD));
				Trace tmp = m.find();
				
				// Record the best trace
				if (bestTrace == null) {
					bestTrace = tmp;
				} else {
					if (!bestTrace.attackSuccess()){
						if (tmp.attackSuccess()) bestTrace = tmp;
						else if (tmp.score(null) > bestTrace.score(null)) bestTrace = tmp;
					} else {
						if (tmp.attackSuccess() && tmp.effectiveStr.length() < bestTrace.effectiveStr.length()) {
							bestTrace = tmp;
						}
					}
				}
			}
			
			// Test input of out
			File out = Paths.get(args[0], args[1], "out").toFile();
			for (File f : out.listFiles()) {
				List<String> atklines = Files.readAllLines(f.toPath());
				String content = String.join("\n", atklines);
				ReScueMatcher m = p.matcher(content, new Trace(RedosAttacker.SUCCESS_THRESHOLD));
				Trace tmp = m.find();
				
				// Record the best trace
				if (bestTrace == null) {
					bestTrace = tmp;
				} else {
					if (!bestTrace.attackSuccess()){
						if (tmp.attackSuccess()) bestTrace = tmp;
						else if (tmp.score(null) > bestTrace.score(null)) bestTrace = tmp;
					} else {
						if (tmp.attackSuccess() && tmp.effectiveStr.length() < bestTrace.effectiveStr.length()) {
							bestTrace = tmp;
						}
					}
				}
			}
			
			// If attack success, write to atk and vul file
			if (bestTrace.attackSuccess()) {
				File atk = Paths.get(args[0], args[1] + "_atk.txt").toFile();
				File vul = Paths.get(args[0], args[1] + "_vul.txt").toFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(atk));
				bw.write(bestTrace.str);
				bw.close();
				bw = new BufferedWriter(new FileWriter(vul));
				bw.write(bestTrace.str);
				bw.close();
			// Only write to vul file
			} else {
				File vul = Paths.get(args[0], args[1] + "_vul.txt").toFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(vul));
				bw.write(bestTrace.str);
				bw.close();
			}
			

			// Read summary line
			String sum = Files.readAllLines(Paths.get(args[0], args[1], "result.txt")).get(0);
			// Update real result
			if (bestTrace.attackSuccess()) sum = sum.replaceFirst("REALRESULT", "success");
			else sum = sum.replaceFirst("REALRESULT", "failed");
			// Update analyse result
			sum = sum.replaceFirst("ANARESULT", "success");
			BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(args[0], args[1], "result.txt").toFile()));
			bw.write(sum);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PatternSyntaxException e) {
			try {
				// Read summary line
				String sum = Files.readAllLines(Paths.get(args[0], args[1], "result.txt")).get(0);
				// Update real result
				sum = sum.replaceFirst("REALRESULT", "unparsable");
				// Update analyse result
				sum = sum.replaceFirst("ANARESULT", "success");
				BufferedWriter bw = new BufferedWriter(new FileWriter(Paths.get(args[0], args[1], "result.txt").toFile()));
				bw.write(sum);
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void dealRegex() {
		try {
			List<String> regs = Files.readAllLines(new File("test/data/py.txt").toPath());
			for (String r : regs) {
				System.out.println(RegexFormatter.deleteFlag(r));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static void testRandomAttacker() {
//		try {
//			List<String> regs = Files.readAllLines(new File("test/data/pumpable.txt").toPath());
//			int attackSuccess = 0, unparsable = 0;
//			int no = 0;
//			for (String r : regs) {
//				System.out.print(++ no + " ");
//				if (no % 10 == 0) System.out.println();
//				
//				RedosAttacker atk = new RandomStringAttacker(50);
//				try {
//					ReScuePattern p = RegexFormatter.formatRegex(r);
////					System.out.println(RegexFormatter.deleteFlag(r));
//					Trace trace = atk.attack(p);
//					if (trace != null && trace.attackSuccess())
//						attackSuccess++;
//				} catch (PatternSyntaxException e) {
//					unparsable ++;
//				}
//			}
//			System.out.println("===Random String Attacker===");
//			System.out.println("Total Regexes: " + regs.size());
//			System.out.println("Attacked: " + attackSuccess);
//			System.out.println("Unparsable: " + unparsable);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public static void testCollisionAttacker() {
//		try {
//			List<String> regs = Files.readAllLines(new File("test/data/pumpable.txt").toPath());
//			int attackSuccess = 0, unparsable = 0;
//			int no = 0;
//			for (String r : regs) {
//				System.out.print(++ no + " ");
//				if (no % 10 == 0) System.out.println();
//				
//				RedosAttacker atk = new CollisionAttacker("test/data/CollisionAttacker/collisions.txt");
//				try {
//					ReScuePattern p = RegexFormatter.formatRegex(r);
////					System.out.println(RegexFormatter.deleteFlag(r));
//					Trace trace = atk.attack(p);
//					if (trace != null && trace.attackSuccess())
//						attackSuccess++;
//				} catch (PatternSyntaxException e) {
//					unparsable ++;
//				}
//			}
//			System.out.println("===Collision Attacker===");
//			System.out.println("Total Regexes: " + regs.size());
//			System.out.println("Attacked: " + attackSuccess);
//			System.out.println("Unparsable: " + unparsable);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static void testReport() {
		String[] suc = {"XaX", "XaaX", "XaaaX", "XaaaaX", "XaaaaaX", "XaaaaaaX", "XaaaaaaaX", "XaaaaaaaaX", "XaaaaaaaaaX", "XaaaaaaaaaaX"};
		String[] fal = {"Xa", "Xaa", "Xaaa", "Xaaaa", "Xaaaaa", "Xaaaaaa", "Xaaaaaaa", "Xaaaaaaaa", "Xaaaaaaaaa", "Xaaaaaaaaaa"};
		
		ReScuePattern p = ReScuePattern.compile("X.+X");
		for (int i = 0; i < suc.length; i++) {
			ReScueMatcher m = p.matcher(suc[i], new Trace(1e5));
			Trace t = m.find();
			assert(t.matchSuccess);
			System.out.println(t.getMatchSteps());
		}
		System.out.println("---X.+X---");
		for (int i = 0; i < fal.length; i++) {
			ReScueMatcher m = p.matcher(fal[i], new Trace(1e5));
			Trace t = m.find();
			assert(!t.matchSuccess);
			System.out.println(t.getMatchSteps());
		}
		System.out.println("---X.+X---");
		System.out.println();
		

		p = ReScuePattern.compile("X(.+)+X");
		for (int i = 0; i < suc.length; i++) {
			ReScueMatcher m = p.matcher(suc[i], new Trace(1e5));
			Trace t = m.find();
			assert(t.matchSuccess);
			System.out.println(t.getMatchSteps());
		}
		System.out.println("---X(.+)+X---");
		for (int i = 0; i < fal.length; i++) {
			ReScueMatcher m = p.matcher(fal[i], new Trace(1e5));
			Trace t = m.find();
			assert(!t.matchSuccess);
			System.out.println(t.getMatchSteps());
		}
		System.out.println("---X(.+)+X---");
	}
	
	public static void testView() {		
		System.out.print("Input regex: ");
		String regex = input.hasNextLine() ? input.nextLine() : null;
		
//		^(([a-zA-Z]:)|(\\{2}\w+)\$?)\2(\\(\w[\w ]*.*))+\.((html|HTML)|(htm|HTM))$
		
		ReScuePattern p  = ReScuePattern.compile(regex);
		p.paintRegex();
	}
	
//	public static void testRelation() {
//		System.out.print("Input regex: ");
//		String regex = input.hasNextLine() ? input.nextLine() : null;
//		
////		^(([a-zA-Z]:)|(\\{2}\w+)\$?)(\\(\w[\w ]*.*))+\.((html|HTML)|(htm|HTM))$
////		[a-zA-Z0-9_\\-]+@([a-zA-Z0-9_\\-]+\\.)+(com)
////		a@\\\\\\\.net
//		
//		ReScuePattern p  = ReScuePattern.compile(regex);
//		p.paintRegex();
//		NodeRelation relation = p.getNodeRelation();
//		relation.print();
//	}
	
	public static void testLittle() {
		Pattern p = Pattern.compile("^abaa?");
		Matcher m = p.matcher("abababa");
		while (m.find()) {
			System.out.println(m.group());
		}
	}
	
	public static void testEscaped() {
		try {
			List<String> regs = Files.readAllLines(new File("test/data/total.txt").toPath());
			for (String r : regs) {
				System.out.println(RegexFormatter.deleteFlag(r));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testOneEscape() {
		String s = input.hasNextLine() ? input.nextLine() : "";
		String n = input.hasNextLine() ? input.nextLine() : "";
		System.out.println(Pattern.compile("(^\\\\[^\\\\])|([^\\\\]\\\\[^\\\\])").matcher(s).find());
		System.out.println(Pattern.compile("(^\\\\[^\\\\])|([^\\\\]\\\\[^\\\\])").matcher(n).find());
	}
	
	public static void validateSuccess() {
		String dir = "test/result/redos_double_genetic_init.jar/total_escaped.txt_result/";
		String sum = dir + "collect_summary.txt";
		int secondSuc = 0;
		try {
			List<String> items = Files.readAllLines(new File(sum).toPath());
			for (String i : items) {
				Pattern p = Pattern.compile("^(\\d+) : (init|cross|mutate) : (.*)$");
				Matcher m = p.matcher(i);
				if (m.find()) {
					String atk_file = dir + m.group(1) + "_atk.txt";
					List<String> atk = Files.readAllLines(new File(atk_file).toPath());
					String a = StringUtils.join(atk, '\n');
					String r = m.group(3);
					ReScuePattern vp = ReScuePattern.compile(r);
					ReScueMatcher vm = vp.matcher(a, new Trace(1e7));
					Trace t = vm.find();
					if (t.attackSuccess()) secondSuc++;
					System.out.println(t.getMatchSteps());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("second validate: " + secondSuc);
	}
}
