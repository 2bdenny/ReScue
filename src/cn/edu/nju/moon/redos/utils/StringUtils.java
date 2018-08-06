package cn.edu.nju.moon.redos.utils;

import java.util.List;
import java.util.Random;

/**
 * Some methods of string operations are here
 */
public class StringUtils {
	public static final String lower = "abcdefghijklmnopqrstuvwxyz";
	public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String digit = "0123456789";
	public static final String punct = ":;<=>?@{|}~\b";
	public static final String hex = "0123456789ABCDEFabcdef";
	public static final String alpha = lower + upper;
	public static final String graph = punct + upper + lower + digit;
	public static final String word = lower + upper + digit;
	public static final String visibleChars = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
	public static final String escapeChars = "\t\n\b\r";
	public static final String spaceChars = " \t\r\n";
	public static final String allChars = lower + upper + digit + visibleChars + escapeChars + spaceChars;
	private static Random rnd = new Random();
	
	public static String randomInsertSlice(String str, List<String> slices) {
		if (rnd.nextInt(2) == 0) return str;
		
		if (slices.size() == 0) return str;
		else if (str.length() == 0){
			return slices.get(rnd.nextInt(slices.size()));
		} else {
			int pos = rnd.nextInt(str.length() + 1);
			if (pos == 0) return slices.get(rnd.nextInt(slices.size())) + str;
			else if (pos == str.length()) return str + slices.get(rnd.nextInt(slices.size()));
			else return str.substring(0, pos) + slices.get(rnd.nextInt(slices.size())) + str.substring(pos);
		}
	}
	
	public static String randomStringFromAlphabet(int len, String alphabet) {
		String result = "";
		int alen = alphabet.length();
		for (int i = 0; i < len; i ++) {
			result = result + alphabet.charAt(rnd.nextInt(alen));
		}
		return result;
	}
	
	/**
	 * Generate a random prefix
	 * @param prefixes Current tried prefixes
	 * @return
	 */
	public static String randPrefix(List<String> prefixes) {
		if (prefixes.size() < 1) return "";
		else {
			int index = rnd.nextInt(prefixes.size() * 2);
			if (index >= prefixes.size()) return "";
			else return prefixes.get(index);
		}
	}
}
