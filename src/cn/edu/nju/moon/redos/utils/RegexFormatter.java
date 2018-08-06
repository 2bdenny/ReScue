package cn.edu.nju.moon.redos.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringEscapeUtils;

import cn.edu.nju.moon.redos.regex.ReScuePattern;

/**
 * Format all escaped and double escaped to formal regex
 */
public class RegexFormatter {
	/**
	 * Remove the front and the end \'"
	 * @param reg
	 * @return
	 */
	public static String nakeRegex(String reg) {
		if (reg != null && reg.length() > 1) {
			if (reg.charAt(reg.length() - 1) == '\n') reg = reg.substring(0, reg.length() - 1); // Delete the end \n
			// u'\\n'
			// First char is u		and		length > 3	and			first char equals to last char		  and	the first char is " or '
			if (reg.charAt(0) == 'u' && reg.length() > 3 && (reg.charAt(1) == reg.charAt(reg.length() - 1) && (reg.charAt(1) == '"' || reg.charAt(1) == '\''))) {
				reg = reg.substring(2, reg.length() - 1);
				if (!(Pattern.compile("(^\\\\[^\\\\])|([^\\\\]\\\\[^\\\\])").matcher(reg).find()) && reg.contains("\\\\")) {
					reg = StringEscapeUtils.unescapeJava(reg);
				}
			// There are split char at the front and the end
			} else if (reg.charAt(0) == reg.charAt(reg.length() - 1)) {
				if (reg.charAt(0) == '/') {
					reg = reg.substring(1, reg.length() - 1);
				} else if (reg.charAt(0) == '"' || reg.charAt(0) == '\'') {
					reg = reg.substring(1, reg.length() - 1);
					if (!(Pattern.compile("(^\\\\[^\\\\])|([^\\\\]\\\\[^\\\\])").matcher(reg).find()) && reg.contains("\\\\")) {
						reg = StringEscapeUtils.unescapeJava(reg);
					}
				}
			}
		}
		return reg;
	}
	
	/**
	 * Remove the flag chars of the regex
	 * @param reg
	 * @return
	 */
	public static String deleteFlag(String reg) {
		reg = nakeRegex(reg);
		if (reg != null && reg.length() > 1) {
			Pattern p = Pattern.compile("^/.+[^\\\\]/[a-zA-Z]+$");
			Matcher m = p.matcher(reg);
			if (m.find()) {
				p = Pattern.compile("[^\\\\]/[a-zA-Z]+$");
				m = p.matcher(reg);
				m.find();
				return reg.substring(1, m.start()+1);
			}
		}
		return reg;
	}
	
	/**
	 * For lazy usage
	 * @param regex
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static ReScuePattern formatRegex(String regex) throws PatternSyntaxException{
		return ReScuePattern.compile(deleteFlag(regex));
	}
	
	/**
	 * For general usage
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Regex Format Arguments Error.");
			return ;
		}
		try {
			if (args[0].equalsIgnoreCase("-format")) {
				List<String> regs = Files.readAllLines(new File(args[1]).toPath());
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(args[2])));
				for (String r : regs)
					bw.write(deleteFlag(r) + "\n");
				bw.close();
			} else if (args[0].equalsIgnoreCase("-deformat")) {
				List<String> regs = Files.readAllLines(new File(args[1]).toPath());
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(args[2])));
				for (String r : regs)
					bw.write("\"" + StringEscapeUtils.escapeJava(deleteFlag(r)) + "\"\n");
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
