package cn.edu.nju.moon.redos.tester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
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
// import cn.edu.nju.moon.redos.attackers.GeneticAttackerWithoutIncubating;
// import cn.edu.nju.moon.redos.attackers.GeneticAttackerWithoutPumping;
// import cn.edu.nju.moon.redos.attackers.GeneticAttackerWithoutSeeding;
import cn.edu.nju.moon.redos.regex.ReScuePattern;
import cn.edu.nju.moon.redos.utils.RegexFormatter;

@RunWith(Parameterized.class)
public class RedosTester {
    private RedosAttacker attacker;
    private static final String[] usage = { "Usage:", "java -jar rescue.jar -h",
        "\tShow this text.", "",
        "java -jar ReScue.jar <-sl string length> <-pz population size> <-g generations>",
        "\t\t\t\t\t<-cp crossover probability> <-mp mutation probability>",
        "\t\t\t\t\t<-q> <-v>", "\tAttack the <regex> with the <attacker>",
        "\tby default(sl is 128, pz is 200, g is 200, cp is 10(0.1), mp is 10(0.1)):",
        "\t\tjava -jar ReScue.jar", "\tor:",
        "\t\tjava -jar ReScue.jar -sl 128 -pz 200 -g 200 -cp 10 -mp 10", "",
        "-sl\tLimit the string length", "", "-pz\tLimit the population size",
        "", "-g\tLimit the generation", "",
        "-cp\tSet the crossover possiblity, the real possibility is calculated by cp / 100.0",
        "",
        "-mp\tSet the mutation possibility, the real possibility is calculated by mp / 100.0",
        "", "-v\tView the inner structure of a regex, usage:",
        "\tjava -jar ReScue.jar -v", "\tor combine with other options", "",
        "-q\tQuiet mode, do not show input message, usage:",
        "\tjava -jar ReScue.jar -q", "\tor combine with other options" };


    public static void main(String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("test")) {
            JUnitCore junit = new JUnitCore();
            junit.run(RedosTester.class);
        }
        else {
            // -h (--help)
            if (args.length == 1 && args[0].equalsIgnoreCase("-h")) {
                if (usage != null) {
                    for (String u : usage)
                        System.out.println(u);
                }
            }
            else if (args.length == 1 && (args[0].equalsIgnoreCase("-v")
                || args[0].equalsIgnoreCase("--visual"))) {
                TempTester.testView();
            }
            else {
                long start_time = System.nanoTime();
                int sl = 64;
                int ml = 128;
                int pz = 200;
                int g = 200;
                int mp = 10;
                int cp = 5;
                boolean quiet = false; // Whether print tip
                boolean vision = false; // Whether show diagram
                for (int i = 0; i < args.length; i++) {
// System.out.println(args[i]);
                    if (args[i].equalsIgnoreCase("-sl")) {
                        sl = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    else if (args[i].equalsIgnoreCase("-ml")) {
                        ml = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    else if (args[i].equalsIgnoreCase("-pz")) {
                        pz = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    else if (args[i].equalsIgnoreCase("-g")) {
                        g = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    else if (args[i].equalsIgnoreCase("-q")) {
                        quiet = true;
                    }
                    else if (args[i].equalsIgnoreCase("-cp")) {
                        cp = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    else if (args[i].equalsIgnoreCase("-v")) {
                        vision = true;
                    }
                    else if (args[i].equalsIgnoreCase("-mp")) {
                        mp = Integer.parseInt(args[i + 1]);
                        i++;
                    }
                    else
                        break;
                }

                if (!quiet)
                    System.out.print("Input regex: ");
                Scanner input = new Scanner(System.in);
                String inputRegex = input.hasNextLine()
                    ? input.nextLine()
                    : null;
                String regex = null;
                // An array of regexes is used in case there is more than one
                // being input
                String[] regexes = null;
                // If txt file
                if (inputRegex.contains(".txt")) {
                    regex = "";
                    String fileName = inputRegex;
                    String path = "";
                    try {
                        // Path of current working jar
                        path = RedosTester.class.getProtectionDomain()
                            .getCodeSource().getLocation().toURI().getPath();
                    }
                    catch (URISyntaxException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    File f = new File(path + fileName);
                    // Gets parent path where file is located
                    String parentPath = f.getParentFile().toString();
                    // Sets up path
                    File file = new File(parentPath + "/" + fileName);

                    Scanner scanner = null;
                    try {
                        // Reads in file to a string, separating regexes with a
                        // ","
                        scanner = new Scanner(file);
                        while (scanner.hasNext()) {
                            regex += scanner.nextLine() + ",";
                        }
                    }
                    catch (FileNotFoundException e) {
                        System.out.println("Scanner error opening the file ");
                        System.out.println(e.getMessage());
                    }
                    // Splits string into string array
                    regexes = regex.split(",");
                    scanner.close();
                }
                // If not a text file, just a line
                else {
                    System.out.println("Input regex: ");
                    regex = input.hasNextLine() ? input.nextLine() : null;
                    // Makes array with one regex
                    regexes = new String[] { regex };
                }
                input.close();
                regex = regexes[0];
                if (regex == null || regex.length() < 1) {
                    System.out.println("Please check your regex.");
                    return;
                }

                RedosAttacker atk = new GeneticAttacker(sl, ml, pz, g,
                    (double)mp / (double)100, (double)cp / (double)100);
// RedosAttacker atk = new GeneticAttackerWithoutSeeding(sl, ml, pz, g,
// (double)mp / (double)100, (double)cp / (double) 100);
// RedosAttacker atk = new GeneticAttackerWithoutIncubating(sl, ml, pz, g,
// (double)mp / (double)100, (double)cp / (double) 100);
// RedosAttacker atk = new GeneticAttackerWithoutPumping(sl, ml, pz, g,
// (double)mp / (double)100, (double)cp / (double) 100);

                // Goes through list of regexes
                for (int i = 0; i < regexes.length; i++) {
                    regex = regexes[i];
                    try {
// ReScuePattern p = RegexFormatter.formatRegex(regex);

                        ReScuePattern p = ReScuePattern.compile(regex);
                        if (vision)
                            p.paintRegex();
                        System.out.println(RegexFormatter.deleteFlag(regex));
                        Trace trace = atk.attack(p);
                        if (trace != null && trace.attackSuccess()) {
                            long elapsed_time = System.nanoTime() - start_time;
                            System.out.println("TIME: " + ((double)elapsed_time
                                / 1e9) + " (s)");
                            System.out.println(
                                "Attack success, attack string is:");
                            System.out.println(trace.str);
                        }
                        else {
                            long elapsed_time = System.nanoTime() - start_time;
                            System.out.println("TIME: " + ((double)elapsed_time
                                / 1e9) + " (s)");
                            System.out.println("Attack failed");
                        }
                    }

                    catch (PatternSyntaxException e) {
                        System.out.println(RegexFormatter.deleteFlag(regex));
                        long elapsed_time = System.nanoTime() - start_time;
                        System.out.println("TIME: " + ((double)elapsed_time
                            / 1e9) + " (s)");
                        System.out.println("Regex compile error");
                    }

                }

            }

        }
    }


    public RedosTester(Class<?> attacker) {
        try {
            this.attacker = (RedosAttacker)attacker.getConstructor()
                .newInstance();
        }
        catch (InstantiationException | IllegalAccessException
            | IllegalArgumentException | InvocationTargetException
            | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }


    @Parameterized.Parameters
    public static Collection<Object> getAllAttackers() {
        return Arrays.asList(new Object[] {
// RandomStringAttacker.class,
// CollisionAttacker.class,
            GeneticAttacker.class });
    }


    @Test
    public void testAttacker() {
        // read regex from test/data/regex.txt & test all attacker
        List<String> regs = null;
        try {
            regs = Files.readAllLines(new File("test/data/pumpable.txt")
                .toPath());
        }
        catch (IOException e1) {
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
                if (trace != null && trace.attackSuccess())
                    attacked++;
            }
            catch (PatternSyntaxException e) {
                System.out.print(i + ": ");
                System.out.println(RegexFormatter.deleteFlag(r));
                System.out.println(i + ": regex compile error");
            }
        }
        System.out.println("---Attack Report---\nCan Attack: " + can_attack
            + "\nFirst Attacked: " + attacked + "\nTotal: " + total + "\n");

        long tEnd = System.nanoTime();
        double elapsedSeconds = (tEnd - tStart) / 1.0e9;
        System.out.println("Elapsed seconds: " + elapsedSeconds);
    }
}
