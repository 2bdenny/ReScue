package cn.edu.nju.moon.redos.utils;

import java.util.List;
import java.util.Stack;

import cn.edu.nju.moon.redos.regex.ReScuePattern.Node;

/**
 * Some operation of trace and log are placed here
 */
public class ReScuePatternUtils {    
    public static void printStack(Stack<List<Node>> stack) {
    	System.out.println("Stack size: " + stack.size());
    	for (List<Node> list : stack) {
    		for (Node n : list) System.out.print(n.toString().split("\\$")[1] + " ");
    		System.out.println();
    	}
    }
    
	/**
	 * Convert a code number to the string
	 * @param c
	 * @return
	 */
	public static String convertString(int c) {
		String result = "";
		char[] tmp = Character.toChars(c);
		result = result + (new String(tmp));
		return result;
	}
	
	/**
	 * Convert a code number to the string
	 * @param c
	 * @return
	 */
	public static String convertString(int[] c) {
		if (c == null) return "";
		else {
			String result = "";
			for (int tc : c) {
				char[] tmp = Character.toChars(tc);
				result = result + (new String(tmp));
			}
			return result;
		}
	}
}
