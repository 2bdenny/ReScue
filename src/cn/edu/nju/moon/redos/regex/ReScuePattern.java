package cn.edu.nju.moon.redos.regex;
/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * pGREAT is developed based on the {java.util.regex.Pattern}
 */

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import cn.edu.nju.moon.redos.Trace;
import cn.edu.nju.moon.redos.utils.ReScuePatternUtils;
import cn.edu.nju.moon.redos.utils.NodeRelation;
import cn.edu.nju.moon.redos.utils.RegexViewer;
import prefuse.data.Schema;
import prefuse.data.Table;

public final class ReScuePattern
    implements java.io.Serializable
{	
    /**
     * Enables Unix lines mode.
     *
     * <p> In this mode, only the <tt>'\n'</tt> line terminator is recognized
     * in the behavior of <tt>.</tt>, <tt>^</tt>, and <tt>$</tt>.
     *
     * <p> Unix lines mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?d)</tt>.
     */
    public static final int UNIX_LINES = 0x01;

    /**
     * Enables case-insensitive matching.
     *
     * <p> By default, case-insensitive matching assumes that only characters
     * in the US-ASCII charset are being matched.  Unicode-aware
     * case-insensitive matching can be enabled by specifying the {@link
     * #UNICODE_CASE} flag in conjunction with this flag.
     *
     * <p> Case-insensitive matching can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?i)</tt>.
     *
     * <p> Specifying this flag may impose a slight performance penalty.  </p>
     */
    public static final int CASE_INSENSITIVE = 0x02;

    /**
     * Permits whitespace and comments in pattern.
     *
     * <p> In this mode, whitespace is ignored, and embedded comments starting
     * with <tt>#</tt> are ignored until the end of a line.
     *
     * <p> Comments mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?x)</tt>.
     */
    public static final int COMMENTS = 0x04;

    /**
     * Enables multiline mode.
     *
     * <p> In multiline mode the expressions <tt>^</tt> and <tt>$</tt> match
     * just after or just before, respectively, a line terminator or the end of
     * the input sequence.  By default these expressions only match at the
     * beginning and the end of the entire input sequence.
     *
     * <p> Multiline mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?m)</tt>.  </p>
     */
    public static final int MULTILINE = 0x08;

    /**
     * Enables literal parsing of the pattern.
     *
     * <p> When this flag is specified then the input string that specifies
     * the pattern is treated as a sequence of literal characters.
     * Metacharacters or escape sequences in the input sequence will be
     * given no special meaning.
     *
     * <p>The flags CASE_INSENSITIVE and UNICODE_CASE retain their impact on
     * matching when used in conjunction with this flag. The other flags
     * become superfluous.
     *
     * <p> There is no embedded flag character for enabling literal parsing.
     * @since 1.5
     */
    public static final int LITERAL = 0x10;

    /**
     * Enables dotall mode.
     *
     * <p> In dotall mode, the expression <tt>.</tt> matches any character,
     * including a line terminator.  By default this expression does not match
     * line terminators.
     *
     * <p> Dotall mode can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?s)</tt>.  (The <tt>s</tt> is a mnemonic for
     * "single-line" mode, which is what this is called in Perl.)  </p>
     */
    public static final int DOTALL = 0x20;

    /**
     * Enables Unicode-aware case folding.
     *
     * <p> When this flag is specified then case-insensitive matching, when
     * enabled by the {@link #CASE_INSENSITIVE} flag, is done in a manner
     * consistent with the Unicode Standard.  By default, case-insensitive
     * matching assumes that only characters in the US-ASCII charset are being
     * matched.
     *
     * <p> Unicode-aware case folding can also be enabled via the embedded flag
     * expression&nbsp;<tt>(?u)</tt>.
     *
     * <p> Specifying this flag may impose a performance penalty.  </p>
     */
    public static final int UNICODE_CASE = 0x40;

    /**
     * Enables canonical equivalence.
     *
     * <p> When this flag is specified then two characters will be considered
     * to match if, and only if, their full canonical decompositions match.
     * The expression <tt>"a&#92;u030A"</tt>, for example, will match the
     * string <tt>"&#92;u00E5"</tt> when this flag is specified.  By default,
     * matching does not take canonical equivalence into account.
     *
     * <p> There is no embedded flag character for enabling canonical
     * equivalence.
     *
     * <p> Specifying this flag may impose a performance penalty.  </p>
     */
    public static final int CANON_EQ = 0x80;

    /**
     * Enables the Unicode version of <i>Predefined character classes</i> and
     * <i>POSIX character classes</i>.
     *
     * <p> When this flag is specified then the (US-ASCII only)
     * <i>Predefined character classes</i> and <i>POSIX character classes</i>
     * are in conformance with
     * <a href="http://www.unicode.org/reports/tr18/"><i>Unicode Technical
     * Standard #18: Unicode Regular Expression</i></a>
     * <i>Annex C: Compatibility Properties</i>.
     * <p>
     * The UNICODE_CHARACTER_CLASS mode can also be enabled via the embedded
     * flag expression&nbsp;<tt>(?U)</tt>.
     * <p>
     * The flag implies UNICODE_CASE, that is, it enables Unicode-aware case
     * folding.
     * <p>
     * Specifying this flag may impose a performance penalty.  </p>
     * @since 1.7
     */
    public static final int UNICODE_CHARACTER_CLASS = 0x100;

    /* Pattern has only two serialized components: The pattern string
     * and the flags, which are all that is needed to recompile the pattern
     * when it is deserialized.
     */

    /** use serialVersionUID from Merlin b59 for interoperability */
    private static final long serialVersionUID = 5073258162644648461L;

    /**
     * The original regular-expression pattern string.
     *
     * @serial
     */
    private String pattern;

    /**
     * The original pattern flags.
     *
     * @serial
     */
    private int flags;

    /**
     * Boolean indicating this Pattern is compiled; this is necessary in order
     * to lazily compile deserialized Patterns.
     */
    private transient volatile boolean compiled = false;

    /**
     * The normalized pattern string.
     */
    private transient String normalizedPattern;

    /**
     * The starting point of state machine for the find operation.  This allows
     * a match to start anywhere in the input.
     */
    // transient means not to serialize
    public transient Node root;

    /**
     * The root of object tree for a match operation.  The pattern is matched
     * at the beginning.  This may include a find that uses BnM or a First
     * node.
     */
    transient Node matchRoot;

    /**
     * Temporary storage used by parsing pattern slice.
     */
    transient int[] buffer;

    /**
     * Map the "name" of the "named capturing group" to its group id
     * node.
     */
    transient volatile Map<String, Integer> namedGroups;

    /**
     * Temporary storage used while parsing group references.
     */
    transient GroupHead[] groupNodes;

    /**
     * Temporary null terminated code point array used by pattern compiling.
     */
    private transient int[] temp;

    /**
     * The number of capturing groups in this Pattern. Used by matchers to
     * allocate storage needed to perform a match.
     */
    transient int capturingGroupCount;

    /**
     * The local variable count used by parsing tree. Used by matchers to
     * allocate storage needed to perform a match.
     */
    transient int localCount;

    /**
     * Index into the pattern string that keeps track of how much has been
     * parsed.
     */
    private transient int cursor;

    /**
     * Holds the length of the pattern string.
     */
    private transient int patternLength;

    /**
     * If the Start node might possibly match supplementary characters.
     * It is set to true during compiling if
     * (1) There is supplementary char in pattern, or
     * (2) There is complement node of Category or Block
     */
    private transient boolean hasSupplementary;    

	/**
     * Compiles the given regular expression into a pattern.
     *
     * @param  regex
     *         The expression to be compiled
     * @return the given regular expression compiled into a pattern
     * @throws  PatternSyntaxException
     *          If the expression's syntax is invalid
     */
    public static ReScuePattern compile(String regex) {
        return new ReScuePattern(regex, 0);
    }

    /**
     * Compiles the given regular expression into a pattern with the given
     * flags.
     *
     * @param  regex
     *         The expression to be compiled
     *
     * @param  flags
     *         Match flags, a bit mask that may include
     *         {@link #CASE_INSENSITIVE}, {@link #MULTILINE}, {@link #DOTALL},
     *         {@link #UNICODE_CASE}, {@link #CANON_EQ}, {@link #UNIX_LINES},
     *         {@link #LITERAL}, {@link #UNICODE_CHARACTER_CLASS}
     *         and {@link #COMMENTS}
     *
     * @return the given regular expression compiled into a pattern with the given flags
     * @throws  IllegalArgumentException
     *          If bit values other than those corresponding to the defined
     *          match flags are set in <tt>flags</tt>
     *
     * @throws  PatternSyntaxException
     *          If the expression's syntax is invalid
     */
    public static ReScuePattern compile(String regex, int flags) {
        return new ReScuePattern(regex, flags);
    }

    /**
     * Returns the regular expression from which this pattern was compiled.
     *
     * @return  The source of this pattern
     */
    public String pattern() {
        return pattern;
    }

    /**
     * <p>Returns the string representation of this pattern. This
     * is the regular expression from which this pattern was
     * compiled.</p>
     *
     * @return  The string representation of this pattern
     * @since 1.5
     */
    public String toString() {
        return pattern;
    }

    /**
     * Creates a matcher that will match the given input against this pattern.
     *
     * @param  input
     *         The character sequence to be matched
     *
     * @return  A new matcher for this pattern
     */
    public ReScueMatcher matcher(CharSequence input, Trace trace) {
        if (!compiled) {
            synchronized(this) {
                if (!compiled)
                    compile();
            }
        }
        ReScueMatcher m = new ReScueMatcher(this, input, trace);
        return m;
    }

    /**
     * Returns this pattern's match flags.
     *
     * @return  The match flags specified when this pattern was compiled
     */
    public int flags() {
        return flags;
    }

    /**
     * Compiles the given regular expression and attempts to match the given
     * input against it.
     *
     * <p> An invocation of this convenience method of the form
     *
     * <blockquote><pre>
     * Pattern.matches(regex, input);</pre></blockquote>
     *
     * behaves in exactly the same way as the expression
     *
     * <blockquote><pre>
     * Pattern.compile(regex).matcher(input).matches()</pre></blockquote>
     *
     * <p> If a pattern is to be used multiple times, compiling it once and reusing
     * it will be more efficient than invoking this method each time.  </p>
     *
     * @param  regex
     *         The expression to be compiled
     *
     * @param  input
     *         The character sequence to be matched
     * @return whether or not the regular expression matches on the input
     * @throws CatastrophicBacktrackingException 
     * @throws  PatternSyntaxException
     *          If the expression's syntax is invalid
     */
    public static boolean matches(String regex, CharSequence input, Trace trace){
        ReScuePattern p = ReScuePattern.compile(regex);
        ReScueMatcher m = p.matcher(input, trace);
        return m.matches();
    }

    /**
     * Splits the given input sequence around matches of this pattern.
     *
     * <p> The array returned by this method contains each substring of the
     * input sequence that is terminated by another subsequence that matches
     * this pattern or is terminated by the end of the input sequence.  The
     * substrings in the array are in the order in which they occur in the
     * input. If this pattern does not match any subsequence of the input then
     * the resulting array has just one element, namely the input sequence in
     * string form.
     *
     * <p> When there is a positive-width match at the beginning of the input
     * sequence then an empty leading substring is included at the beginning
     * of the resulting array. A zero-width match at the beginning however
     * never produces such empty leading substring.
     *
     * <p> The <tt>limit</tt> parameter controls the number of times the
     * pattern is applied and therefore affects the length of the resulting
     * array.  If the limit <i>n</i> is greater than zero then the pattern
     * will be applied at most <i>n</i>&nbsp;-&nbsp;1 times, the array's
     * length will be no greater than <i>n</i>, and the array's last entry
     * will contain all input beyond the last matched delimiter.  If <i>n</i>
     * is non-positive then the pattern will be applied as many times as
     * possible and the array can have any length.  If <i>n</i> is zero then
     * the pattern will be applied as many times as possible, the array can
     * have any length, and trailing empty strings will be discarded.
     *
     * <p> The input <tt>"boo:and:foo"</tt>, for example, yields the following
     * results with these parameters:
     *
     * <blockquote><table cellpadding=1 cellspacing=0
     *              summary="Split examples showing regex, limit, and result">
     * <tr><th align="left"><i>Regex&nbsp;&nbsp;&nbsp;&nbsp;</i></th>
     *     <th align="left"><i>Limit&nbsp;&nbsp;&nbsp;&nbsp;</i></th>
     *     <th align="left"><i>Result&nbsp;&nbsp;&nbsp;&nbsp;</i></th></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>2</td>
     *     <td><tt>{ "boo", "and:foo" }</tt></td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>5</td>
     *     <td><tt>{ "boo", "and", "foo" }</tt></td></tr>
     * <tr><td align=center>:</td>
     *     <td align=center>-2</td>
     *     <td><tt>{ "boo", "and", "foo" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>5</td>
     *     <td><tt>{ "b", "", ":and:f", "", "" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>-2</td>
     *     <td><tt>{ "b", "", ":and:f", "", "" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td align=center>0</td>
     *     <td><tt>{ "b", "", ":and:f" }</tt></td></tr>
     * </table></blockquote>
     *
     * @param  input
     *         The character sequence to be split
     *
     * @param  limit
     *         The result threshold, as described above
     *
     * @return  The array of strings computed by splitting the input
     *          around matches of this pattern
     * @throws CatastrophicBacktrackingException 
     */
    public String[] split(CharSequence input, int limit, Trace trace){
        int index = 0;
        boolean matchLimited = limit > 0;
        ArrayList<String> matchList = new ArrayList<>();
        ReScueMatcher m = matcher(input, trace);

        // Add segments before each match found
        while(m.find().matchSuccess) {
            if (!matchLimited || matchList.size() < limit - 1) {
                if (index == 0 && index == m.start() && m.start() == m.end()) {
                    // no empty leading substring included for zero-width match
                    // at the beginning of the input char sequence.
                    continue;
                }
                String match = input.subSequence(index, m.start()).toString();
                matchList.add(match);
                index = m.end();
            } else if (matchList.size() == limit - 1) { // last one
                String match = input.subSequence(index,
                                                 input.length()).toString();
                matchList.add(match);
                index = m.end();
            }
        }

        // If no match was found, return this
        if (index == 0)
            return new String[] {input.toString()};

        // Add remaining segment
        if (!matchLimited || matchList.size() < limit)
            matchList.add(input.subSequence(index, input.length()).toString());

        // Construct result
        int resultSize = matchList.size();
        if (limit == 0)
            while (resultSize > 0 && matchList.get(resultSize-1).equals(""))
                resultSize--;
        String[] result = new String[resultSize];
        return matchList.subList(0, resultSize).toArray(result);
    }

    /**
     * Splits the given input sequence around matches of this pattern.
     *
     * <p> This method works as if by invoking the two-argument {@link
     * #split(java.lang.CharSequence, int) split} method with the given input
     * sequence and a limit argument of zero.  Trailing empty strings are
     * therefore not included in the resulting array. </p>
     *
     * <p> The input <tt>"boo:and:foo"</tt>, for example, yields the following
     * results with these expressions:
     *
     * <blockquote><table cellpadding=1 cellspacing=0
     *              summary="Split examples showing regex and result">
     * <tr><th align="left"><i>Regex&nbsp;&nbsp;&nbsp;&nbsp;</i></th>
     *     <th align="left"><i>Result</i></th></tr>
     * <tr><td align=center>:</td>
     *     <td><tt>{ "boo", "and", "foo" }</tt></td></tr>
     * <tr><td align=center>o</td>
     *     <td><tt>{ "b", "", ":and:f" }</tt></td></tr>
     * </table></blockquote>
     *
     *
     * @param  input
     *         The character sequence to be split
     *
     * @return  The array of strings computed by splitting the input
     *          around matches of this pattern
     * @throws CatastrophicBacktrackingException 
     */
    public String[] split(CharSequence input, Trace trace){
        return split(input, 0, trace);
    }

    /**
     * Returns a literal pattern <code>String</code> for the specified
     * <code>String</code>.
     *
     * <p>This method produces a <code>String</code> that can be used to
     * create a <code>Pattern</code> that would match the string
     * <code>s</code> as if it were a literal pattern.</p> Metacharacters
     * or escape sequences in the input sequence will be given no special
     * meaning.
     *
     * @param  s The string to be literalized
     * @return  A literal string replacement
     * @since 1.5
     */
    public static String quote(String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1)
            return "\\Q" + s + "\\E";

        StringBuilder sb = new StringBuilder(s.length() * 2);
        sb.append("\\Q");
        slashEIndex = 0;
        int current = 0;
        while ((slashEIndex = s.indexOf("\\E", current)) != -1) {
            sb.append(s.substring(current, slashEIndex));
            current = slashEIndex + 2;
            sb.append("\\E\\\\E\\Q");
        }
        sb.append(s.substring(current, s.length()));
        sb.append("\\E");
        return sb.toString();
    }

    /**
     * Recompile the Pattern instance from a stream.  The original pattern
     * string is read in and the object tree is recompiled from it.
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.IOException, ClassNotFoundException {

        // Read in all fields
        s.defaultReadObject();

        // Initialize counts
        capturingGroupCount = 1;
        localCount = 0;

        // if length > 0, the Pattern is lazily compiled
        compiled = false;
        if (pattern.length() == 0) {
            root = new Start(lastAccept);
            matchRoot = lastAccept;
            compiled = true;
        }
    }

    /**
     * This private constructor is used to create all Patterns. The pattern
     * string and match flags are all that is needed to completely describe
     * a Pattern. An empty pattern string results in an object tree with
     * only a Start node and a LastNode node.
     */
    private ReScuePattern(String p, int f) {
        pattern = p;
        flags = f;

        // to use UNICODE_CASE if UNICODE_CHARACTER_CLASS present
        if ((flags & UNICODE_CHARACTER_CLASS) != 0)
            flags |= UNICODE_CASE;

        // Reset group index count
        capturingGroupCount = 1;
        localCount = 0;

        if (pattern.length() > 0) {
            compile();
        } else {
            root = new Start(lastAccept);
            matchRoot = lastAccept;
        }
    }

    /**
     * The pattern is converted to normalizedD form and then a pure group
     * is constructed to match canonical equivalences of the characters.
     */
    private void normalize() {
        boolean inCharClass = false;
        int lastCodePoint = -1;

        // Convert pattern into normalizedD form
        normalizedPattern = Normalizer.normalize(pattern, Normalizer.Form.NFD);
        patternLength = normalizedPattern.length();

        // Modify pattern to match canonical equivalences
        StringBuilder newPattern = new StringBuilder(patternLength);
        for(int i=0; i<patternLength; ) {
            int c = normalizedPattern.codePointAt(i);
            StringBuilder sequenceBuffer;
            if ((Character.getType(c) == Character.NON_SPACING_MARK)
                && (lastCodePoint != -1)) {
                sequenceBuffer = new StringBuilder();
                sequenceBuffer.appendCodePoint(lastCodePoint);
                sequenceBuffer.appendCodePoint(c);
                while(Character.getType(c) == Character.NON_SPACING_MARK) {
                    i += Character.charCount(c);
                    if (i >= patternLength)
                        break;
                    c = normalizedPattern.codePointAt(i);
                    sequenceBuffer.appendCodePoint(c);
                }
                String ea = produceEquivalentAlternation(
                                               sequenceBuffer.toString());
                newPattern.setLength(newPattern.length()-Character.charCount(lastCodePoint));
                newPattern.append("(?:").append(ea).append(")");
            } else if (c == '[' && lastCodePoint != '\\') {
                i = normalizeCharClass(newPattern, i);
            } else {
                newPattern.appendCodePoint(c);
            }
            lastCodePoint = c;
            i += Character.charCount(c);
        }
        normalizedPattern = newPattern.toString();
    }

    /**
     * Complete the character class being parsed and add a set
     * of alternations to it that will match the canonical equivalences
     * of the characters within the class.
     */
    private int normalizeCharClass(StringBuilder newPattern, int i) {
        StringBuilder charClass = new StringBuilder();
        StringBuilder eq = null;
        int lastCodePoint = -1;
        String result;

        i++;
        charClass.append("[");
        while(true) {
            int c = normalizedPattern.codePointAt(i);
            StringBuilder sequenceBuffer;

            if (c == ']' && lastCodePoint != '\\') {
                charClass.append((char)c);
                break;
            } else if (Character.getType(c) == Character.NON_SPACING_MARK) {
                sequenceBuffer = new StringBuilder();
                sequenceBuffer.appendCodePoint(lastCodePoint);
                while(Character.getType(c) == Character.NON_SPACING_MARK) {
                    sequenceBuffer.appendCodePoint(c);
                    i += Character.charCount(c);
                    if (i >= normalizedPattern.length())
                        break;
                    c = normalizedPattern.codePointAt(i);
                }
                String ea = produceEquivalentAlternation(
                                                  sequenceBuffer.toString());

                charClass.setLength(charClass.length()-Character.charCount(lastCodePoint));
                if (eq == null)
                    eq = new StringBuilder();
                eq.append('|');
                eq.append(ea);
            } else {
                charClass.appendCodePoint(c);
                i++;
            }
            if (i == normalizedPattern.length())
                throw error("Unclosed character class");
            lastCodePoint = c;
        }

        if (eq != null) {
            result = "(?:"+charClass.toString()+eq.toString()+")";
        } else {
            result = charClass.toString();
        }

        newPattern.append(result);
        return i;
    }

    /**
     * Given a specific sequence composed of a regular character and
     * combining marks that follow it, produce the alternation that will
     * match all canonical equivalences of that sequence.
     */
    private String produceEquivalentAlternation(String source) {
        int len = countChars(source, 0, 1);
        if (source.length() == len)
            // source has one character.
            return source;

        String base = source.substring(0,len);
        String combiningMarks = source.substring(len);

        String[] perms = producePermutations(combiningMarks);
        StringBuilder result = new StringBuilder(source);

        // Add combined permutations
        for(int x=0; x<perms.length; x++) {
            String next = base + perms[x];
            if (x>0)
                result.append("|"+next);
            next = composeOneStep(next);
            if (next != null)
                result.append("|"+produceEquivalentAlternation(next));
        }
        return result.toString();
    }

    /**
     * Returns an array of strings that have all the possible
     * permutations of the characters in the input string.
     * This is used to get a list of all possible orderings
     * of a set of combining marks. Note that some of the permutations
     * are invalid because of combining class collisions, and these
     * possibilities must be removed because they are not canonically
     * equivalent.
     */
    private String[] producePermutations(String input) {
        if (input.length() == countChars(input, 0, 1))
            return new String[] {input};

        if (input.length() == countChars(input, 0, 2)) {
            int c0 = Character.codePointAt(input, 0);
            int c1 = Character.codePointAt(input, Character.charCount(c0));
            if (getClass(c1) == getClass(c0)) {
                return new String[] {input};
            }
            String[] result = new String[2];
            result[0] = input;
            StringBuilder sb = new StringBuilder(2);
            sb.appendCodePoint(c1);
            sb.appendCodePoint(c0);
            result[1] = sb.toString();
            return result;
        }

        int length = 1;
        int nCodePoints = countCodePoints(input);
        for(int x=1; x<nCodePoints; x++)
            length = length * (x+1);

        String[] temp = new String[length];

        int combClass[] = new int[nCodePoints];
        for(int x=0, i=0; x<nCodePoints; x++) {
            int c = Character.codePointAt(input, i);
            combClass[x] = getClass(c);
            i +=  Character.charCount(c);
        }

        // For each char, take it out and add the permutations
        // of the remaining chars
        int index = 0;
        int len;
        // offset maintains the index in code units.
loop:   for(int x=0, offset=0; x<nCodePoints; x++, offset+=len) {
            len = countChars(input, offset, 1);
            boolean skip = false;
            for(int y=x-1; y>=0; y--) {
                if (combClass[y] == combClass[x]) {
                    continue loop;
                }
            }
            StringBuilder sb = new StringBuilder(input);
            String otherChars = sb.delete(offset, offset+len).toString();
            String[] subResult = producePermutations(otherChars);

            String prefix = input.substring(offset, offset+len);
            for(int y=0; y<subResult.length; y++)
                temp[index++] =  prefix + subResult[y];
        }
        String[] result = new String[index];
        for (int x=0; x<index; x++)
            result[x] = temp[x];
        return result;
    }

    private int getClass(int c) {
        return sun.text.Normalizer.getCombiningClass(c);
    }

    /**
     * Attempts to compose input by combining the first character
     * with the first combining mark following it. Returns a String
     * that is the composition of the leading character with its first
     * combining mark followed by the remaining combining marks. Returns
     * null if the first two characters cannot be further composed.
     */
    private String composeOneStep(String input) {
        int len = countChars(input, 0, 2);
        String firstTwoCharacters = input.substring(0, len);
        String result = Normalizer.normalize(firstTwoCharacters, Normalizer.Form.NFC);

        if (result.equals(firstTwoCharacters))
            return null;
        else {
            String remainder = input.substring(len);
            return result + remainder;
        }
    }

    /**
     * Preprocess any \Q...\E sequences in `temp', meta-quoting them.
     * See the description of `quotemeta' in perlfunc(1).
     */
    private void RemoveQEQuoting() {
        final int pLen = patternLength;
        int i = 0;
        while (i < pLen-1) {
            if (temp[i] != '\\')
                i += 1;
            else if (temp[i + 1] != 'Q')
                i += 2;
            else
                break;
        }
        if (i >= pLen - 1)    // No \Q sequence found
            return;
        int j = i;
        i += 2;
        int[] newtemp = new int[j + 3*(pLen-i) + 2];
        System.arraycopy(temp, 0, newtemp, 0, j);

        boolean inQuote = true;
        boolean beginQuote = true;
        while (i < pLen) {
            int c = temp[i++];
            if (!ASCII.isAscii(c) || ASCII.isAlpha(c)) {
                newtemp[j++] = c;
            } else if (ASCII.isDigit(c)) {
                if (beginQuote) {
                    /*
                     * A unicode escape \[0xu] could be before this quote,
                     * and we don't want this numeric char to processed as
                     * part of the escape.
                     */
                    newtemp[j++] = '\\';
                    newtemp[j++] = 'x';
                    newtemp[j++] = '3';
                }
                newtemp[j++] = c;
            } else if (c != '\\') {
                if (inQuote) newtemp[j++] = '\\';
                newtemp[j++] = c;
            } else if (inQuote) {
                if (temp[i] == 'E') {
                    i++;
                    inQuote = false;
                } else {
                    newtemp[j++] = '\\';
                    newtemp[j++] = '\\';
                }
            } else {
                if (temp[i] == 'Q') {
                    i++;
                    inQuote = true;
                    beginQuote = true;
                    continue;
                } else {
                    newtemp[j++] = c;
                    if (i != pLen)
                        newtemp[j++] = temp[i++];
                }
            }

            beginQuote = false;
        }

        patternLength = j;
        temp = Arrays.copyOf(newtemp, j + 2); // double zero termination
    }

    /**
     * Copies regular expression to an int array and invokes the parsing
     * of the expression which will create the object tree.
     */
    private void compile() {
        // Handle canonical equivalences
        if (has(CANON_EQ) && !has(LITERAL)) {
            normalize();
        } else {
            normalizedPattern = pattern;
        }
        patternLength = normalizedPattern.length();

        // Copy pattern to int array for convenience
        // Use double zero to terminate pattern
        temp = new int[patternLength + 2];

        hasSupplementary = false;
        int c, count = 0;
        // Convert all chars into code points
        for (int x = 0; x < patternLength; x += Character.charCount(c)) {
            c = normalizedPattern.codePointAt(x);
            if (isSupplementary(c)) {
                hasSupplementary = true;
            }
            temp[count++] = c;
        }

        patternLength = count;   // patternLength now in code points

        if (! has(LITERAL))
            RemoveQEQuoting();

        // Allocate all temporary objects here.
        buffer = new int[32];
        groupNodes = new GroupHead[10];
        namedGroups = null;

        if (has(LITERAL)) {
            // Literal pattern handling
            matchRoot = newSlice(temp, patternLength, hasSupplementary);
            matchRoot.next = lastAccept;
        } else {
            // Start recursive descent parsing
            matchRoot = expr(lastAccept);
            // Check extra pattern characters
            if (patternLength != cursor) {
                if (peek() == ')') {
                    throw error("Unmatched closing ')'");
                } else {
                    throw error("Unexpected internal error");
                }
            }
        }

        // Peephole optimization
        if (matchRoot instanceof Slice) {
            root = BnM.optimize(matchRoot);
            if (root == matchRoot) {
                root = hasSupplementary ? new StartS(matchRoot) : new Start(matchRoot);
            }
        } else if (matchRoot instanceof Begin || matchRoot instanceof First) {
            root = matchRoot;
        } else {
            root = hasSupplementary ? new StartS(matchRoot) : new Start(matchRoot);
        }

        // Release temporary storage
        temp = null;
        buffer = null;
        groupNodes = null;
        patternLength = 0;
        compiled = true;
    }

    Map<String, Integer> namedGroups() {
        if (namedGroups == null)
            namedGroups = new HashMap<>(2);
        return namedGroups;
    }

    /**
     * Used to print out a subtree of the Pattern to help with debugging.
     */
    public static void printObjectTree(Node node) {
        while(node != null) {
            if (node instanceof Prolog) {
                System.out.println(node);
                printObjectTree(((Prolog)node).loop);
                System.out.println("**** end contents prolog loop");
            } else if (node instanceof Loop) {
                System.out.println(node);
                printObjectTree(((Loop)node).body);
                System.out.println("**** end contents Loop body");
            } else if (node instanceof Curly) {
                System.out.println(node);
                printObjectTree(((Curly)node).atom);
                System.out.println("**** end contents Curly body");
            } else if (node instanceof GroupCurly) {
                System.out.println(node);
                printObjectTree(((GroupCurly)node).atom);
                System.out.println("**** end contents GroupCurly body");
            } else if (node instanceof GroupTail) {
                System.out.println(node);
                System.out.println("Tail next is "+node.next);
                return;
            } else {
                System.out.println(node);
            }
            node = node.next;
            if (node != null)
                System.out.println("->next:");
            if (node == ReScuePattern.accept) {
                System.out.println("Accept Node");
                node = null;
            }
       }
    }

    /**
     * Used to accumulate information about a subtree of the object graph
     * so that optimizations can be applied to the subtree.
     */
    static final class TreeInfo {
        int minLength;
        int maxLength;
        boolean maxValid;
        boolean deterministic;

        TreeInfo() {
            reset();
        }
        void reset() {
            minLength = 0;
            maxLength = 0;
            maxValid = true;
            deterministic = true;
        }
    }

    /*
     * The following private methods are mainly used to improve the
     * readability of the code. In order to let the Java compiler easily
     * inline them, we should not put many assertions or error checks in them.
     */

    /**
     * Indicates whether a particular flag is set or not.
     */
    private boolean has(int f) {
        return (flags & f) != 0;
    }

    /**
     * Match next character, signal error if failed.
     */
    private void accept(int ch, String s) {
        int testChar = temp[cursor++];
        if (has(COMMENTS))
            testChar = parsePastWhitespace(testChar);
        if (ch != testChar) {
            throw error(s);
        }
    }

    /**
     * Mark the end of pattern with a specific character.
     */
    private void mark(int c) {
        temp[patternLength] = c;
    }

    /**
     * Peek the next character, and do not advance the cursor.
     */
    private int peek() {
        int ch = temp[cursor];
        if (has(COMMENTS))
            ch = peekPastWhitespace(ch);
        return ch;
    }

    /**
     * Read the next character, and advance the cursor by one.
     */
    private int read() {
        int ch = temp[cursor++];
        if (has(COMMENTS))
            ch = parsePastWhitespace(ch);
        return ch;
    }

    /**
     * Read the next character, and advance the cursor by one,
     * ignoring the COMMENTS setting
     */
    private int readEscaped() {
        int ch = temp[cursor++];
        return ch;
    }

    /**
     * Advance the cursor by one, and peek the next character.
     */
    private int next() {
        int ch = temp[++cursor];
        if (has(COMMENTS))
            ch = peekPastWhitespace(ch);
        return ch;
    }

    /**
     * Advance the cursor by one, and peek the next character,
     * ignoring the COMMENTS setting
     */
    private int nextEscaped() {
        int ch = temp[++cursor];
        return ch;
    }

    /**
     * If in xmode peek past whitespace and comments.
     */
    private int peekPastWhitespace(int ch) {
        while (ASCII.isSpace(ch) || ch == '#') {
            while (ASCII.isSpace(ch))
                ch = temp[++cursor];
            if (ch == '#') {
                ch = peekPastLine();
            }
        }
        return ch;
    }

    /**
     * If in xmode parse past whitespace and comments.
     */
    private int parsePastWhitespace(int ch) {
        while (ASCII.isSpace(ch) || ch == '#') {
            while (ASCII.isSpace(ch))
                ch = temp[cursor++];
            if (ch == '#')
                ch = parsePastLine();
        }
        return ch;
    }

    /**
     * xmode parse past comment to end of line.
     */
    private int parsePastLine() {
        int ch = temp[cursor++];
        while (ch != 0 && !isLineSeparator(ch))
            ch = temp[cursor++];
        return ch;
    }

    /**
     * xmode peek past comment to end of line.
     */
    private int peekPastLine() {
        int ch = temp[++cursor];
        while (ch != 0 && !isLineSeparator(ch))
            ch = temp[++cursor];
        return ch;
    }

    /**
     * Determines if character is a line separator in the current mode
     */
    private boolean isLineSeparator(int ch) {
        if (has(UNIX_LINES)) {
            return ch == '\n';
        } else {
            return (ch == '\n' ||
                    ch == '\r' ||
                    (ch|1) == '\u2029' ||
                    ch == '\u0085');
        }
    }

    /**
     * Read the character after the next one, and advance the cursor by two.
     */
    private int skip() {
        int i = cursor;
        int ch = temp[i+1];
        cursor = i + 2;
        return ch;
    }

    /**
     * Unread one next character, and retreat cursor by one.
     */
    private void unread() {
        cursor--;
    }

    /**
     * Internal method used for handling all syntax errors. The pattern is
     * displayed with a pointer to aid in locating the syntax error.
     */
    private PatternSyntaxException error(String s) {
        return new PatternSyntaxException(s, normalizedPattern,  cursor - 1);
    }

    /**
     * Determines if there is any supplementary character or unpaired
     * surrogate in the specified range.
     */
    private boolean findSupplementary(int start, int end) {
        for (int i = start; i < end; i++) {
            if (isSupplementary(temp[i]))
                return true;
        }
        return false;
    }

    /**
     * Determines if the specified code point is a supplementary
     * character or unpaired surrogate.
     */
    private static final boolean isSupplementary(int ch) {
        return ch >= Character.MIN_SUPPLEMENTARY_CODE_POINT ||
               Character.isSurrogate((char)ch);
    }

    /**
     *  The following methods handle the main parsing. They are sorted
     *  according to their precedence order, the lowest one first.
     */

    /**
     * The expression is parsed with branch nodes added for alternations.
     * This may be called recursively to parse sub expressions that may
     * contain alternations.
     */
    private Node expr(Node end) {
        Node prev = null;
        Node firstTail = null;
        Branch branch = null;
        Node branchConn = null;

        for (;;) {
            Node node = sequence(end);
            Node nodeTail = root;      //double return
            if (prev == null) {
                prev = node;
                firstTail = nodeTail;
            } else {
                // Branch
                if (branchConn == null) {
                    branchConn = new BranchConn("BranchEnd");
                    branchConn.next = end;
                }
                if (node == end) {
                    // if the node returned from sequence() is "end"
                    // we have an empty expr, set a null atom into
                    // the branch to indicate to go "next" directly.
                    node = null;
                } else {
                    // the "tail.next" of each atom goes to branchConn
                    nodeTail.next = branchConn;
                }
                if (prev == branch) {
                    branch.add(node);
                } else {
                    if (prev == end) {
                        prev = null;
                    } else {
                        // replace the "end" with "branchConn" at its tail.next
                        // when put the "prev" into the branch as the first atom.
                        firstTail.next = branchConn;
                    }
                    prev = branch = new Branch(prev, node, branchConn, "|");
                }
            }
            if (peek() != '|') {
                return prev;
            }
            next();
        }
    }

    @SuppressWarnings("fallthrough")
    /**
     * Parsing of sequences between alternations.
     */
    private Node sequence(Node end) {
        Node head = null;
        Node tail = null;
        Node node = null;
    LOOP:
        for (;;) {
            int ch = peek();
            switch (ch) {
            case '(':
                // Because group handles its own closure,
                // we need to treat it differently
                node = group0();
                // Check for comment or flag group
                if (node == null)
                    continue;
                if (head == null)
                    head = node;
                else
                    tail.next = node;
                // Double return: Tail was returned in root
                tail = root;
                continue;
            case '[':
                node = clazz(true);
                break;
            case '\\':
                ch = nextEscaped();
                if (ch == 'p' || ch == 'P') {
                    boolean oneLetter = true;
                    boolean comp = (ch == 'P');
                    ch = next(); // Consume { if present
                    if (ch != '{') {
                        unread();
                    } else {
                        oneLetter = false;
                    }
                    node = family(oneLetter, comp);
                } else {
                    unread();
                    node = atom();
                }
                break;
            case '^':
                next();
                if (has(MULTILINE)) {
                    if (has(UNIX_LINES))
                        node = new UnixCaret("^");
                    else
                        node = new Caret("^");
                } else {
                    node = new Begin("^");
                }
                break;
            case '$':
                next();
                if (has(UNIX_LINES))
                    node = new UnixDollar(has(MULTILINE), "$");
                else
                    node = new Dollar(has(MULTILINE), "$");
                break;
            case '.':
                next();
                if (has(DOTALL)) {
                    node = new All(".");
                } else {
                    if (has(UNIX_LINES))
                        node = new UnixDot(".");
                    else {
                        node = new Dot(".");
                    }
                }
                break;
            case '|':
            case ')':
                break LOOP;
            case ']': // Now interpreting dangling ] and } as literals
            case '}':
                node = atom();
                break;
            case '?':
            case '*':
            case '+':
                next();
                throw error("Dangling meta character '" + ((char)ch) + "'");
            case 0:
                if (cursor >= patternLength) {
                    break LOOP;
                }
                // Fall through
            default:
                node = atom();
                break;
            }

            node = closure(node);

            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
        }
        if (head == null) {
            return end;
        }
        tail.next = end;
        root = tail;      //double return
        return head;
    }

    @SuppressWarnings("fallthrough")
    /**
     * Parse and add a new Single or Slice.
     */
    private Node atom() {
        int first = 0;
        int prev = -1;
        boolean hasSupplementary = false;
        int ch = peek();
        for (;;) {
            switch (ch) {
            case '*':
            case '+':
            case '?':
            case '{':
                if (first > 1) {
                    cursor = prev;    // Unwind one character
                    first--;
                }
                break;
            case '$':
            case '.':
            case '^':
            case '(':
            case '[':
            case '|':
            case ')':
                break;
            case '\\':
                ch = nextEscaped();
                if (ch == 'p' || ch == 'P') { // Property
                    if (first > 0) { // Slice is waiting; handle it first
                        unread();
                        break;
                    } else { // No slice; just return the family node
                        boolean comp = (ch == 'P');
                        boolean oneLetter = true;
                        ch = next(); // Consume { if present
                        if (ch != '{')
                            unread();
                        else
                            oneLetter = false;
                        return family(oneLetter, comp);
                    }
                }
                unread();
                prev = cursor;
                ch = escape(false, first == 0, false);
                if (ch >= 0) {
                    append(ch, first);
                    first++;
                    if (isSupplementary(ch)) {
                        hasSupplementary = true;
                    }
                    ch = peek();
                    continue;
                } else if (first == 0) {
                    return root;
                }
                // Unwind meta escape sequence
                cursor = prev;
                break;
            case 0:
                if (cursor >= patternLength) {
                    break;
                }
                // Fall through
            default:
                prev = cursor;
                append(ch, first);
                first++;
                if (isSupplementary(ch)) {
                    hasSupplementary = true;
                }
                ch = next();
                continue;
            }
            break;
        }
        if (first == 1) {
            return newSingle(buffer[0]);
        } else {
            return newSlice(buffer, first, hasSupplementary);
        }
    }

    private void append(int ch, int len) {
        if (len >= buffer.length) {
            int[] tmp = new int[len+len];
            System.arraycopy(buffer, 0, tmp, 0, len);
            buffer = tmp;
        }
        buffer[len] = ch;
    }

    /**
     * Parses a backref greedily, taking as many numbers as it
     * can. The first digit is always treated as a backref, but
     * multi digit numbers are only treated as a backref if at
     * least that many backrefs exist at this point in the regex.
     */
    private Node ref(int refNum) {
        boolean done = false;
        while(!done) {
            int ch = peek();
            switch(ch) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                int newRefNum = (refNum * 10) + (ch - '0');
                // Add another number if it doesn't make a group
                // that doesn't exist
                if (capturingGroupCount - 1 < newRefNum) {
                    done = true;
                    break;
                }
                refNum = newRefNum;
                read();
                break;
            default:
                done = true;
                break;
            }
        }
        if (has(CASE_INSENSITIVE))
            return new CIBackRef(refNum, has(UNICODE_CASE), "\\" + refNum);
        else
            return new BackRef(refNum, "\\" + refNum);
    }

    /**
     * Parses an escape sequence to determine the actual value that needs
     * to be matched.
     * If -1 is returned and create was true a new object was added to the tree
     * to handle the escape sequence.
     * If the returned value is greater than zero, it is the value that
     * matches the escape sequence.
     */
    private int escape(boolean inclass, boolean create, boolean isrange) {
        int ch = skip();
        switch (ch) {
        case '0':
            return o();
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            if (inclass) break;
            if (create) {
                root = ref((ch - '0'));
            }
            return -1;
        case 'A':
            if (inclass) break;
            if (create) root = new Begin("\\A");
            return -1;
        case 'B':
            if (inclass) break;
            if (create) root = new Bound(Bound.NONE, has(UNICODE_CHARACTER_CLASS), "\\B");
            return -1;
        case 'C':
            break;
        case 'D':
            if (create) root = has(UNICODE_CHARACTER_CLASS)
                               ? new Utype(UnicodeProp.DIGIT).complement()
                               : new Ctype(ASCII.DIGIT).complement();
            return -1;
        case 'E':
        case 'F':
            break;
        case 'G':
            if (inclass) break;
            if (create) root = new LastMatch("\\G");
            return -1;
        case 'H':
            if (create) root = new HorizWS("\\H").complement();
            return -1;
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
            break;
        case 'R':
            if (inclass) break;
            if (create) root = new LineEnding("\\R");
            return -1;
        case 'S':
            if (create) root = has(UNICODE_CHARACTER_CLASS)
                               ? new Utype(UnicodeProp.WHITE_SPACE).complement()
                               : new Ctype(ASCII.SPACE).complement();
            return -1;
        case 'T':
        case 'U':
            break;
        case 'V':
            if (create) root = new VertWS("\\V").complement();
            return -1;
        case 'W':
            if (create) root = has(UNICODE_CHARACTER_CLASS)
                               ? new Utype(UnicodeProp.WORD).complement()
                               : new Ctype(ASCII.WORD).complement();
            return -1;
        case 'X':
        case 'Y':
            break;
        case 'Z':
            if (inclass) break;
            if (create) {
                if (has(UNIX_LINES))
                    root = new UnixDollar(false, "\\Z");
                else
                    root = new Dollar(false, "\\Z");
            }
            return -1;
        case 'a':
            return '\007';
        case 'b':
            if (inclass) break;
            if (create) root = new Bound(Bound.BOTH, has(UNICODE_CHARACTER_CLASS), "\\b");
            return -1;
        case 'c':
            return c();
        case 'd':
            if (create) root = has(UNICODE_CHARACTER_CLASS)
                               ? new Utype(UnicodeProp.DIGIT)
                               : new Ctype(ASCII.DIGIT);
            return -1;
        case 'e':
            return '\033';
        case 'f':
            return '\f';
        case 'g':
            break;
        case 'h':
            if (create) root = new HorizWS("\\h");
            return -1;
        case 'i':
        case 'j':
            break;
        case 'k':
            if (inclass)
                break;
            if (read() != '<')
                throw error("\\k is not followed by '<' for named capturing group");
            String name = groupname(read());
            if (!namedGroups().containsKey(name))
                throw error("(named capturing group <"+ name+"> does not exit");
            if (create) {
                if (has(CASE_INSENSITIVE))
                    root = new CIBackRef(namedGroups().get(name), has(UNICODE_CASE), "\\" + namedGroups().get(name));
                else
                    root = new BackRef(namedGroups().get(name), "\\" + namedGroups().get(name));
            }
            return -1;
        case 'l':
        case 'm':
            break;
        case 'n':
            return '\n';
        case 'o':
        case 'p':
        case 'q':
            break;
        case 'r':
            return '\r';
        case 's':
            if (create) root = has(UNICODE_CHARACTER_CLASS)
                               ? new Utype(UnicodeProp.WHITE_SPACE)
                               : new Ctype(ASCII.SPACE);
            return -1;
        case 't':
            return '\t';
        case 'u':
            return u();
        case 'v':
            // '\v' was implemented as VT/0x0B in releases < 1.8 (though
            // undocumented). In JDK8 '\v' is specified as a predefined
            // character class for all vertical whitespace characters.
            // So [-1, root=VertWS node] pair is returned (instead of a
            // single 0x0B). This breaks the range if '\v' is used as
            // the start or end value, such as [\v-...] or [...-\v], in
            // which a single definite value (0x0B) is expected. For
            // compatibility concern '\013'/0x0B is returned if isrange.
            if (isrange)
                return '\013';
            if (create) root = new VertWS("\\v");
            return -1;
        case 'w':
            if (create) root = has(UNICODE_CHARACTER_CLASS)
                               ? new Utype(UnicodeProp.WORD)
                               : new Ctype(ASCII.WORD);
            return -1;
        case 'x':
            return x();
        case 'y':
            break;
        case 'z':
            if (inclass) break;
            if (create) root = new End("\\z");
            return -1;
        default:
            return ch;
        }
        throw error("Illegal/unsupported escape sequence");
    }

    /**
     * Parse a character class, and return the node that matches it.
     *
     * Consumes a ] on the way out if consume is true. Usually consume
     * is true except for the case of [abc&&def] where def is a separate
     * right hand node with "understood" brackets.
     */
    private CharProperty clazz(boolean consume) {
        CharProperty prev = null;
        CharProperty node = null;
        BitClass bits = new BitClass("");
        boolean include = true;
        boolean firstInClass = true;
        int ch = next();
        for (;;) {
            switch (ch) {
                case '^':
                    // Negates if first char in a class, otherwise literal
                    if (firstInClass) {
                        if (temp[cursor-1] != '[')
                            break;
                        ch = next();
                        include = !include;
                        continue;
                    } else {
                        // ^ not first in class, treat as literal
                        break;
                    }
                case '[':
                    firstInClass = false;
                    node = clazz(true);
                    if (prev == null)
                        prev = node;
                    else
                        prev = union(prev, node);
                    ch = peek();
                    continue;
                case '&':
                    firstInClass = false;
                    ch = next();
                    if (ch == '&') {
                        ch = next();
                        CharProperty rightNode = null;
                        while (ch != ']' && ch != '&') {
                            if (ch == '[') {
                                if (rightNode == null)
                                    rightNode = clazz(true);
                                else
                                    rightNode = union(rightNode, clazz(true));
                            } else { // abc&&def
                                unread();
                                rightNode = clazz(false);
                            }
                            ch = peek();
                        }
                        if (rightNode != null)
                            node = rightNode;
                        if (prev == null) {
                            if (rightNode == null)
                                throw error("Bad class syntax");
                            else
                                prev = rightNode;
                        } else {
                            prev = intersection(prev, node);
                        }
                    } else {
                        // treat as a literal &
                        unread();
                        break;
                    }
                    continue;
                case 0:
                    firstInClass = false;
                    if (cursor >= patternLength)
                        throw error("Unclosed character class");
                    break;
                case ']':
                    firstInClass = false;
                    if (prev != null) {
                        if (consume)
                            next();
                        return prev;
                    }
                    break;
                default:
                    firstInClass = false;
                    break;
            }
            node = range(bits);
            if (include) {
                if (prev == null) {
                    prev = node;
                } else {
                    if (prev != node)
                        prev = union(prev, node);
                }
            } else {
                if (prev == null) {
                    prev = node.complement();
                } else {
                    if (prev != node)
                        prev = setDifference(prev, node);
                }
            }
            ch = peek();
        }
    }

    private CharProperty bitsOrSingle(BitClass bits, int ch) {
        /* Bits can only handle codepoints in [u+0000-u+00ff] range.
           Use "single" node instead of bits when dealing with unicode
           case folding for codepoints listed below.
           (1)Uppercase out of range: u+00ff, u+00b5
              toUpperCase(u+00ff) -> u+0178
              toUpperCase(u+00b5) -> u+039c
           (2)LatinSmallLetterLongS u+17f
              toUpperCase(u+017f) -> u+0053
           (3)LatinSmallLetterDotlessI u+131
              toUpperCase(u+0131) -> u+0049
           (4)LatinCapitalLetterIWithDotAbove u+0130
              toLowerCase(u+0130) -> u+0069
           (5)KelvinSign u+212a
              toLowerCase(u+212a) ==> u+006B
           (6)AngstromSign u+212b
              toLowerCase(u+212b) ==> u+00e5
        */
        int d;
        if (ch < 256 &&
            !(has(CASE_INSENSITIVE) && has(UNICODE_CASE) &&
              (ch == 0xff || ch == 0xb5 ||
               ch == 0x49 || ch == 0x69 ||  //I and i
               ch == 0x53 || ch == 0x73 ||  //S and s
               ch == 0x4b || ch == 0x6b ||  //K and k
               ch == 0xc5 || ch == 0xe5)))  //A+ring
            return bits.add(ch, flags());
        return newSingle(ch);
    }

    /**
     * Parse a single character or a character range in a character class
     * and return its representative node.
     */
    private CharProperty range(BitClass bits) {
        int ch = peek();
        if (ch == '\\') {
            ch = nextEscaped();
            if (ch == 'p' || ch == 'P') { // A property
                boolean comp = (ch == 'P');
                boolean oneLetter = true;
                // Consume { if present
                ch = next();
                if (ch != '{')
                    unread();
                else
                    oneLetter = false;
                return family(oneLetter, comp);
            } else { // ordinary escape
                boolean isrange = temp[cursor+1] == '-';
                unread();
                ch = escape(true, true, isrange);
                if (ch == -1)
                    return (CharProperty) root;
            }
        } else {
            next();
        }
        if (ch >= 0) {
            if (peek() == '-') {
                int endRange = temp[cursor+1];
                if (endRange == '[') {
                    return bitsOrSingle(bits, ch);
                }
                if (endRange != ']') {
                    next();
                    int m = peek();
                    if (m == '\\') {
                        m = escape(true, false, true);
                    } else {
                        next();
                    }
                    if (m < ch) {
                        throw error("Illegal character range");
                    }
                    if (has(CASE_INSENSITIVE))
                        return caseInsensitiveRangeFor(ch, m);
                    else
                        return rangeFor(ch, m);
                }
            }
            return bitsOrSingle(bits, ch);
        }
        throw error("Unexpected character '"+((char)ch)+"'");
    }

    /**
     * Parses a Unicode character family and returns its representative node.
     */
    private CharProperty family(boolean singleLetter,
                                boolean maybeComplement)
    {
        next();
        String name;
        CharProperty node = null;

        if (singleLetter) {
            int c = temp[cursor];
            if (!Character.isSupplementaryCodePoint(c)) {
                name = String.valueOf((char)c);
            } else {
                name = new String(temp, cursor, 1);
            }
            read();
        } else {
            int i = cursor;
            mark('}');
            while(read() != '}') {
            }
            mark('\000');
            int j = cursor;
            if (j > patternLength)
                throw error("Unclosed character family");
            if (i + 1 >= j)
                throw error("Empty character family");
            name = new String(temp, i, j-i-1);
        }

        int i = name.indexOf('=');
        if (i != -1) {
            // property construct \p{name=value}
            String value = name.substring(i + 1);
            name = name.substring(0, i).toLowerCase(Locale.ENGLISH);
            if ("sc".equals(name) || "script".equals(name)) {
                node = unicodeScriptPropertyFor(value);
            } else if ("blk".equals(name) || "block".equals(name)) {
                node = unicodeBlockPropertyFor(value);
            } else if ("gc".equals(name) || "general_category".equals(name)) {
                node = charPropertyNodeFor(value);
            } else {
                throw error("Unknown Unicode property {name=<" + name + ">, "
                             + "value=<" + value + ">}");
            }
        } else {
            if (name.startsWith("In")) {
                // \p{inBlockName}
                node = unicodeBlockPropertyFor(name.substring(2));
            } else if (name.startsWith("Is")) {
                // \p{isGeneralCategory} and \p{isScriptName}
                name = name.substring(2);
                UnicodeProp uprop = UnicodeProp.forName(name);
                if (uprop != null)
                    node = new Utype(uprop);
                if (node == null)
                    node = CharPropertyNames.charPropertyFor(name);
                if (node == null)
                    node = unicodeScriptPropertyFor(name);
            } else {
                if (has(UNICODE_CHARACTER_CLASS)) {
                    UnicodeProp uprop = UnicodeProp.forPOSIXName(name);
                    if (uprop != null)
                        node = new Utype(uprop);
                }
                if (node == null)
                    node = charPropertyNodeFor(name);
            }
        }
        if (maybeComplement) {
            if (node instanceof Category || node instanceof Block)
                hasSupplementary = true;
            node = node.complement();
        }
        return node;
    }


    /**
     * Returns a CharProperty matching all characters belong to
     * a UnicodeScript.
     */
    private CharProperty unicodeScriptPropertyFor(String name) {
        final Character.UnicodeScript script;
        try {
            script = Character.UnicodeScript.forName(name);
        } catch (IllegalArgumentException iae) {
            throw error("Unknown character script name {" + name + "}");
        }
        return new Script(script);
    }

    /**
     * Returns a CharProperty matching all characters in a UnicodeBlock.
     */
    private CharProperty unicodeBlockPropertyFor(String name) {
        final Character.UnicodeBlock block;
        try {
            block = Character.UnicodeBlock.forName(name);
        } catch (IllegalArgumentException iae) {
            throw error("Unknown character block name {" + name + "}");
        }
        return new Block(block);
    }

    /**
     * Returns a CharProperty matching all characters in a named property.
     */
    private CharProperty charPropertyNodeFor(String name) {
        CharProperty p = CharPropertyNames.charPropertyFor(name);
        if (p == null)
            throw error("Unknown character property name {" + name + "}");
        return p;
    }

    /**
     * Parses and returns the name of a "named capturing group", the trailing
     * ">" is consumed after parsing.
     */
    private String groupname(int ch) {
        StringBuilder sb = new StringBuilder();
        sb.append(Character.toChars(ch));
        while (ASCII.isLower(ch=read()) || ASCII.isUpper(ch) ||
               ASCII.isDigit(ch)) {
            sb.append(Character.toChars(ch));
        }
        if (sb.length() == 0)
            throw error("named capturing group has 0 length name");
        if (ch != '>')
            throw error("named capturing group is missing trailing '>'");
        return sb.toString();
    }

    /**
     * Parses a group and returns the head node of a set of nodes that process
     * the group. Sometimes a double return system is used where the tail is
     * returned in root.
     */
    private Node group0() {
        boolean capturingGroup = false;
        Node head = null;
        Node tail = null;
        int save = flags;
        root = null;
        int ch = next();
        if (ch == '?') {
            ch = skip();
            switch (ch) {
            case ':':   //  (?:xxx) pure group
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                break;
            case '=':   // (?=xxx) and (?!xxx) lookahead
            case '!':
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                if (ch == '=') {
                    head = tail = new Pos(head, "(?=xxx)");
                } else {
                    head = tail = new Neg(head, "(?!xxx)");
                }
                break;
            case '>':   // (?>xxx)  independent group
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                head = tail = new Ques(head, INDEPENDENT, "(?>xxx)");
                break;
            case '<':   // (?<xxx)  look behind
                ch = read();
                if (ASCII.isLower(ch) || ASCII.isUpper(ch)) {
                    // named captured group
                    String name = groupname(ch);
                    if (namedGroups().containsKey(name))
                        throw error("Named capturing group <" + name
                                    + "> is already defined");
                    capturingGroup = true;
                    head = createGroup(false);
                    tail = root;
                    namedGroups().put(name, capturingGroupCount-1);
                    head.next = expr(tail);
                    break;
                }
                int start = cursor;
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                tail.next = lookbehindEnd;
                TreeInfo info = new TreeInfo();
                head.study(info);
                if (info.maxValid == false) {
                    throw error("Look-behind group does not have "
                                + "an obvious maximum length");
                }
                boolean hasSupplementary = findSupplementary(start, patternLength);
                if (ch == '=') {
                    head = tail = (hasSupplementary ?
                                   new BehindS(head, info.maxLength,
                                               info.minLength, "(<=xxx)") :
                                   new Behind(head, info.maxLength,
                                              info.minLength, "(<=xxx)"));
                } else if (ch == '!') {
                    head = tail = (hasSupplementary ?
                                   new NotBehindS(head, info.maxLength,
                                                  info.minLength, "(<!xxx)") :
                                   new NotBehind(head, info.maxLength,
                                                 info.minLength, "(<!xxx)"));
                } else {
                    throw error("Unknown look-behind group");
                }
                break;
            case '$':
            case '@':
                throw error("Unknown group type");
            default:    // (?xxx:) inlined match flags
                unread();
                addFlag();
                ch = read();
                if (ch == ')') {
                    return null;    // Inline modifier only
                }
                if (ch != ':') {
                    throw error("Unknown inline modifier");
                }
                head = createGroup(true);
                tail = root;
                head.next = expr(tail);
                break;
            }
        } else { // (xxx) a regular group
            capturingGroup = true;
            head = createGroup(false);
            tail = root;
            head.next = expr(tail);
        }

        accept(')', "Unclosed group");
        flags = save;

        // Check for quantifiers
        Node node = closure(head);
        if (node == head) { // No closure
            root = tail;
            return node;    // Dual return
        }
        if (head == tail) { // Zero length assertion
            root = node;
            return node;    // Dual return
        }

        if (node instanceof Ques) {
            Ques ques = (Ques) node;
            if (ques.type == POSSESSIVE) {
                root = node;
                return node;
            }
            tail.next = new BranchConn("BranchEnd");
            tail = tail.next;
            if (ques.type == GREEDY) {
                head = new Branch(head, null, tail, "?");
            } else { // Reluctant quantifier
                head = new Branch(null, head, tail, "?Lazy");
            }
            root = tail;
            return head;
        } else if (node instanceof Curly) {
            Curly curly = (Curly) node;
            if (curly.type == POSSESSIVE) {
                root = node;
                return node;
            }
            // Discover if the group is deterministic
            TreeInfo info = new TreeInfo();
            if (head.study(info)) { // Deterministic
                GroupTail temp = (GroupTail) tail;
                String self_str = head == null ? curly.self : "GroupHead: (\n" + curly.self;
                head = root = new GroupCurly(head.next, curly.cmin,
                                   curly.cmax, curly.type,
                                   ((GroupTail)tail).localIndex,
                                   ((GroupTail)tail).groupIndex,
//                                             capturingGroup, "(){" + curly.cmin + "," + curly.cmax + "}");
                						     capturingGroup, self_str);
                return head;
            } else { // Non-deterministic
                int temp = ((GroupHead) head).localIndex;
                Loop loop;
                if (curly.type == GREEDY)
                    loop = new Loop(this.localCount, temp, "Loop");
                else  // Reluctant Curly
                    loop = new LazyLoop(this.localCount, temp, "LoopLazy");
                Prolog prolog = new Prolog(loop);
                this.localCount += 1;
                loop.cmin = curly.cmin;
                loop.cmax = curly.cmax;
                loop.body = head;
                tail.next = loop;
                root = loop;
                return prolog; // Dual return
            }
        }
        throw error("Internal logic error");
    }

    /**
     * Create group head and tail nodes using double return. If the group is
     * created with anonymous true then it is a pure group and should not
     * affect group counting.
     */
    private Node createGroup(boolean anonymous) {
        int localIndex = localCount++;
        int groupIndex = 0;
        if (!anonymous)
            groupIndex = capturingGroupCount++;
        GroupHead head = new GroupHead(localIndex, "(");
        root = new GroupTail(localIndex, groupIndex, ")");
        if (!anonymous && groupIndex < 10)
            groupNodes[groupIndex] = head;
        return head;
    }

    @SuppressWarnings("fallthrough")
    /**
     * Parses inlined match flags and set them appropriately.
     */
    private void addFlag() {
        int ch = peek();
        for (;;) {
            switch (ch) {
            case 'i':
                flags |= CASE_INSENSITIVE;
                break;
            case 'm':
                flags |= MULTILINE;
                break;
            case 's':
                flags |= DOTALL;
                break;
            case 'd':
                flags |= UNIX_LINES;
                break;
            case 'u':
                flags |= UNICODE_CASE;
                break;
            case 'c':
                flags |= CANON_EQ;
                break;
            case 'x':
                flags |= COMMENTS;
                break;
            case 'U':
                flags |= (UNICODE_CHARACTER_CLASS | UNICODE_CASE);
                break;
            case '-': // subFlag then fall through
                ch = next();
                subFlag();
            default:
                return;
            }
            ch = next();
        }
    }

    @SuppressWarnings("fallthrough")
    /**
     * Parses the second part of inlined match flags and turns off
     * flags appropriately.
     */
    private void subFlag() {
        int ch = peek();
        for (;;) {
            switch (ch) {
            case 'i':
                flags &= ~CASE_INSENSITIVE;
                break;
            case 'm':
                flags &= ~MULTILINE;
                break;
            case 's':
                flags &= ~DOTALL;
                break;
            case 'd':
                flags &= ~UNIX_LINES;
                break;
            case 'u':
                flags &= ~UNICODE_CASE;
                break;
            case 'c':
                flags &= ~CANON_EQ;
                break;
            case 'x':
                flags &= ~COMMENTS;
                break;
            case 'U':
                flags &= ~(UNICODE_CHARACTER_CLASS | UNICODE_CASE);
            default:
                return;
            }
            ch = next();
        }
    }

    static final int MAX_REPS   = 0x7FFFFFFF;

    static final int GREEDY     = 0;

    static final int LAZY       = 1;

    static final int POSSESSIVE = 2;

    static final int INDEPENDENT = 3;

    /**
     * Processes repetition. If the next character peeked is a quantifier
     * then new nodes must be appended to handle the repetition.
     * Prev could be a single or a group, so it could be a chain of nodes.
     */
    private Node closure(Node prev) {
        Node atom;
        int ch = peek();
        switch (ch) {
        case '?':
            ch = next();
            if (ch == '?') {
                next();
                return new Ques(prev, LAZY, "?Lazy");
            } else if (ch == '+') {
                next();
                return new Ques(prev, POSSESSIVE, "?Poss");
            }
            return new Ques(prev, GREEDY, "?");
        case '*':
            ch = next();
            if (ch == '?') {
                next();
                return new Curly(prev, 0, MAX_REPS, LAZY, "*Lazy");
            } else if (ch == '+') {
                next();
                return new Curly(prev, 0, MAX_REPS, POSSESSIVE, "*Poss");
            }
            return new Curly(prev, 0, MAX_REPS, GREEDY, "*");
        case '+':
            ch = next();
            if (ch == '?') {
                next();
                return new Curly(prev, 1, MAX_REPS, LAZY, "+Lazy");
            } else if (ch == '+') {
                next();
                return new Curly(prev, 1, MAX_REPS, POSSESSIVE, "+Poss");
            }
            return new Curly(prev, 1, MAX_REPS, GREEDY, "+");
        case '{':
            ch = temp[cursor+1];
            if (ASCII.isDigit(ch)) {
                skip();
                int cmin = 0;
                do {
                    cmin = cmin * 10 + (ch - '0');
                } while (ASCII.isDigit(ch = read()));
                int cmax = cmin;
                if (ch == ',') {
                    ch = read();
                    cmax = MAX_REPS;
                    if (ch != '}') {
                        cmax = 0;
                        while (ASCII.isDigit(ch)) {
                            cmax = cmax * 10 + (ch - '0');
                            ch = read();
                        }
                    }
                }
                if (ch != '}')
                    throw error("Unclosed counted closure");
                if (((cmin) | (cmax) | (cmax - cmin)) < 0)
                    throw error("Illegal repetition range");
                Curly curly;
                ch = peek();
                if (ch == '?') {
                    next();
                    curly = new Curly(prev, cmin, cmax, LAZY, "{" + cmin + "," + cmax + "}Lazy");
                } else if (ch == '+') {
                    next();
                    curly = new Curly(prev, cmin, cmax, POSSESSIVE, "{" + cmin + "," + cmax + "}Poss");
                } else {
                    curly = new Curly(prev, cmin, cmax, GREEDY, "{" + cmin + "," + cmax + "}");
                }
                return curly;
            } else {
                throw error("Illegal repetition");
            }
        default:
            return prev;
        }
    }

    /**
     *  Utility method for parsing control escape sequences.
     */
    private int c() {
        if (cursor < patternLength) {
            return read() ^ 64;
        }
        throw error("Illegal control escape sequence");
    }

    /**
     *  Utility method for parsing octal escape sequences.
     */
    private int o() {
        int n = read();
        if (((n-'0')|('7'-n)) >= 0) {
            int m = read();
            if (((m-'0')|('7'-m)) >= 0) {
                int o = read();
                if ((((o-'0')|('7'-o)) >= 0) && (((n-'0')|('3'-n)) >= 0)) {
                    return (n - '0') * 64 + (m - '0') * 8 + (o - '0');
                }
                unread();
                return (n - '0') * 8 + (m - '0');
            }
            unread();
            return (n - '0');
        }
        throw error("Illegal octal escape sequence");
    }

    /**
     *  Utility method for parsing hexadecimal escape sequences.
     */
    private int x() {
        int n = read();
        if (ASCII.isHexDigit(n)) {
            int m = read();
            if (ASCII.isHexDigit(m)) {
                return ASCII.toDigit(n) * 16 + ASCII.toDigit(m);
            }
        } else if (n == '{' && ASCII.isHexDigit(peek())) {
            int ch = 0;
            while (ASCII.isHexDigit(n = read())) {
                ch = (ch << 4) + ASCII.toDigit(n);
                if (ch > Character.MAX_CODE_POINT)
                    throw error("Hexadecimal codepoint is too big");
            }
            if (n != '}')
                throw error("Unclosed hexadecimal escape sequence");
            return ch;
        }
        throw error("Illegal hexadecimal escape sequence");
    }

    /**
     *  Utility method for parsing unicode escape sequences.
     */
    private int cursor() {
        return cursor;
    }

    private void setcursor(int pos) {
        cursor = pos;
    }

    private int uxxxx() {
        int n = 0;
        for (int i = 0; i < 4; i++) {
            int ch = read();
            if (!ASCII.isHexDigit(ch)) {
                throw error("Illegal Unicode escape sequence");
            }
            n = n * 16 + ASCII.toDigit(ch);
        }
        return n;
    }

    private int u() {
        int n = uxxxx();
        if (Character.isHighSurrogate((char)n)) {
            int cur = cursor();
            if (read() == '\\' && read() == 'u') {
                int n2 = uxxxx();
                if (Character.isLowSurrogate((char)n2))
                    return Character.toCodePoint((char)n, (char)n2);
            }
            setcursor(cur);
        }
        return n;
    }

    //
    // Utility methods for code point support
    //

    private static final int countChars(CharSequence seq, int index,
                                        int lengthInCodePoints) {
        // optimization
        if (lengthInCodePoints == 1 && !Character.isHighSurrogate(seq.charAt(index))) {
            assert (index >= 0 && index < seq.length());
            return 1;
        }
        int length = seq.length();
        int x = index;
        if (lengthInCodePoints >= 0) {
            assert (index >= 0 && index < length);
            for (int i = 0; x < length && i < lengthInCodePoints; i++) {
                if (Character.isHighSurrogate(seq.charAt(x++))) {
                    if (x < length && Character.isLowSurrogate(seq.charAt(x))) {
                        x++;
                    }
                }
            }
            return x - index;
        }

        assert (index >= 0 && index <= length);
        if (index == 0) {
            return 0;
        }
        int len = -lengthInCodePoints;
        for (int i = 0; x > 0 && i < len; i++) {
            if (Character.isLowSurrogate(seq.charAt(--x))) {
                if (x > 0 && Character.isHighSurrogate(seq.charAt(x-1))) {
                    x--;
                }
            }
        }
        return index - x;
    }

    private static final int countCodePoints(CharSequence seq) {
        int length = seq.length();
        int n = 0;
        for (int i = 0; i < length; ) {
            n++;
            if (Character.isHighSurrogate(seq.charAt(i++))) {
                if (i < length && Character.isLowSurrogate(seq.charAt(i))) {
                    i++;
                }
            }
        }
        return n;
    }

    /**
     *  Creates a bit vector for matching Latin-1 values. A normal BitClass
     *  never matches values above Latin-1, and a complemented BitClass always
     *  matches values above Latin-1.
     */
    private static final class BitClass extends BmpCharProperty {
        final boolean[] bits;
        BitClass(String self) { super(self); bits = new boolean[256]; }
        private BitClass(boolean[] bits, String self) { super(self); this.bits = bits; }
        BitClass add(int c, int flags) {
            assert c >= 0 && c <= 255;
            if ((flags & CASE_INSENSITIVE) != 0) {
                if (ASCII.isAscii(c)) {
                    bits[ASCII.toUpper(c)] = true;
                    bits[ASCII.toLower(c)] = true;
                    
                    this.self = self + (char) (ASCII.toUpper(c));
                    this.self = self + (char) (ASCII.toLower(c));
                } else if ((flags & UNICODE_CASE) != 0) {
                    bits[Character.toLowerCase(c)] = true;
                    bits[Character.toUpperCase(c)] = true;
                    
                    this.self = self + (char) (ASCII.toUpper(c));
                    this.self = self + (char) (ASCII.toLower(c));
                }
            }
            bits[c] = true;
            this.self = self + (char) c;
            return this;
        }
        boolean isSatisfiedBy(int ch) {
            return ch < 256 && bits[ch];
        }
    }

    /**
     *  Returns a suitably optimized, single character matcher.
     */
    private CharProperty newSingle(final int ch) {
        if (has(CASE_INSENSITIVE)) {
            int lower, upper;
            if (has(UNICODE_CASE)) {
                upper = Character.toUpperCase(ch);
                lower = Character.toLowerCase(upper);
                if (upper != lower)
                    return new SingleU(lower);
            } else if (ASCII.isAscii(ch)) {
                lower = ASCII.toLower(ch);
                upper = ASCII.toUpper(ch);
                if (lower != upper)
                    return new SingleI(lower, upper);
            }
        }
        if (isSupplementary(ch))
            return new SingleS(ch);    // Match a given Unicode character
        return new Single(ch);         // Match a given BMP character
    }

    /**
     *  Utility method for creating a string slice matcher.
     */
    private Node newSlice(int[] buf, int count, boolean hasSupplementary) {
        int[] tmp = new int[count];
        if (has(CASE_INSENSITIVE)) {
            if (has(UNICODE_CASE)) {
                for (int i = 0; i < count; i++) {
                    tmp[i] = Character.toLowerCase(
                                 Character.toUpperCase(buf[i]));
                }
                return hasSupplementary? new SliceUS(tmp) : new SliceU(tmp);
            }
            for (int i = 0; i < count; i++) {
                tmp[i] = ASCII.toLower(buf[i]);
            }
            return hasSupplementary? new SliceIS(tmp) : new SliceI(tmp);
        }
        for (int i = 0; i < count; i++) {
            tmp[i] = buf[i];
        }
        return hasSupplementary ? new SliceS(tmp) : new Slice(tmp);
    }

    /**
     * The following classes are the building components of the object
     * tree that represents a compiled regular expression. The object tree
     * is made of individual elements that handle constructs in the Pattern.
     * Each type of object knows how to match its equivalent construct with
     * the match() method.
     */

    /**
     * Base class for all node classes. Subclasses should override the match()
     * method as appropriate. This class is an accepting node, so its match()
     * always returns true.
     * 
     * @pGREAT instrument this class to provide main functionality
     */
    public static class Node extends Object {
    	// For which string (or char) in the regex, create this node
    	String self;
    	
    	// All possible next node, these vars are used to generate regex graph
        Node next;
        Node next_self; // Prepare for start without ^ regex's pump-along
        Node atom; // Curly
        Node atom_self;
        Node body; // Prolog
        Node[] atoms; // Branch
        Node conn; // BranchEnd
        GroupHead head; // GroupRef
        Loop loop; // Prolog
        Node cond, yes, not; // Conditions
        
    	CharProperty lhs; // This 2 vars lead to memory cost
    	CharProperty rhs; // But deprecated, because these 2 will not be called match and not occur in stack
        
        Node(String self) {
        	this.self = self;
            next = ReScuePattern.accept;
        }
        /**
         * This method implements the classic accept node.
         */
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            matcher.last = i;
            matcher.groups[0] = matcher.first;
            matcher.groups[1] = matcher.last;
            return true;
        }
        
        boolean match(ReScueMatcher matcher, int i, CharSequence seq, boolean isTraced){
			if (isTraced) {
				if (trace.logMatch(this, i)) {
					return this.match(matcher, i, seq);
				}
				else return false;
			} else return this.match(matcher, i, seq);
        }
        /**
         * This method is good for all zero length assertions.
         */
        boolean study(TreeInfo info) {
            if (next != null) {
                return next.study(info);
            } else {
                return info.deterministic;
            }
        }
    }

    static class LastNode extends Node {
        LastNode(String self) {
			super(self);
		}

		/**
         * This method implements the classic accept node with
         * the addition of a check to see if the match occurred
         * using all of the input.
         */
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (matcher.acceptMode == ReScueMatcher.ENDANCHOR && i != matcher.to)
                return false;
            matcher.last = i;
            matcher.groups[0] = matcher.first;
            matcher.groups[1] = matcher.last;
            return true;
        }
    }

    /**
     * Used for REs that can start anywhere within the input string.
     * This basically tries to match repeatedly at each spot in the
     * input string, moving forward after each try. An anchored search
     * or a BnM will bypass this node completely.
     */
    static class Start extends Node {    	
        int minLength;
        Start(Node node) {
        	super("");
            this.next = node;
            TreeInfo info = new TreeInfo();
            next.study(info);
            minLength = info.minLength;
            
            if (this.next != null) this.next.next_self = this.next;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (i > matcher.to - minLength) {
                matcher.hitEnd = true;
                return false;
            }
            int guard = matcher.to - minLength;
            for (; i <= guard; i++) {
                if (next.match(matcher, i, seq, true)) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
            }
            matcher.hitEnd = true;
            return false;
        }
        boolean study(TreeInfo info) {
            next.study(info);
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /*
     * StartS supports supplementary characters, including unpaired surrogates.
     */
    static final class StartS extends Start {
        StartS(Node node) {
            super(node);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (i > matcher.to - minLength) {
                matcher.hitEnd = true;
                return false;
            }
            int guard = matcher.to - minLength;
            while (i <= guard) {
                //if ((ret = next.match(matcher, i, seq)) || i == guard)
                if (next.match(matcher, i, seq, true)) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
                if (i == guard)
                    break;
                // Optimization to move to the next character. This is
                // faster than countChars(seq, i, 1).
                if (Character.isHighSurrogate(seq.charAt(i++))) {
                    if (i < seq.length() &&
                        Character.isLowSurrogate(seq.charAt(i))) {
                        i++;
                    }
                }
            }
            matcher.hitEnd = true;
            return false;
        }
    }

    /**
     * Node to anchor at the beginning of input. This object implements the
     * match for a \A sequence, and the caret anchor will use this if not in
     * multiline mode.
     */
    static final class Begin extends Node {
        Begin(String self) {
			super(self);
		}

		boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int fromIndex = (matcher.anchoringBounds) ?
                matcher.from : 0;
            if (i == fromIndex && next.match(matcher, i, seq, true)) {
                matcher.first = i;
                matcher.groups[0] = i;
                matcher.groups[1] = matcher.last;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Node to anchor at the end of input. This is the absolute end, so this
     * should not match at the last newline before the end as $ will.
     */
    static final class End extends Node {
        End(String self) {
			super(self);
		}

		boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int endIndex = (matcher.anchoringBounds) ?
                matcher.to : matcher.getTextLength();
            if (i == endIndex) {
                matcher.hitEnd = true;
                return next.match(matcher, i, seq, true);
            }
            return false;
        }
    }

    /**
     * Node to anchor at the beginning of a line. This is essentially the
     * object to match for the multiline ^.
     */
    static final class Caret extends Node {
        Caret(String self) {
			super(self);
		}

		boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int startIndex = matcher.from;
            int endIndex = matcher.to;
            if (!matcher.anchoringBounds) {
                startIndex = 0;
                endIndex = matcher.getTextLength();
            }
            // Perl does not match ^ at end of input even after newline
            if (i == endIndex) {
                matcher.hitEnd = true;
                return false;
            }                      
            if (i > startIndex) {
                char ch = seq.charAt(i-1);
                if (ch != '\n' && ch != '\r'
                    && (ch|1) != '\u2029'
                    && ch != '\u0085' ) {
                    return false;
                }
                // Should treat /r/n as one newline
                if (ch == '\r' && seq.charAt(i) == '\n')
                    return false;
            }
            return next.match(matcher, i, seq, true);
        }
    }

    /**
     * Node to anchor at the beginning of a line when in unixdot mode.
     */
    static final class UnixCaret extends Node {
        UnixCaret(String self) {
			super(self);
		}

		boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int startIndex = matcher.from;
            int endIndex = matcher.to;
            if (!matcher.anchoringBounds) {
                startIndex = 0;
                endIndex = matcher.getTextLength();
            }
            // Perl does not match ^ at end of input even after newline
            if (i == endIndex) {
                matcher.hitEnd = true;
                return false;
            }
            if (i > startIndex) {
                char ch = seq.charAt(i-1);
                if (ch != '\n') {
                    return false;
                }
            }
            return next.match(matcher, i, seq, true);
        }
    }

    /**
     * Node to match the location where the last match ended.
     * This is used for the \G construct.
     */
    static final class LastMatch extends Node {
        LastMatch(String self) {
			super(self);
		}

		boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (i != matcher.oldLast)
                return false;
            return next.match(matcher, i, seq, true);
        }
    }

    /**
     * Node to anchor at the end of a line or the end of input based on the
     * multiline mode.
     *
     * When not in multiline mode, the $ can only match at the very end
     * of the input, unless the input ends in a line terminator in which
     * it matches right before the last line terminator.
     *
     * Note that \r\n is considered an atomic line terminator.
     *
     * Like ^ the $ operator matches at a position, it does not match the
     * line terminators themselves.
     */
    static final class Dollar extends Node {
        boolean multiline;
        Dollar(boolean mul, String self) {
        	super(self);
            multiline = mul;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int endIndex = (matcher.anchoringBounds) ?
                matcher.to : matcher.getTextLength();
            if (!multiline) {
                if (i < endIndex - 2)
                    return false;
                if (i == endIndex - 2) {
                    char ch = seq.charAt(i);
                    if (ch != '\r')
                        return false;
                    ch = seq.charAt(i + 1);
                    if (ch != '\n')
                        return false;
                }
            }
            // Matches before any line terminator; also matches at the
            // end of input
            // Before line terminator:
            // If multiline, we match here no matter what
            // If not multiline, fall through so that the end
            // is marked as hit; this must be a /r/n or a /n
            // at the very end so the end was hit; more input
            // could make this not match here
            if (i < endIndex) {
                char ch = seq.charAt(i);
                 if (ch == '\n') {
                     // No match between \r\n
                     if (i > 0 && seq.charAt(i-1) == '\r')
                         return false;
                     if (multiline)
                         return next.match(matcher, i, seq, true);
                 } else if (ch == '\r' || ch == '\u0085' ||
                            (ch|1) == '\u2029') {
                     if (multiline)
                         return next.match(matcher, i, seq, true);
                 } else { // No line terminator, no match
                     return false;
                 }
            }
            // Matched at current end so hit end
            matcher.hitEnd = true;
            // If a $ matches because of end of input, then more input
            // could cause it to fail!
            matcher.requireEnd = true;
            return next.match(matcher, i, seq, true);
        }
        boolean study(TreeInfo info) {
            next.study(info);
            return info.deterministic;
        }
    }

    /**
     * Node to anchor at the end of a line or the end of input based on the
     * multiline mode when in unix lines mode.
     */
    static final class UnixDollar extends Node {
        boolean multiline;
        UnixDollar(boolean mul, String self) {
        	super(self);
            multiline = mul;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int endIndex = (matcher.anchoringBounds) ?
                matcher.to : matcher.getTextLength();
            if (i < endIndex) {
                char ch = seq.charAt(i);
                if (ch == '\n') {
                    // If not multiline, then only possible to
                    // match at very end or one before end
                    if (multiline == false && i != endIndex - 1)
                        return false;
                    // If multiline return next.match without setting
                    // matcher.hitEnd
                    if (multiline)
                        return next.match(matcher, i, seq, true);
                } else {
                    return false;
                }
            }
            // Matching because at the end or 1 before the end;
            // more input could change this so set hitEnd
            matcher.hitEnd = true;
            // If a $ matches because of end of input, then more input
            // could cause it to fail!
            matcher.requireEnd = true;
            return next.match(matcher, i, seq, true);
        }
        boolean study(TreeInfo info) {
            next.study(info);
            return info.deterministic;
        }
    }

    /**
     * Node class that matches a Unicode line ending '\R'
     */
    static final class LineEnding extends Node {
        LineEnding(String self) {
			super(self);
		}
		boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            // (u+000Du+000A|[u+000Au+000Bu+000Cu+000Du+0085u+2028u+2029])
            if (i < matcher.to) {
                int ch = seq.charAt(i);
                if (ch == 0x0A || ch == 0x0B || ch == 0x0C ||
                    ch == 0x85 || ch == 0x2028 || ch == 0x2029)
                    return next.match(matcher, i + 1, seq, true);
                if (ch == 0x0D) {
                    i++;
                    if (i < matcher.to && seq.charAt(i) == 0x0A)
                        i++;
                    return next.match(matcher, i, seq, true);
                }
            } else {
                matcher.hitEnd = true;
            }
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength += 2;
            return next.study(info);
        }
    }

    /**
     * Abstract node class to match one character satisfying some
     * boolean property.
     */
    private static abstract class CharProperty extends Node {
    	public CharProperty(String self){
    		super(self);
    	}
    	public CharProperty(CharProperty lhs, CharProperty rhs) {
    		super(lhs == null ? rhs.self : (rhs == null ? lhs.self : lhs.self + rhs.self));
    		this.lhs = lhs;
    		this.rhs = rhs;
    	}
    	
        abstract boolean isSatisfiedBy(int ch);
        CharProperty complement() {
            return new CharProperty(self) {
                    boolean isSatisfiedBy(int ch) {
                        return ! CharProperty.this.isSatisfiedBy(ch);}};
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (i < matcher.to) {
                int ch = Character.codePointAt(seq, i);
                return isSatisfiedBy(ch)
                    && next.match(matcher, i+Character.charCount(ch), seq, true);
            } else {
                matcher.hitEnd = true;
                return false;
            }
        }
        boolean study(TreeInfo info) {
            info.minLength++;
            info.maxLength++;
            return next.study(info);
        }
//        public String getGroupElements() {
//        	String result = "";
//        	try {
//				for (int i = this.lower; i <= this.upper; i++) {
//					char[] tmp = Character.toChars(i);
//		        	result = result + (new String(tmp));
//				}
//			} catch (NoSuchFieldException | SecurityException e) {
//				e.printStackTrace();
//			}
//        }
    }

    /**
     * Optimized version of CharProperty that works only for
     * properties never satisfied by Supplementary characters.
     */
    private static abstract class BmpCharProperty extends CharProperty {
        BmpCharProperty(String self) {
			super(self);
		}

		boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (i < matcher.to) {
                return isSatisfiedBy(seq.charAt(i))
                    && next.match(matcher, i+1, seq, true);
            } else {
                matcher.hitEnd = true;
                return false;
            }
        }
    }

    /**
     * Node class that matches a Supplementary Unicode character
     */
    static final class SingleS extends CharProperty {
        final int c;
        SingleS(int c) {
        	super(ReScuePatternUtils.convertString(c));
        	this.c = c;
        }
        boolean isSatisfiedBy(int ch) {
            return ch == c;
        }
        
        public String getSliceBuffer(){
        	String result = "";
        	char[] tmp = Character.toChars(c);
        	result = result + (new String(tmp));
        	return result;
        }
    }

    /**
     * Optimization -- matches a given BMP character
     */
    static final class Single extends BmpCharProperty {
        final int c;
        Single(int c) {
        	super(ReScuePatternUtils.convertString(c));
        	this.c = c;
        }
        boolean isSatisfiedBy(int ch) {
            return ch == c;
        }
        
        public String getSliceBuffer(){
        	String result = "";
        	char[] tmp = Character.toChars(c);
        	result = result + (new String(tmp));
        	return result;
        }
    }

    /**
     * Case insensitive matches a given BMP character
     */
    static final class SingleI extends BmpCharProperty {
        final int lower;
        final int upper;
        SingleI(int lower, int upper) {
        	super(ReScuePatternUtils.convertString(upper));
            this.lower = lower;
            this.upper = upper;
        }
        boolean isSatisfiedBy(int ch) {
            return ch == lower || ch == upper;
        }
        
        public String getSliceBuffer(){
        	String result = "";
        	char[] tmp = Character.toChars(lower);
        	result = result + (new String(tmp));
        	return result;
        }
    }

    /**
     * Unicode case insensitive matches a given Unicode character
     */
    static final class SingleU extends CharProperty {
        final int lower;
        SingleU(int lower) {
        	super(ReScuePatternUtils.convertString(lower));
            this.lower = lower;
        }
        boolean isSatisfiedBy(int ch) {
            return lower == ch ||
                lower == Character.toLowerCase(Character.toUpperCase(ch));
        }
        
        public String getSliceBuffer(){
        	String result = "";
        	char[] tmp = Character.toChars(lower);
        	result = result + (new String(tmp));
        	return result;
        }
    }

    /**
     * Node class that matches a Unicode block.
     */
    static final class Block extends CharProperty {
        final Character.UnicodeBlock block;
        Block(Character.UnicodeBlock block) {
        	super(block.toString());
            this.block = block;
        }
        boolean isSatisfiedBy(int ch) {
            return block == Character.UnicodeBlock.of(ch);
        }
    }

    /**
     * Node class that matches a Unicode script
     */
    static final class Script extends CharProperty {
        final Character.UnicodeScript script;
        Script(Character.UnicodeScript script) {
        	super(script.name());
            this.script = script;
        }
        boolean isSatisfiedBy(int ch) {
            return script == Character.UnicodeScript.of(ch);
        }
    }

    /**
     * Node class that matches a Unicode category.
     */
    static final class Category extends CharProperty {
        final int typeMask;
        Category(int typeMask) {
        	super("CategoryMask: " + typeMask);
        	this.typeMask = typeMask;
        }
        boolean isSatisfiedBy(int ch) {
            return (typeMask & (1 << Character.getType(ch))) != 0;
        }
    }

    /**
     * Node class that matches a Unicode "type"
     */
    static final class Utype extends CharProperty {
        final UnicodeProp uprop;
        Utype(UnicodeProp uprop) {
        	super(uprop.name());
        	this.uprop = uprop;
        }
        boolean isSatisfiedBy(int ch) {
            return uprop.is(ch);
        }
    }

    /**
     * Node class that matches a POSIX type.
     */
    static final class Ctype extends BmpCharProperty {
        final int ctype;
        Ctype(int ctype) { super("POSIX type: " + ctype); this.ctype = ctype;}
        boolean isSatisfiedBy(int ch) {
            return ch < 128 && ASCII.isType(ch, ctype);
        }
    }

    /**
     * Node class that matches a Perl vertical whitespace
     */
    static final class VertWS extends BmpCharProperty {
        VertWS(String self) {
			super(self);
		}

		boolean isSatisfiedBy(int cp) {
            return (cp >= 0x0A && cp <= 0x0D) ||
                   cp == 0x85 || cp == 0x2028 || cp == 0x2029;
        }
    }

    /**
     * Node class that matches a Perl horizontal whitespace
     */
    static final class HorizWS extends BmpCharProperty {
        HorizWS(String self) {
			super(self);
		}

		boolean isSatisfiedBy(int cp) {
            return cp == 0x09 || cp == 0x20 || cp == 0xa0 ||
                   cp == 0x1680 || cp == 0x180e ||
                   cp >= 0x2000 && cp <= 0x200a ||
                   cp == 0x202f || cp == 0x205f || cp == 0x3000;
        }
    }

    /**
     * Base class for all Slice nodes
     */
    static class SliceNode extends Node {
        int[] buffer;
        SliceNode(int[] buf) {
        	super(ReScuePatternUtils.convertString(buf));
            buffer = buf;
        }
        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxLength += buffer.length;
            return next.study(info);
        }
        
        /**
         * 杩欎釜鍑芥暟鐢ㄤ簬灏唅nt鍖栫殑unicode瀛楃杞洖string
         * @return slice鐨刡uffer杞殑unicode瀛楃涓�
         */
        public String getSliceBuffer(){
        	String result = "";
        	for (int b : buffer) {
        		char[] tmp = Character.toChars(b);
        		result = result + (new String(tmp));
        	}
        	return result;
        }
    }

    /**
     * Node class for a case sensitive/BMP-only sequence of literal
     * characters.
     */
    static final class Slice extends SliceNode {
        Slice(int[] buf) {
            super(buf);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int[] buf = buffer;
            int len = buf.length;
            for (int j=0; j<len; j++) {
                if ((i+j) >= matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                if (buf[j] != seq.charAt(i+j))
                    return false;
            }
            return next.match(matcher, i+len, seq, true);
        }
    }

    /**
     * Node class for a case_insensitive/BMP-only sequence of literal
     * characters.
     */
    static class SliceI extends SliceNode {
        SliceI(int[] buf) {
            super(buf);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int[] buf = buffer;
            int len = buf.length;
            for (int j=0; j<len; j++) {
                if ((i+j) >= matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                int c = seq.charAt(i+j);
                if (buf[j] != c &&
                    buf[j] != ASCII.toLower(c))
                    return false;
            }
            return next.match(matcher, i+len, seq, true);
        }
    }

    /**
     * Node class for a unicode_case_insensitive/BMP-only sequence of
     * literal characters. Uses unicode case folding.
     */
    static final class SliceU extends SliceNode {
        SliceU(int[] buf) {
            super(buf);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int[] buf = buffer;
            int len = buf.length;
            for (int j=0; j<len; j++) {
                if ((i+j) >= matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                int c = seq.charAt(i+j);
                if (buf[j] != c &&
                    buf[j] != Character.toLowerCase(Character.toUpperCase(c)))
                    return false;
            }
            return next.match(matcher, i+len, seq, true);
        }
    }

    /**
     * Node class for a case sensitive sequence of literal characters
     * including supplementary characters.
     */
    static final class SliceS extends SliceNode {
        SliceS(int[] buf) {
            super(buf);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int[] buf = buffer;
            int x = i;
            for (int j = 0; j < buf.length; j++) {
                if (x >= matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                int c = Character.codePointAt(seq, x);
                if (buf[j] != c)
                    return false;
                x += Character.charCount(c);
                if (x > matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
            }
            return next.match(matcher, x, seq, true);
        }
    }

    /**
     * Node class for a case insensitive sequence of literal characters
     * including supplementary characters.
     */
    static class SliceIS extends SliceNode {
        SliceIS(int[] buf) {
            super(buf);
        }
        int toLower(int c) {
            return ASCII.toLower(c);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int[] buf = buffer;
            int x = i;
            for (int j = 0; j < buf.length; j++) {
                if (x >= matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                int c = Character.codePointAt(seq, x);
                if (buf[j] != c && buf[j] != toLower(c))
                    return false;
                x += Character.charCount(c);
                if (x > matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
            }
            return next.match(matcher, x, seq, true);
        }
    }

    /**
     * Node class for a case insensitive sequence of literal characters.
     * Uses unicode case folding.
     */
    static final class SliceUS extends SliceIS {
        SliceUS(int[] buf) {
            super(buf);
        }
        int toLower(int c) {
            return Character.toLowerCase(Character.toUpperCase(c));
        }
    }

    private static boolean inRange(int lower, int ch, int upper) {
        return lower <= ch && ch <= upper;
    }

    /**
     * Returns node for matching characters within an explicit value range.
     */
    private static CharProperty rangeFor(final int lower,
                                         final int upper) {
        return new CharProperty(ReScuePatternUtils.convertString(lower) + "-" + ReScuePatternUtils.convertString(upper)) {
                boolean isSatisfiedBy(int ch) {
                    return inRange(lower, ch, upper);}};
    }

    /**
     * Returns node for matching characters within an explicit value
     * range in a case insensitive manner.
     */
    private CharProperty caseInsensitiveRangeFor(final int lower,
                                                 final int upper) {
        if (has(UNICODE_CASE))
            return new CharProperty(ReScuePatternUtils.convertString(lower) + "-" + ReScuePatternUtils.convertString(upper)) {
                boolean isSatisfiedBy(int ch) {
                    if (inRange(lower, ch, upper))
                        return true;
                    int up = Character.toUpperCase(ch);
                    return inRange(lower, up, upper) ||
                           inRange(lower, Character.toLowerCase(up), upper);}};
        return new CharProperty(ReScuePatternUtils.convertString(lower) + "-" + ReScuePatternUtils.convertString(upper)) {
            boolean isSatisfiedBy(int ch) {
                return inRange(lower, ch, upper) ||
                    ASCII.isAscii(ch) &&
                        (inRange(lower, ASCII.toUpper(ch), upper) ||
                         inRange(lower, ASCII.toLower(ch), upper));
            }};
    }

    /**
     * Implements the Unicode category ALL and the dot metacharacter when
     * in dotall mode.
     */
    static final class All extends CharProperty {
        public All(String self) {
			super(self);
		}

		boolean isSatisfiedBy(int ch) {
            return true;
        }
    }

    /**
     * Node class for the dot metacharacter when dotall is not enabled.
     */
    static final class Dot extends CharProperty {
        public Dot(String self) {
			super(self);
		}

		boolean isSatisfiedBy(int ch) {
            return (ch != '\n' && ch != '\r'
                    && (ch|1) != '\u2029'
                    && ch != '\u0085');
        }
    }

    /**
     * Node class for the dot metacharacter when dotall is not enabled
     * but UNIX_LINES is enabled.
     */
    static final class UnixDot extends CharProperty {
        public UnixDot(String self) {
			super(self);
		}

		boolean isSatisfiedBy(int ch) {
            return ch != '\n';
        }
    }

    /**
     * The 0 or 1 quantifier. This one class implements all three types.
     */
    static final class Ques extends Node {
//        Node atom;
        int type;
        Ques(Node node, int type, String self) {
        	super(self);
            this.atom = node;
            this.type = type;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            switch (type) {
            case GREEDY:
                return (atom.match(matcher, i, seq, true) && next.match(matcher, matcher.last, seq, true))
                    || next.match(matcher, i, seq, true);
            case LAZY:
                return next.match(matcher, i, seq, true)
                    || (atom.match(matcher, i, seq, true) && next.match(matcher, matcher.last, seq, true));
            case POSSESSIVE:
                if (atom.match(matcher, i, seq, true)) i = matcher.last;
                return next.match(matcher, i, seq, true);
            default:
                return atom.match(matcher, i, seq, true) && next.match(matcher, matcher.last, seq, true);
            }
        }
        boolean study(TreeInfo info) {
            if (type != INDEPENDENT) {
                int minL = info.minLength;
                atom.study(info);
                info.minLength = minL;
                info.deterministic = false;
                return next.study(info);
            } else {
                atom.study(info);
                return next.study(info);
            }
        }
    }

    /**
     * Handles the curly-brace style repetition with a specified minimum and
     * maximum occurrences. The * quantifier is handled as a special case.
     * This class handles the three types.
     */
    static final class Curly extends Node {
//        Node atom;
        int type;
        int cmin;
        int cmax;

        Curly(Node node, int cmin, int cmax, int type, String self) {
        	super(self);
            this.atom = node;
            this.type = type;
            this.cmin = cmin;
            this.cmax = cmax;
            if (this.atom != null) this.atom.atom_self = this.atom;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
        	if (this.next != null && this.next.next_self == null) this.next.next_self = this.next;
        	
            int j;
            for (j = 0; j < cmin; j++) {
                if (atom.match(matcher, i, seq, true)) {
                    i = matcher.last;
                    continue;
                }
                return false;
            }
            if (type == GREEDY)
                return match0(matcher, i, j, seq);
            else if (type == LAZY)
                return match1(matcher, i, j, seq);
            else
                return match2(matcher, i, j, seq);
        }
        // Greedy match.
        // i is the index to start matching at
        // j is the number of atoms that have matched
        boolean match0(ReScueMatcher matcher, int i, int j, CharSequence seq){
            if (j >= cmax) {
                // We have matched the maximum... continue with the rest of
                // the regular expression
                return next.match(matcher, i, seq, true);
            }
            int backLimit = j;
            while (atom.match(matcher, i, seq, true)) {
                // k is the length of this match
                int k = matcher.last - i;
                if (k == 0) // Zero length match
                    break;
                // Move up index and number matched
                i = matcher.last;
                j++;
                // We are greedy so match as many as we can
                while (j < cmax) {
                    if (!atom.match(matcher, i, seq, true))
                        break;
                    if (i + k != matcher.last) {
                        if (match0(matcher, matcher.last, j+1, seq))
                            return true;
                        break;
                    }
                    i += k;
                    j++;
                }
                // Handle backing off if match fails
                while (j >= backLimit) {
                	if (next.match(matcher, i, seq, true))
                        return true;
                    i -= k;
                    j--;
                }
                return false;
            }
            return next.match(matcher, i, seq, true);
        }
        // Reluctant match. At this point, the minimum has been satisfied.
        // i is the index to start matching at
        // j is the number of atoms that have matched
        boolean match1(ReScueMatcher matcher, int i, int j, CharSequence seq){
            for (;;) {
                // Try finishing match without consuming any more
                if (next.match(matcher, i, seq, true))
                    return true;
                // At the maximum, no match found
                if (j >= cmax)
                    return false;
                // Okay, must try one more atom
                if (!atom.match(matcher, i, seq, true))
                    return false;
                // If we haven't moved forward then must break out
                if (i == matcher.last)
                    return false;
                // Move up index and number matched
                i = matcher.last;
                j++;
            }
        }
        boolean match2(ReScueMatcher matcher, int i, int j, CharSequence seq){
            for (; j < cmax; j++) {
                if (!atom.match(matcher, i, seq, true))
                    break;
                if (i == matcher.last)
                    break;
                i = matcher.last;
            }
            return next.match(matcher, i, seq, true);
        }
        boolean study(TreeInfo info) {
            // Save original info
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;
            info.reset();

            atom.study(info);

            int temp = info.minLength * cmin + minL;
            if (temp < minL) {
                temp = 0xFFFFFFF; // arbitrary large number
            }
            info.minLength = temp;

            if (maxV & info.maxValid) {
                temp = info.maxLength * cmax + maxL;
                info.maxLength = temp;
                if (temp < maxL) {
                    info.maxValid = false;
                }
            } else {
                info.maxValid = false;
            }

            if (info.deterministic && cmin == cmax)
                info.deterministic = detm;
            else
                info.deterministic = false;
            return next.study(info);
        }
    }

    /**
     * Handles the curly-brace style repetition with a specified minimum and
     * maximum occurrences in deterministic cases. This is an iterative
     * optimization over the Prolog and Loop system which would handle this
     * in a recursive way. The * quantifier is handled as a special case.
     * If capture is true then this class saves group settings and ensures
     * that groups are unset when backing off of a group match.
     */
    static final class GroupCurly extends Node {
//        Node atom;
        int type;
        int cmin;
        int cmax;
        int localIndex;
        int groupIndex;
        boolean capture;

        GroupCurly(Node node, int cmin, int cmax, int type, int local,
                   int group, boolean capture, String self) {
        	super(self);
            this.atom = node;
            this.type = type;
            this.cmin = cmin;
            this.cmax = cmax;
            this.localIndex = local;
            this.groupIndex = group;
            this.capture = capture;
            if (this.atom != null) this.atom.atom_self = this.atom;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
        	if (this.next != null && this.next.next_self == null) this.next.next_self = this.next;
        	
            int[] groups = matcher.groups;
            int[] locals = matcher.locals;
            int save0 = locals[localIndex];
            int save1 = 0;
            int save2 = 0;

            if (capture) {
                save1 = groups[groupIndex];
                save2 = groups[groupIndex+1];
            }

            // Notify GroupTail there is no need to setup group info
            // because it will be set here
            locals[localIndex] = -1;

            boolean ret = true;
            for (int j = 0; j < cmin; j++) {
                if (atom.match(matcher, i, seq, true)) {
                    if (capture) {
                        groups[groupIndex] = i;
                        groups[groupIndex+1] = matcher.last;
                    }
                    i = matcher.last;
                } else {
                    ret = false;
                    break;
                }
            }
            if (ret) {
                if (type == GREEDY) {
                    ret = match0(matcher, i, cmin, seq);
                } else if (type == LAZY) {
                    ret = match1(matcher, i, cmin, seq);
                } else {
                    ret = match2(matcher, i, cmin, seq);
                }
            }
            if (!ret) {
                locals[localIndex] = save0;
                if (capture) {
                    groups[groupIndex] = save1;
                    groups[groupIndex+1] = save2;
                }
            }
            return ret;
        }
        // Aggressive group match
        boolean match0(ReScueMatcher matcher, int i, int j, CharSequence seq){
            // don't back off passing the starting "j"
            int min = j;
            int[] groups = matcher.groups;
            int save0 = 0;
            int save1 = 0;
            if (capture) {
                save0 = groups[groupIndex];
                save1 = groups[groupIndex+1];
            }
            for (;;) {
                if (j >= cmax)
                    break;
                if (!atom.match(matcher, i, seq, true))
                    break;
                int k = matcher.last - i;
                if (k <= 0) {
                    if (capture) {
                        groups[groupIndex] = i;
                        groups[groupIndex+1] = i + k;
                    }
                    i = i + k;
                    break;
                }
                for (;;) {
                    if (capture) {
                        groups[groupIndex] = i;
                        groups[groupIndex+1] = i + k;
                    }
                    i = i + k;
                    if (++j >= cmax)
                        break;
                    if (!atom.match(matcher, i, seq, true))
                        break;
                    if (i + k != matcher.last) {
                        if (match0(matcher, i, j, seq))
                            return true;
                        break;
                    }
                }
                while (j > min) {
                    if (next.match(matcher, i, seq, true)) {
                        if (capture) {
                            groups[groupIndex+1] = i;
                            groups[groupIndex] = i - k;
                        }
                        return true;
                    }
                    // backing off
                    i = i - k;
                    if (capture) {
                        groups[groupIndex+1] = i;
                        groups[groupIndex] = i - k;
                    }
                    j--;

                }
                break;
            }
            if (capture) {
                groups[groupIndex] = save0;
                groups[groupIndex+1] = save1;
            }
            return next.match(matcher, i, seq, true);
        }
        // Reluctant matching
        boolean match1(ReScueMatcher matcher, int i, int j, CharSequence seq){
            for (;;) {
                if (next.match(matcher, i, seq, true))
                    return true;
                if (j >= cmax)
                    return false;
                if (!atom.match(matcher, i, seq, true))
                    return false;
                if (i == matcher.last)
                    return false;
                if (capture) {
                    matcher.groups[groupIndex] = i;
                    matcher.groups[groupIndex+1] = matcher.last;
                }
                i = matcher.last;
                j++;
            }
        }
        // Possessive matching
        boolean match2(ReScueMatcher matcher, int i, int j, CharSequence seq){
            for (; j < cmax; j++) {
                if (!atom.match(matcher, i, seq, true)) {
                    break;
                }
                if (capture) {
                    matcher.groups[groupIndex] = i;
                    matcher.groups[groupIndex+1] = matcher.last;
                }
                if (i == matcher.last) {
                    break;
                }
                i = matcher.last;
            }
            return next.match(matcher, i, seq, true);
        }
        boolean study(TreeInfo info) {
            // Save original info
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            boolean detm = info.deterministic;
            info.reset();

            atom.study(info);

            int temp = info.minLength * cmin + minL;
            if (temp < minL) {
                temp = 0xFFFFFFF; // Arbitrary large number
            }
            info.minLength = temp;

            if (maxV & info.maxValid) {
                temp = info.maxLength * cmax + maxL;
                info.maxLength = temp;
                if (temp < maxL) {
                    info.maxValid = false;
                }
            } else {
                info.maxValid = false;
            }

            if (info.deterministic && cmin == cmax) {
                info.deterministic = detm;
            } else {
                info.deterministic = false;
            }
            return next.study(info);
        }
    }

    /**
     * A Guard node at the end of each atom node in a Branch. It
     * serves the purpose of chaining the "match" operation to
     * "next" but not the "study", so we can collect the TreeInfo
     * of each atom node without including the TreeInfo of the
     * "next".
     */
    static final class BranchConn extends Node {
        BranchConn(String self) {super(self);};
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            return next.match(matcher, i, seq, true);
        }
        boolean study(TreeInfo info) {
            return info.deterministic;
        }
    }

    /**
     * Handles the branching of alternations. Note this is also used for
     * the ? quantifier to branch between the case where it matches once
     * and where it does not occur.
     */
    static final class Branch extends Node {
        int size = 2;
        Branch(Node first, Node second, Node branchConn, String self) {
        	super(self);
        	this.atoms = new Node[2];
            conn = branchConn;
            atoms[0] = first;
            atoms[1] = second;
        }

        void add(Node node) {
            if (size >= atoms.length) {
                Node[] tmp = new Node[atoms.length*2];
                System.arraycopy(atoms, 0, tmp, 0, atoms.length);
                atoms = tmp;
            }
            atoms[size++] = node;
        }

        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            for (int n = 0; n < size; n++) {
                if (atoms[n] == null) {
                    if (conn.next.match(matcher, i, seq, true))
                        return true;
                } else if (atoms[n].match(matcher, i, seq, true)) {
                    return true;
                }
            }
            return false;
        }

        boolean study(TreeInfo info) {
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;

            int minL2 = Integer.MAX_VALUE; //arbitrary large enough num
            int maxL2 = -1;
            for (int n = 0; n < size; n++) {
                info.reset();
                if (atoms[n] != null)
                    atoms[n].study(info);
                minL2 = Math.min(minL2, info.minLength);
                maxL2 = Math.max(maxL2, info.maxLength);
                maxV = (maxV & info.maxValid);
            }

            minL += minL2;
            maxL += maxL2;

            info.reset();
            conn.next.study(info);

            info.minLength += minL;
            info.maxLength += maxL;
            info.maxValid &= maxV;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * The GroupHead saves the location where the group begins in the locals
     * and restores them when the match is done.
     *
     * The matchRef is used when a reference to this group is accessed later
     * in the expression. The locals will have a negative value in them to
     * indicate that we do not want to unset the group if the reference
     * doesn't match.
     */
    static final class GroupHead extends Node {
        int localIndex;
        GroupHead(int localCount, String self) {
        	super(self);
            localIndex = localCount;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int save = matcher.locals[localIndex];
            matcher.locals[localIndex] = i;
            boolean ret = next.match(matcher, i, seq, true);
            matcher.locals[localIndex] = save;
            return ret;
        }
        boolean matchRef(ReScueMatcher matcher, int i, CharSequence seq){
            int save = matcher.locals[localIndex];
            matcher.locals[localIndex] = ~i; // HACK
            boolean ret = next.match(matcher, i, seq, true);
            matcher.locals[localIndex] = save;
            return ret;
        }
    }

    /**
     * Recursive reference to a group in the regular expression. It calls
     * matchRef because if the reference fails to match we would not unset
     * the group.
     */
    static final class GroupRef extends Node {
        GroupRef(GroupHead head, String self) {
        	super(self);
            this.head = head;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            return head.matchRef(matcher, i, seq)
                && next.match(matcher, matcher.last, seq, true);
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return next.study(info);
        }
    }

    /**
     * The GroupTail handles the setting of group beginning and ending
     * locations when groups are successfully matched. It must also be able to
     * unset groups that have to be backed off of.
     *
     * The GroupTail node is also used when a previous group is referenced,
     * and in that case no group information needs to be set.
     */
    static final class GroupTail extends Node {
        int localIndex;
        int groupIndex;
        GroupTail(int localCount, int groupCount, String self) {
        	super(self);
            localIndex = localCount;
            groupIndex = groupCount + groupCount;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int tmp = matcher.locals[localIndex];
            if (tmp >= 0) { // This is the normal group case.
                // Save the group so we can unset it if it
                // backs off of a match.
                int groupStart = matcher.groups[groupIndex];
                int groupEnd = matcher.groups[groupIndex+1];

                matcher.groups[groupIndex] = tmp;
                matcher.groups[groupIndex+1] = i;
                if (next.match(matcher, i, seq, true)) {
                    return true;
                }
                matcher.groups[groupIndex] = groupStart;
                matcher.groups[groupIndex+1] = groupEnd;
                return false;
            } else {
                // This is a group reference case. We don't need to save any
                // group info because it isn't really a group.
                matcher.last = i;
                return true;
            }
        }
    }

    /**
     * This sets up a loop to handle a recursive quantifier structure.
     */
    static final class Prolog extends Node {
//        Loop loop;
        Prolog(Loop loop) {
        	super(loop.self);
            this.loop = loop;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            return loop.matchInit(matcher, i, seq, true);
        }
        boolean study(TreeInfo info) {
            return loop.study(info);
        }
    }

    /**
     * Handles the repetition count for a greedy Curly. The matchInit
     * is called from the Prolog to save the index of where the group
     * beginning is stored. A zero length group check occurs in the
     * normal match but is skipped in the matchInit.
     */
    static class Loop extends Node {
//        Node body;
        int countIndex; // local count index in matcher locals
        int beginIndex; // group beginning index
        int cmin, cmax;
        Loop(int countIndex, int beginIndex, String self) {
        	super(self);
            this.countIndex = countIndex;
            this.beginIndex = beginIndex;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            // Avoid infinite loop in zero-length case.
            if (i > matcher.locals[beginIndex]) {
                int count = matcher.locals[countIndex];

                // This block is for before we reach the minimum
                // iterations required for the loop to match
                if (count < cmin) {
                    matcher.locals[countIndex] = count + 1;
                    boolean b = body.match(matcher, i, seq, true);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!b)
                        matcher.locals[countIndex] = count;
                    // Return success or failure since we are under
                    // minimum
                    return b;
                }
                // This block is for after we have the minimum
                // iterations required for the loop to match
                if (count < cmax) {
                    matcher.locals[countIndex] = count + 1;
                    boolean b = body.match(matcher, i, seq, true);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!b)
                        matcher.locals[countIndex] = count;
                    else
                        return true;
                }
            }
            return next.match(matcher, i, seq, true);
        }
        boolean matchInit(ReScueMatcher matcher, int i, CharSequence seq, boolean isTraced) {
			if (isTraced) {
				if (trace.logMatch(this, i)) return this.matchInit(matcher, i, seq);
				else return false;
			} else return this.matchInit(matcher, i, seq);
        }
        boolean matchInit(ReScueMatcher matcher, int i, CharSequence seq){
            int save = matcher.locals[countIndex];
            boolean ret = false;
            if (0 < cmin) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq, true);
            } else if (0 < cmax) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq, true);
                if (ret == false)
                    ret = next.match(matcher, i, seq, true);
            } else {
                ret = next.match(matcher, i, seq, true);
            }
            matcher.locals[countIndex] = save;
            return ret;
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * Handles the repetition count for a reluctant Curly. The matchInit
     * is called from the Prolog to save the index of where the group
     * beginning is stored. A zero length group check occurs in the
     * normal match but is skipped in the matchInit.
     */
    static final class LazyLoop extends Loop {
        LazyLoop(int countIndex, int beginIndex, String self) {
            super(countIndex, beginIndex, self);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            // Check for zero length group
            if (i > matcher.locals[beginIndex]) {
                int count = matcher.locals[countIndex];
                if (count < cmin) {
                    matcher.locals[countIndex] = count + 1;
                    boolean result = body.match(matcher, i, seq, true);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!result)
                        matcher.locals[countIndex] = count;
                    return result;
                }
                if (next.match(matcher, i, seq, true))
                    return true;
                if (count < cmax) {
                    matcher.locals[countIndex] = count + 1;
                    boolean result = body.match(matcher, i, seq, true);
                    // If match failed we must backtrack, so
                    // the loop count should NOT be incremented
                    if (!result)
                        matcher.locals[countIndex] = count;
                    return result;
                }
                return false;
            }
            return next.match(matcher, i, seq, true);
        }
        boolean matchInit(ReScueMatcher matcher, int i, CharSequence seq){
            int save = matcher.locals[countIndex];
            boolean ret = false;
            if (0 < cmin) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq, true);
            } else if (next.match(matcher, i, seq, true)) {
                ret = true;
            } else if (0 < cmax) {
                matcher.locals[countIndex] = 1;
                ret = body.match(matcher, i, seq, true);
            }
            matcher.locals[countIndex] = save;
            return ret;
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            info.deterministic = false;
            return false;
        }
    }

    /**
     * Refers to a group in the regular expression. Attempts to match
     * whatever the group referred to last matched.
     */
    static class BackRef extends Node {
        int groupIndex;
        BackRef(int groupCount, String self) {
            super(self);
            groupIndex = groupCount + groupCount;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int j = matcher.groups[groupIndex];
            int k = matcher.groups[groupIndex+1];

            int groupSize = k - j;
            // If the referenced group didn't match, neither can this
            if (j < 0)
                return false;

            // If there isn't enough input left no match
            if (i + groupSize > matcher.to) {
                matcher.hitEnd = true;
                return false;
            }
            // Check each new char to make sure it matches what the group
            // referenced matched last time around
            for (int index=0; index<groupSize; index++){
                if (seq.charAt(i+index) != seq.charAt(j+index))
                    return false;
            }

            return next.match(matcher, i+groupSize, seq, true);
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            return next.study(info);
        }
    }

    static class CIBackRef extends Node {
        int groupIndex;
        boolean doUnicodeCase;
        CIBackRef(int groupCount, boolean doUnicodeCase, String self) {
            super(self);
            groupIndex = groupCount + groupCount;
            this.doUnicodeCase = doUnicodeCase;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int j = matcher.groups[groupIndex];
            int k = matcher.groups[groupIndex+1];

            int groupSize = k - j;

            // If the referenced group didn't match, neither can this
            if (j < 0)
                return false;

            // If there isn't enough input left no match
            if (i + groupSize > matcher.to) {
                matcher.hitEnd = true;
                return false;
            }

            // Check each new char to make sure it matches what the group
            // referenced matched last time around
            int x = i;
            for (int index=0; index<groupSize; index++) {
                int c1 = Character.codePointAt(seq, x);
                int c2 = Character.codePointAt(seq, j);
                if (c1 != c2) {
                    if (doUnicodeCase) {
                        int cc1 = Character.toUpperCase(c1);
                        int cc2 = Character.toUpperCase(c2);
                        if (cc1 != cc2 &&
                            Character.toLowerCase(cc1) !=
                            Character.toLowerCase(cc2))
                            return false;
                    } else {
                        if (ASCII.toLower(c1) != ASCII.toLower(c2))
                            return false;
                    }
                }
                x += Character.charCount(c1);
                j += Character.charCount(c2);
            }

            return next.match(matcher, i+groupSize, seq, true);
        }
        boolean study(TreeInfo info) {
            info.maxValid = false;
            return next.study(info);
        }
    }

    /**
     * Searches until the next instance of its atom. This is useful for
     * finding the atom efficiently without passing an instance of it
     * (greedy problem) and without a lot of wasted search time (reluctant
     * problem).
     */
    static final class First extends Node {
//        Node atom;
        First(Node node) {
        	super(node.self);
            this.atom = BnM.optimize(node);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (atom instanceof BnM) {
                return atom.match(matcher, i, seq, true)
                    && next.match(matcher, matcher.last, seq, true);
            }
            for (;;) {
                if (i > matcher.to) {
                    matcher.hitEnd = true;
                    return false;
                }
                if (atom.match(matcher, i, seq, true)) {
                    return next.match(matcher, matcher.last, seq, true);
                }
                i += countChars(seq, i, 1);
                matcher.first++;
            }
        }
        boolean study(TreeInfo info) {
            atom.study(info);
            info.maxValid = false;
            info.deterministic = false;
            return next.study(info);
        }
    }

    static final class Conditional extends Node {
//        Node cond, yes, not;
        Conditional(Node cond, Node yes, Node not, String self) {
        	super(self);
            this.cond = cond;
            this.yes = yes;
            this.not = not;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            if (cond.match(matcher, i, seq, true)) {
                return yes.match(matcher, i, seq, true);
            } else {
                return not.match(matcher, i, seq, true);
            }
        }
        boolean study(TreeInfo info) {
            int minL = info.minLength;
            int maxL = info.maxLength;
            boolean maxV = info.maxValid;
            info.reset();
            yes.study(info);

            int minL2 = info.minLength;
            int maxL2 = info.maxLength;
            boolean maxV2 = info.maxValid;
            info.reset();
            not.study(info);

            info.minLength = minL + Math.min(minL2, info.minLength);
            info.maxLength = maxL + Math.max(maxL2, info.maxLength);
            info.maxValid = (maxV & maxV2 & info.maxValid);
            info.deterministic = false;
            return next.study(info);
        }
    }

    /**
     * Zero width positive lookahead.
     */
    static final class Pos extends Node {
//        Node cond;
        Pos(Node cond, String self) {
        	super(self);
            this.cond = cond;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int savedTo = matcher.to;
            boolean conditionMatched = false;

            // Relax transparent region boundaries for lookahead
            if (matcher.transparentBounds)
                matcher.to = matcher.getTextLength();
            try {
                conditionMatched = cond.match(matcher, i, seq, true);
            } finally {
                // Reinstate region boundaries
                matcher.to = savedTo;
            }
            return conditionMatched && next.match(matcher, i, seq, true);
        }
    }

    /**
     * Zero width negative lookahead.
     */
    static final class Neg extends Node {
//        Node cond;
        Neg(Node cond, String self) {
        	super(self);
            this.cond = cond;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int savedTo = matcher.to;
            boolean conditionMatched = false;

            // Relax transparent region boundaries for lookahead
            if (matcher.transparentBounds)
                matcher.to = matcher.getTextLength();
            try {
                if (i < matcher.to) {
                    conditionMatched = !cond.match(matcher, i, seq, true);
                } else {
                    // If a negative lookahead succeeds then more input
                    // could cause it to fail!
                    matcher.requireEnd = true;
                    conditionMatched = !cond.match(matcher, i, seq, true);
                }
            } finally {
                // Reinstate region boundaries
                matcher.to = savedTo;
            }
            return conditionMatched && next.match(matcher, i, seq, true);
        }
    }

    /**
     * For use with lookbehinds; matches the position where the lookbehind
     * was encountered.
     */
    static Node lookbehindEnd = new Node("") {
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            return i == matcher.lookbehindTo;
        }
    };

    /**
     * Zero width positive lookbehind.
     */
    static class Behind extends Node {
//        Node cond;
        int rmax, rmin;
        Behind(Node cond, int rmax, int rmin, String self) {
        	super(self);
            this.cond = cond;
            this.rmax = rmax;
            this.rmin = rmin;
        }

        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int savedFrom = matcher.from;
            boolean conditionMatched = false;
            int startIndex = (!matcher.transparentBounds) ?
                             matcher.from : 0;
            int from = Math.max(i - rmax, startIndex);
            // Set end boundary
            int savedLBT = matcher.lookbehindTo;
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;
            for (int j = i - rmin; !conditionMatched && j >= from; j--) {
                conditionMatched = cond.match(matcher, j, seq, true);
            }
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return conditionMatched && next.match(matcher, i, seq, true);
        }
    }

    /**
     * Zero width positive lookbehind, including supplementary
     * characters or unpaired surrogates.
     */
    static final class BehindS extends Behind {
        BehindS(Node cond, int rmax, int rmin, String self) {
            super(cond, rmax, rmin, self);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int rmaxChars = countChars(seq, i, -rmax);
            int rminChars = countChars(seq, i, -rmin);
            int savedFrom = matcher.from;
            int startIndex = (!matcher.transparentBounds) ?
                             matcher.from : 0;
            boolean conditionMatched = false;
            int from = Math.max(i - rmaxChars, startIndex);
            // Set end boundary
            int savedLBT = matcher.lookbehindTo;
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;

            for (int j = i - rminChars;
                 !conditionMatched && j >= from;
                 j -= j>from ? countChars(seq, j, -1) : 1) {
                conditionMatched = cond.match(matcher, j, seq, true);
            }
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return conditionMatched && next.match(matcher, i, seq, true);
        }
    }

    /**
     * Zero width negative lookbehind.
     */
    static class NotBehind extends Node {
//        Node cond;
        int rmax, rmin;
        NotBehind(Node cond, int rmax, int rmin, String self) {
        	super(self);
            this.cond = cond;
            this.rmax = rmax;
            this.rmin = rmin;
        }

        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int savedLBT = matcher.lookbehindTo;
            int savedFrom = matcher.from;
            boolean conditionMatched = false;
            int startIndex = (!matcher.transparentBounds) ?
                             matcher.from : 0;
            int from = Math.max(i - rmax, startIndex);
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;
            for (int j = i - rmin; !conditionMatched && j >= from; j--) {
                conditionMatched = cond.match(matcher, j, seq, true);
            }
            // Reinstate region boundaries
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return !conditionMatched && next.match(matcher, i, seq, true);
        }
    }

    /**
     * Zero width negative lookbehind, including supplementary
     * characters or unpaired surrogates.
     */
    static final class NotBehindS extends NotBehind {
        NotBehindS(Node cond, int rmax, int rmin, String self) {
            super(cond, rmax, rmin, self);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int rmaxChars = countChars(seq, i, -rmax);
            int rminChars = countChars(seq, i, -rmin);
            int savedFrom = matcher.from;
            int savedLBT = matcher.lookbehindTo;
            boolean conditionMatched = false;
            int startIndex = (!matcher.transparentBounds) ?
                             matcher.from : 0;
            int from = Math.max(i - rmaxChars, startIndex);
            matcher.lookbehindTo = i;
            // Relax transparent region boundaries for lookbehind
            if (matcher.transparentBounds)
                matcher.from = 0;
            for (int j = i - rminChars;
                 !conditionMatched && j >= from;
                 j -= j>from ? countChars(seq, j, -1) : 1) {
                conditionMatched = cond.match(matcher, j, seq, true);
            }
            //Reinstate region boundaries
            matcher.from = savedFrom;
            matcher.lookbehindTo = savedLBT;
            return !conditionMatched && next.match(matcher, i, seq, true);
        }
    }

    /**
     * Returns the set union of two CharProperty nodes.
     */
    private static CharProperty union(final CharProperty lhs,
                                      final CharProperty rhs) {
        return new CharProperty(lhs, rhs) {
                boolean isSatisfiedBy(int ch) {
                    return lhs.isSatisfiedBy(ch) || rhs.isSatisfiedBy(ch);}};
    }

    /**
     * Returns the set intersection of two CharProperty nodes.
     */
    private static CharProperty intersection(final CharProperty lhs,
                                             final CharProperty rhs) {
        return new CharProperty(lhs, rhs) {
                boolean isSatisfiedBy(int ch) {
                    return lhs.isSatisfiedBy(ch) && rhs.isSatisfiedBy(ch);}};
    }

    /**
     * Returns the set difference of two CharProperty nodes.
     */
    private static CharProperty setDifference(final CharProperty lhs,
                                              final CharProperty rhs) {
        return new CharProperty(lhs, rhs) {
                boolean isSatisfiedBy(int ch) {
                    return ! rhs.isSatisfiedBy(ch) && lhs.isSatisfiedBy(ch);}};
    }

    /**
     * Handles word boundaries. Includes a field to allow this one class to
     * deal with the different types of word boundaries we can match. The word
     * characters include underscores, letters, and digits. Non spacing marks
     * can are also part of a word if they have a base character, otherwise
     * they are ignored for purposes of finding word boundaries.
     */
    static final class Bound extends Node {
        static int LEFT = 0x1;
        static int RIGHT= 0x2;
        static int BOTH = 0x3;
        static int NONE = 0x4;
        int type;
        boolean useUWORD;
        Bound(int n, boolean useUWORD, String self) {
        	super(self);
            type = n;
            this.useUWORD = useUWORD;
        }

        boolean isWord(int ch) {
            return useUWORD ? UnicodeProp.WORD.is(ch)
                            : (ch == '_' || Character.isLetterOrDigit(ch));
        }

        int check(ReScueMatcher matcher, int i, CharSequence seq) {
            int ch;
            boolean left = false;
            int startIndex = matcher.from;
            int endIndex = matcher.to;
            if (matcher.transparentBounds) {
                startIndex = 0;
                endIndex = matcher.getTextLength();
            }
            if (i > startIndex) {
                ch = Character.codePointBefore(seq, i);
                left = (isWord(ch) ||
                    ((Character.getType(ch) == Character.NON_SPACING_MARK)
                     && hasBaseCharacter(matcher, i-1, seq)));
            }
            boolean right = false;
            if (i < endIndex) {
                ch = Character.codePointAt(seq, i);
                right = (isWord(ch) ||
                    ((Character.getType(ch) == Character.NON_SPACING_MARK)
                     && hasBaseCharacter(matcher, i, seq)));
            } else {
                // Tried to access char past the end
                matcher.hitEnd = true;
                // The addition of another char could wreck a boundary
                matcher.requireEnd = true;
            }
            return ((left ^ right) ? (right ? LEFT : RIGHT) : NONE);
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            return (check(matcher, i, seq) & type) > 0
                && next.match(matcher, i, seq, true);
        }
    }

    /**
     * Non spacing marks only count as word characters in bounds calculations
     * if they have a base character.
     */
    private static boolean hasBaseCharacter(ReScueMatcher matcher, int i,
                                            CharSequence seq)
    {
        int start = (!matcher.transparentBounds) ?
            matcher.from : 0;
        for (int x=i; x >= start; x--) {
            int ch = Character.codePointAt(seq, x);
            if (Character.isLetterOrDigit(ch))
                return true;
            if (Character.getType(ch) == Character.NON_SPACING_MARK)
                continue;
            return false;
        }
        return false;
    }

    /**
     * Attempts to match a slice in the input using the Boyer-Moore string
     * matching algorithm. The algorithm is based on the idea that the
     * pattern can be shifted farther ahead in the search text if it is
     * matched right to left.
     * <p>
     * The pattern is compared to the input one character at a time, from
     * the rightmost character in the pattern to the left. If the characters
     * all match the pattern has been found. If a character does not match,
     * the pattern is shifted right a distance that is the maximum of two
     * functions, the bad character shift and the good suffix shift. This
     * shift moves the attempted match position through the input more
     * quickly than a naive one position at a time check.
     * <p>
     * The bad character shift is based on the character from the text that
     * did not match. If the character does not appear in the pattern, the
     * pattern can be shifted completely beyond the bad character. If the
     * character does occur in the pattern, the pattern can be shifted to
     * line the pattern up with the next occurrence of that character.
     * <p>
     * The good suffix shift is based on the idea that some subset on the right
     * side of the pattern has matched. When a bad character is found, the
     * pattern can be shifted right by the pattern length if the subset does
     * not occur again in pattern, or by the amount of distance to the
     * next occurrence of the subset in the pattern.
     *
     * Boyer-Moore search methods adapted from code by Amy Yu.
     */
    static class BnM extends Node {
        int[] buffer;
        int[] lastOcc;
        int[] optoSft;

        /**
         * Pre calculates arrays needed to generate the bad character
         * shift and the good suffix shift. Only the last seven bits
         * are used to see if chars match; This keeps the tables small
         * and covers the heavily used ASCII range, but occasionally
         * results in an aliased match for the bad character shift.
         */
        static Node optimize(Node node) {
            if (!(node instanceof Slice)) {
                return node;
            }

            int[] src = ((Slice) node).buffer;
            int patternLength = src.length;
            // The BM algorithm requires a bit of overhead;
            // If the pattern is short don't use it, since
            // a shift larger than the pattern length cannot
            // be used anyway.
            if (patternLength < 4) {
                return node;
            }
            int i, j, k;
            int[] lastOcc = new int[128];
            int[] optoSft = new int[patternLength];
            // Precalculate part of the bad character shift
            // It is a table for where in the pattern each
            // lower 7-bit value occurs
            for (i = 0; i < patternLength; i++) {
                lastOcc[src[i]&0x7F] = i + 1;
            }
            // Precalculate the good suffix shift
            // i is the shift amount being considered
NEXT:       for (i = patternLength; i > 0; i--) {
                // j is the beginning index of suffix being considered
                for (j = patternLength - 1; j >= i; j--) {
                    // Testing for good suffix
                    if (src[j] == src[j-i]) {
                        // src[j..len] is a good suffix
                        optoSft[j-1] = i;
                    } else {
                        // No match. The array has already been
                        // filled up with correct values before.
                        continue NEXT;
                    }
                }
                // This fills up the remaining of optoSft
                // any suffix can not have larger shift amount
                // then its sub-suffix. Why???
                while (j > 0) {
                    optoSft[--j] = i;
                }
            }
            // Set the guard value because of unicode compression
            optoSft[patternLength-1] = 1;
            if (node instanceof SliceS)
                return new BnMS(src, lastOcc, optoSft, node.next);
            return new BnM(src, lastOcc, optoSft, node.next);
        }
        BnM(int[] src, int[] lastOcc, int[] optoSft, Node next) {
        	super(ReScuePatternUtils.convertString(src));
            this.buffer = src;
            this.lastOcc = lastOcc;
            this.optoSft = optoSft;
            this.next = next;
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int[] src = buffer;
            int patternLength = src.length;
            int last = matcher.to - patternLength;

            // Loop over all possible match positions in text
NEXT:       while (i <= last) {
                // Loop over pattern from right to left
                for (int j = patternLength - 1; j >= 0; j--) {
                    int ch = seq.charAt(i+j);
                    if (ch != src[j]) {
                        // Shift search to the right by the maximum of the
                        // bad character shift and the good suffix shift
                        i += Math.max(j + 1 - lastOcc[ch&0x7F], optoSft[j]);
                        continue NEXT;
                    }
                }
                // Entire pattern matched starting at i
                matcher.first = i;
                boolean ret = next.match(matcher, i + patternLength, seq, true);
                if (ret) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
                i++;
            }
            // BnM is only used as the leading node in the unanchored case,
            // and it replaced its Start() which always searches to the end
            // if it doesn't find what it's looking for, so hitEnd is true.
            matcher.hitEnd = true;
            return false;
        }
        boolean study(TreeInfo info) {
            info.minLength += buffer.length;
            info.maxValid = false;
            return next.study(info);
        }
        
        /**
         * 杩欎釜鍑芥暟鐢ㄤ簬灏唅nt鍖栫殑unicode瀛楃杞洖string
         * @return slice鐨刡uffer杞殑unicode瀛楃涓�
         */
        public String getSliceBuffer(){
        	String result = "";
        	for (int b : buffer) {
        		char[] tmp = Character.toChars(b);
        		result = result + (new String(tmp));
        	}
        	return result;
        }
    }

    /**
     * Supplementary support version of BnM(). Unpaired surrogates are
     * also handled by this class.
     */
    static final class BnMS extends BnM {
        int lengthInChars;

        BnMS(int[] src, int[] lastOcc, int[] optoSft, Node next) {
            super(src, lastOcc, optoSft, next);
            for (int x = 0; x < buffer.length; x++) {
                lengthInChars += Character.charCount(buffer[x]);
            }
        }
        boolean match(ReScueMatcher matcher, int i, CharSequence seq){
            int[] src = buffer;
            int patternLength = src.length;
            int last = matcher.to - lengthInChars;

            // Loop over all possible match positions in text
NEXT:       while (i <= last) {
                // Loop over pattern from right to left
                int ch;
                for (int j = countChars(seq, i, patternLength), x = patternLength - 1;
                     j > 0; j -= Character.charCount(ch), x--) {
                    ch = Character.codePointBefore(seq, i+j);
                    if (ch != src[x]) {
                        // Shift search to the right by the maximum of the
                        // bad character shift and the good suffix shift
                        int n = Math.max(x + 1 - lastOcc[ch&0x7F], optoSft[x]);
                        i += countChars(seq, i, n);
                        continue NEXT;
                    }
                }
                // Entire pattern matched starting at i
                matcher.first = i;
                boolean ret = next.match(matcher, i + lengthInChars, seq, true);
                if (ret) {
                    matcher.first = i;
                    matcher.groups[0] = matcher.first;
                    matcher.groups[1] = matcher.last;
                    return true;
                }
                i += countChars(seq, i, 1);
            }
            matcher.hitEnd = true;
            return false;
        }
    }

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

    /**
     *  This must be the very first initializer.
     */
    static Node accept = new Node("Exit");

    static Node lastAccept = new LastNode("Acc");

    private static class CharPropertyNames {

        static CharProperty charPropertyFor(String name) {
            CharPropertyFactory m = map.get(name);
            return m == null ? null : m.make();
        }

        private static abstract class CharPropertyFactory {
            abstract CharProperty make();
        }

        private static void defCategory(String name,
                                        final int typeMask) {
            map.put(name, new CharPropertyFactory() {
                    CharProperty make() { return new Category(typeMask);}});
        }

        private static void defRange(String name,
                                     final int lower, final int upper) {
            map.put(name, new CharPropertyFactory() {
                    CharProperty make() { return rangeFor(lower, upper);}});
        }

        private static void defCtype(String name,
                                     final int ctype) {
            map.put(name, new CharPropertyFactory() {
                    CharProperty make() { return new Ctype(ctype);}});
        }

        private static abstract class CloneableProperty
            extends CharProperty implements Cloneable
        {
            public CloneableProperty(String self) {
				super(self);
			}

			public CloneableProperty clone() {
                try {
                    return (CloneableProperty) super.clone();
                } catch (CloneNotSupportedException e) {
                    throw new AssertionError(e);
                }
            }
        }

        private static void defClone(String name,
                                     final CloneableProperty p) {
            map.put(name, new CharPropertyFactory() {
                    CharProperty make() { return p.clone();}});
        }

        private static final HashMap<String, CharPropertyFactory> map
            = new HashMap<>();

        static {
            // Unicode character property aliases, defined in
            // http://www.unicode.org/Public/UNIDATA/PropertyValueAliases.txt
            defCategory("Cn", 1<<Character.UNASSIGNED);
            defCategory("Lu", 1<<Character.UPPERCASE_LETTER);
            defCategory("Ll", 1<<Character.LOWERCASE_LETTER);
            defCategory("Lt", 1<<Character.TITLECASE_LETTER);
            defCategory("Lm", 1<<Character.MODIFIER_LETTER);
            defCategory("Lo", 1<<Character.OTHER_LETTER);
            defCategory("Mn", 1<<Character.NON_SPACING_MARK);
            defCategory("Me", 1<<Character.ENCLOSING_MARK);
            defCategory("Mc", 1<<Character.COMBINING_SPACING_MARK);
            defCategory("Nd", 1<<Character.DECIMAL_DIGIT_NUMBER);
            defCategory("Nl", 1<<Character.LETTER_NUMBER);
            defCategory("No", 1<<Character.OTHER_NUMBER);
            defCategory("Zs", 1<<Character.SPACE_SEPARATOR);
            defCategory("Zl", 1<<Character.LINE_SEPARATOR);
            defCategory("Zp", 1<<Character.PARAGRAPH_SEPARATOR);
            defCategory("Cc", 1<<Character.CONTROL);
            defCategory("Cf", 1<<Character.FORMAT);
            defCategory("Co", 1<<Character.PRIVATE_USE);
            defCategory("Cs", 1<<Character.SURROGATE);
            defCategory("Pd", 1<<Character.DASH_PUNCTUATION);
            defCategory("Ps", 1<<Character.START_PUNCTUATION);
            defCategory("Pe", 1<<Character.END_PUNCTUATION);
            defCategory("Pc", 1<<Character.CONNECTOR_PUNCTUATION);
            defCategory("Po", 1<<Character.OTHER_PUNCTUATION);
            defCategory("Sm", 1<<Character.MATH_SYMBOL);
            defCategory("Sc", 1<<Character.CURRENCY_SYMBOL);
            defCategory("Sk", 1<<Character.MODIFIER_SYMBOL);
            defCategory("So", 1<<Character.OTHER_SYMBOL);
            defCategory("Pi", 1<<Character.INITIAL_QUOTE_PUNCTUATION);
            defCategory("Pf", 1<<Character.FINAL_QUOTE_PUNCTUATION);
            defCategory("L", ((1<<Character.UPPERCASE_LETTER) |
                              (1<<Character.LOWERCASE_LETTER) |
                              (1<<Character.TITLECASE_LETTER) |
                              (1<<Character.MODIFIER_LETTER)  |
                              (1<<Character.OTHER_LETTER)));
            defCategory("M", ((1<<Character.NON_SPACING_MARK) |
                              (1<<Character.ENCLOSING_MARK)   |
                              (1<<Character.COMBINING_SPACING_MARK)));
            defCategory("N", ((1<<Character.DECIMAL_DIGIT_NUMBER) |
                              (1<<Character.LETTER_NUMBER)        |
                              (1<<Character.OTHER_NUMBER)));
            defCategory("Z", ((1<<Character.SPACE_SEPARATOR) |
                              (1<<Character.LINE_SEPARATOR)  |
                              (1<<Character.PARAGRAPH_SEPARATOR)));
            defCategory("C", ((1<<Character.CONTROL)     |
                              (1<<Character.FORMAT)      |
                              (1<<Character.PRIVATE_USE) |
                              (1<<Character.SURROGATE))); // Other
            defCategory("P", ((1<<Character.DASH_PUNCTUATION)      |
                              (1<<Character.START_PUNCTUATION)     |
                              (1<<Character.END_PUNCTUATION)       |
                              (1<<Character.CONNECTOR_PUNCTUATION) |
                              (1<<Character.OTHER_PUNCTUATION)     |
                              (1<<Character.INITIAL_QUOTE_PUNCTUATION) |
                              (1<<Character.FINAL_QUOTE_PUNCTUATION)));
            defCategory("S", ((1<<Character.MATH_SYMBOL)     |
                              (1<<Character.CURRENCY_SYMBOL) |
                              (1<<Character.MODIFIER_SYMBOL) |
                              (1<<Character.OTHER_SYMBOL)));
            defCategory("LC", ((1<<Character.UPPERCASE_LETTER) |
                               (1<<Character.LOWERCASE_LETTER) |
                               (1<<Character.TITLECASE_LETTER)));
            defCategory("LD", ((1<<Character.UPPERCASE_LETTER) |
                               (1<<Character.LOWERCASE_LETTER) |
                               (1<<Character.TITLECASE_LETTER) |
                               (1<<Character.MODIFIER_LETTER)  |
                               (1<<Character.OTHER_LETTER)     |
                               (1<<Character.DECIMAL_DIGIT_NUMBER)));
            defRange("L1", 0x00, 0xFF); // Latin-1
            map.put("all", new CharPropertyFactory() {
                    CharProperty make() { return new All("all"); }});

            // Posix regular expression character classes, defined in
            // http://www.unix.org/onlinepubs/009695399/basedefs/xbd_chap09.html
            defRange("ASCII", 0x00, 0x7F);   // ASCII
            defCtype("Alnum", ASCII.ALNUM);  // Alphanumeric characters
            defCtype("Alpha", ASCII.ALPHA);  // Alphabetic characters
            defCtype("Blank", ASCII.BLANK);  // Space and tab characters
            defCtype("Cntrl", ASCII.CNTRL);  // Control characters
            defRange("Digit", '0', '9');     // Numeric characters
            defCtype("Graph", ASCII.GRAPH);  // printable and visible
            defRange("Lower", 'a', 'z');     // Lower-case alphabetic
            defRange("Print", 0x20, 0x7E);   // Printable characters
            defCtype("Punct", ASCII.PUNCT);  // Punctuation characters
            defCtype("Space", ASCII.SPACE);  // Space characters
            defRange("Upper", 'A', 'Z');     // Upper-case alphabetic
            defCtype("XDigit",ASCII.XDIGIT); // hexadecimal digits

            // Java character properties, defined by methods in Character.java
            defClone("javaLowerCase", new CloneableProperty("javaLowerCase") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isLowerCase(ch);}});
            defClone("javaUpperCase", new CloneableProperty("javaUpperCase") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isUpperCase(ch);}});
            defClone("javaAlphabetic", new CloneableProperty("javaAlphabetic") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isAlphabetic(ch);}});
            defClone("javaIdeographic", new CloneableProperty("javaIdeographic") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isIdeographic(ch);}});
            defClone("javaTitleCase", new CloneableProperty("javaTitleCase") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isTitleCase(ch);}});
            defClone("javaDigit", new CloneableProperty("javaDigit") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isDigit(ch);}});
            defClone("javaDefined", new CloneableProperty("javaDefined") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isDefined(ch);}});
            defClone("javaLetter", new CloneableProperty("javaLetter") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isLetter(ch);}});
            defClone("javaLetterOrDigit", new CloneableProperty("javaLetterOrDigit") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isLetterOrDigit(ch);}});
            defClone("javaJavaIdentifierStart", new CloneableProperty("javaJavaIdentifierStart") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isJavaIdentifierStart(ch);}});
            defClone("javaJavaIdentifierPart", new CloneableProperty("javaJavaIdentifierPart") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isJavaIdentifierPart(ch);}});
            defClone("javaUnicodeIdentifierStart", new CloneableProperty("javaUnicodeIdentifierStart") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isUnicodeIdentifierStart(ch);}});
            defClone("javaUnicodeIdentifierPart", new CloneableProperty("javaUnicodeIdentifierPart") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isUnicodeIdentifierPart(ch);}});
            defClone("javaIdentifierIgnorable", new CloneableProperty("javaIdentifierIgnorable") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isIdentifierIgnorable(ch);}});
            defClone("javaSpaceChar", new CloneableProperty("javaSpaceChar") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isSpaceChar(ch);}});
            defClone("javaWhitespace", new CloneableProperty("javaWhitespace") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isWhitespace(ch);}});
            defClone("javaISOControl", new CloneableProperty("javaISOControl") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isISOControl(ch);}});
            defClone("javaMirrored", new CloneableProperty("javaMirrored") {
                boolean isSatisfiedBy(int ch) {
                    return Character.isMirrored(ch);}});
        }
    }

    /**
     * Creates a predicate which can be used to match a string.
     *
     * @return  The predicate which can be used for matching on a string
     * @since   1.8
     */
    public Predicate<String> asPredicate (Trace trace) {
        return s -> matcher(s, trace).find().matchSuccess;
    }

    /**
     * Creates a stream from the given input sequence around matches of this
     * pattern.
     *
     * <p> The stream returned by this method contains each substring of the
     * input sequence that is terminated by another subsequence that matches
     * this pattern or is terminated by the end of the input sequence.  The
     * substrings in the stream are in the order in which they occur in the
     * input. Trailing empty strings will be discarded and not encountered in
     * the stream.
     *
     * <p> If this pattern does not match any subsequence of the input then
     * the resulting stream has just one element, namely the input sequence in
     * string form.
     *
     * <p> When there is a positive-width match at the beginning of the input
     * sequence then an empty leading substring is included at the beginning
     * of the stream. A zero-width match at the beginning however never produces
     * such empty leading substring.
     *
     * <p> If the input sequence is mutable, it must remain constant during the
     * execution of the terminal stream operation.  Otherwise, the result of the
     * terminal stream operation is undefined.
     *
     * @param   input
     *          The character sequence to be split
     *
     * @return  The stream of strings computed by splitting the input
     *          around matches of this pattern
     * @see     #split(CharSequence)
     * @since   1.8
     */
    public Stream<String> splitAsStream(final CharSequence input, Trace trace) {
        class MatcherIterator implements Iterator<String> {
            private final ReScueMatcher matcher;
            // The start position of the next sub-sequence of input
            // when current == input.length there are no more elements
            private int current;
            // null if the next element, if any, needs to obtained
            private String nextElement;
            // > 0 if there are N next empty elements
            private int emptyElementCount;

            MatcherIterator() {
                this.matcher = matcher(input, trace);
            }

            public String next() {
                if (!hasNext())
                    throw new NoSuchElementException();

                if (emptyElementCount == 0) {
                    String n = nextElement;
                    nextElement = null;
                    return n;
                } else {
                    emptyElementCount--;
                    return "";
                }
            }

            public boolean hasNext() {
                if (nextElement != null || emptyElementCount > 0)
                    return true;

                if (current == input.length())
                    return false;

                // Consume the next matching element
				// Count sequence of matching empty elements
				while (matcher.find().matchSuccess) {
					nextElement = input.subSequence(current, matcher.start()).toString();
					current = matcher.end();
					if (!nextElement.isEmpty()) {
						return true;
					} else if (current > 0) { // no empty leading substring for
												// zero-width
												// match at the beginning of the
												// input
						emptyElementCount++;
					}
				}

                // Consume last matching element
                nextElement = input.subSequence(current, input.length()).toString();
                current = input.length();
                if (!nextElement.isEmpty()) {
                    return true;
                } else {
                    // Ignore a terminal sequence of matching empty elements
                    emptyElementCount = 0;
                    nextElement = null;
                    return false;
                }
            }
        }
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new MatcherIterator(), Spliterator.ORDERED | Spliterator.NONNULL), false);
    }
    
    ////////////////////////////////////////////////////
    /*
     * Most of the additional code are here
     */
    public static Trace trace; // Trace of one matching
    private Set<Node> nodes = new HashSet<Node>(); // All nodes (states) of a compiled regex
    
    /**
     * Get all nodes (states) of a compiled regex
     */
    public Set<Node> getAllNodes() {
    	if (nodes.isEmpty()) traverseAllNodes();
    	return nodes;
    }
    
    /**
     * Print all nodes (for testing)
     */
    public void printAllNodes() {
    	if (nodes.isEmpty()) traverseAllNodes();
    	allNode(root, true);
    }
    
    /**
     * Traverse all node without printing
     */
    private void traverseAllNodes() {
    	if (nodes.isEmpty()) {
    		allNode(root, false);
    	}
    }
    
    /**
     * Get all pure text from the compiled regex
     * That is, buffers and chars of all slice and single node
     */
    public List<String> getAllSlices() {
    	if (nodes.isEmpty()) traverseAllNodes();
    	List<String> slices = new ArrayList<String>();
    	for (Node n : nodes) {
    		if (n instanceof SliceNode) slices.add(((SliceNode) n).getSliceBuffer());
    		if (n instanceof BnM) slices.add(((BnM) n).getSliceBuffer());
    		if (n instanceof Single) slices.add(((Single) n).getSliceBuffer());
    		if (n instanceof SingleI) slices.add(((SingleI) n).getSliceBuffer());
    		if (n instanceof SingleS) slices.add(((SingleS) n).getSliceBuffer());
    		if (n instanceof SingleU) slices.add(((SingleU) n).getSliceBuffer());
    	}
//    	slices.add(pattern);
    	String[] forceSlice = pattern.split("[\\[({})\\]]");
    	for (String fs : forceSlice) {
    		if (fs.length() == 0) continue;
    		else if (fs.length() == 3 && fs.charAt(1) == '-') {
    			for (char i = fs.charAt(0); i <= fs.charAt(2); i++){
    				String c = "" + i;
	    			if (!slices.contains(c)) {
	    				slices.add(c);
//	    				System.out.print(c + " ");
	    			}
    			}
    		} else {
    			if (!slices.contains(fs)) {
    				slices.add(fs);
//    				System.out.print(fs + " ");
    			}
    		}
    	}
//    	System.out.println();
    	return slices;
    }
    
    /**
     * Traverse node recursively
     * @param node Current node
     * @param print Print or not
     */
    private void allNode(Node node, boolean print) {
		if (node == null) {
			if (print) System.out.println("End");
		} else {
			if (print) System.out.println(node);
			nodes.add(node);
			
			// Curly
			if (node.atom != null && !nodes.contains(node.atom)) allNode(node.atom, print);
			if (node.atom_self != null && !nodes.contains(node.atom_self)) allNode(node.atom_self, print);
			// Branch
			if (node.atoms != null) {
				for (Node a : node.atoms) {
					if (!nodes.contains(a)) allNode(a, print);
				}
			}
			if (node.conn != null && !nodes.contains(node.conn)) allNode(node.conn, print);
			// Loop
			if (node.body != null && !nodes.contains(node.body)) allNode(node.body, print);
			// Prolog
			if (node.loop != null && !nodes.contains(node.loop)) allNode(node.loop, print);
			// GroupRef
			if (node.head != null && !nodes.contains(node.head)) allNode(node.head, print);
			// Conditional
			if (node.cond != null && !nodes.contains(node.cond)) allNode(node.cond, print);
			if (node.yes != null && !nodes.contains(node.yes)) allNode(node.yes, print);
			if (node.not != null && !nodes.contains(node.not)) allNode(node.not, print);
			
			if (node.next != null && !nodes.contains(node.next)) allNode(node.next, print);
			// Start
			if (node.next_self != null && !nodes.contains(node.next_self)) allNode(node.next_self, print);
		}
    }
    
    
    /**
     * Get parent-child relation between the nodes (source-destination of a state transition)
     * Currently we can get a relation of directly child and parents <child, list<node> parents>
     */
    @Deprecated
    public NodeRelation getNodeRelation() {
    	NodeRelation result = new NodeRelation();
    	Set<Node> visitedNodes = new HashSet<Node>();
    	nodeChild(visitedNodes, result, null, root);
    	return result;
    }
    
    /**
     * Traverse all node, and get the source-destination relation
     * @param visitedNodes
     * @param relation
     * @param prev
     * @param cur
     */
    @Deprecated
    private void nodeChild(Set<Node> visitedNodes, NodeRelation relation, Node prev, Node cur) {
    	if (cur == null) { // 涓嶅簲璇ュ嚭鐜扮殑鎯呭喌
    		System.out.println("Error: node is null");
		} else {
			if (visitedNodes.contains(cur)) {
				relation.addParent(cur, prev);
			} else {
				visitedNodes.add(cur);
				relation.addParent(cur, prev);
			
				// Curly
				if (cur.atom != null) nodeChild(visitedNodes, relation, cur, cur.atom);
				if (cur.atom != null && cur.atom_self != null) nodeChild(visitedNodes, relation, cur.atom, cur.atom_self);
				// Branch
				if (cur.atoms != null) {
					for (Node a : cur.atoms) {
						nodeChild(visitedNodes, relation, cur, a);
					}
				}
				if (cur.conn != null) nodeChild(visitedNodes, relation, cur, cur.conn);
				// Loop
				if (cur.body != null) nodeChild(visitedNodes, relation, cur, cur.body);
				// Prolog
				if (cur.loop != null) nodeChild(visitedNodes, relation, cur, cur.loop);
				// GroupRef
				if (cur.head != null) nodeChild(visitedNodes, relation, cur, cur.head);
				// Conditional
				if (cur.cond != null) nodeChild(visitedNodes, relation, cur, cur.cond);
				if (cur.yes != null) nodeChild(visitedNodes, relation, cur, cur.yes);
				if (cur.not != null) nodeChild(visitedNodes, relation, cur, cur.not);
				// Next
				if (cur.next != null) nodeChild(visitedNodes, relation, cur, cur.next);
				// the node after Start
				if (cur.next_self != null) nodeChild(visitedNodes, relation, cur, cur.next_self);
			}
		}
    }
    
    /**
     * Get all destination node of a parent node
     * @param node
     * @return
     */
    public List<Node> getChildNodes(Node node) {
    	List<Node> result = new ArrayList<Node>();
    	
    	// All possible next node
        if (node.atom != null) result.add(node.atom);
        if (node.atom_self != null) result.add(node.atom_self);
        if (node.body != null) result.add(node.body);
        if (node.atoms != null) {
        	for (Node a : node.atoms) if (a != null) result.add(a);
        }
        if (node.conn != null) result.add(node.conn);
        if (node.head != null) result.add(node.head);
        if (node.loop != null) result.add(node.loop);
        if (node.cond != null) result.add(node.cond);
        if (node.yes != null) result.add(node.yes);
        if (node.not != null) result.add(node.not);
        if (node.next != null) result.add(node.next);
        if (node.next_self != null) result.add(node.next_self);
        return result;
    }
    
    /**
     * Paint the regex by {prefuse.jar}
     */
    int nodeIndex = 0;
    boolean painted = false;
    public Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
    public void paintRegex() {
    	HashMap<Node, Integer> visitedNodes = new HashMap<Node, Integer>();
        HashMap<Node, List<Node>> visitedEdges = new HashMap<Node, List<Node>>();
        
    	Schema n_sch = new Schema();
    	n_sch.addColumn("Node", String.class);
    	n_sch.lockSchema();
    	Table nodes = n_sch.instantiate();
    	
    	Schema e_sch = new Schema();
    	e_sch.addColumn("from", int.class);
    	e_sch.addColumn("to", int.class);
    	e_sch.addColumn("name", String.class);
    	Table edges = e_sch.instantiate();
    	
    	nodeIndex = 0;
    	paintNode(null, root, "", nodes, edges, visitedNodes, visitedEdges);
    	RegexViewer.paintRegex(this, nodes, edges, true, "from", "to", "Node");
    	painted = true;
    }
    
    public void paintTrace(Trace t){
    	if (painted) RegexViewer.paintLog(this, t.getLogNode(), t.getLogIdx());
    	else {
    		paintRegex();
    		RegexViewer.paintLog(this, t.getLogNode(), t.getLogIdx());
    	}
    }
    
    /**
     * Traverse all node recursively to collect node information for painting
     * @param prev Previous node
     * @param cur Current node
     * @param edgename Edge name
     * @param nodes A table storing all nodes
     * @param edges A table storing all edges
     * @param visitedNodes
     * @param visitedEdges
     */
    private void paintNode(Node prev, Node cur, String edgename, Table nodes, Table edges, HashMap<Node, Integer> visitedNodes, HashMap<Node, List<Node>> visitedEdges) {
    	// Exit recurse
    	if (cur == null) return ;
    	
    	// Root node
    	if (prev == null) {
    		int rid = nodes.addRow();
//    		nodes.set(rid, "Node", cur.toString().split("[$@]")[1] + "\n" + cur.self);
    		nodeMap.put(nodeIndex, cur);
    		nodes.set(rid, "Node", (nodeIndex++) + ": " + cur.toString().split("[$@]")[1] + "\n" + cur.self);
    		visitedNodes.put(cur, rid);
    		
    		paintNode(cur, cur.next, "next", nodes, edges, visitedNodes, visitedEdges);
    	// Not root node
		} else {
			if (!visitedNodes.containsKey(cur)) {
				// Save the node
				int rid = nodes.addRow();
//				nodes.set(rid, "Node", cur.toString().split("\\$")[1] + "\n" + cur.self);
				nodeMap.put(nodeIndex, cur);
				nodes.set(rid, "Node", (nodeIndex++) + ": " + cur.toString().split("[$@]")[1] + "\n" + cur.self);
				visitedNodes.put(cur, rid);
				
				// Save the edge
				// prev->cur Node prev outdegree = 0
				if (!visitedEdges.containsKey(prev)) {
					int erid = edges.addRow();
					edges.set(erid, "from", visitedNodes.get(prev));
					edges.set(erid, "to", visitedNodes.get(cur));
					edges.set(erid, "name", edgename);
					
					ArrayList<Node> tos = new ArrayList<Node>();
					tos.add(cur);
					visitedEdges.put(cur, tos);
				// prev->cur Node prev outdegree > 0
				} else if (!visitedEdges.get(prev).contains(cur)) {
					int erid = edges.addRow();
					edges.set(erid, "from", visitedNodes.get(prev));
					edges.set(erid, "to", visitedNodes.get(cur));
					edges.set(erid, "name", edgename);
					
					visitedEdges.get(prev).add(cur);
				}
				
				// Visit children of the node, normal state
				// Curly
				if (cur.atom != null) {
					paintNode(cur, cur.atom, "atom", nodes, edges, visitedNodes, visitedEdges);
				}
				if (cur.atom_self != null) paintNode(cur, cur.atom_self, "atom_self", nodes, edges, visitedNodes, visitedEdges);
				// Branch
				if (cur.atoms != null) {
					int i = 0;
					for (Node a : cur.atoms) {
						paintNode(cur, a, "atoms[" + i + "]", nodes , edges, visitedNodes, visitedEdges);
						i++;
					}
				}
				// Conn
				if (cur.conn != null) paintNode(cur, cur.conn, "conn", nodes, edges, visitedNodes, visitedEdges);
				// Loop
				if (cur.body != null) paintNode(cur, cur.body, "body", nodes, edges, visitedNodes, visitedEdges);
				// Prolog
				if (cur.loop != null) paintNode(cur, cur.loop, "loop", nodes, edges, visitedNodes, visitedEdges);
				// GroupRef
				if (cur.head != null) paintNode(cur, cur.head, "head", nodes, edges, visitedNodes, visitedEdges);
				// Conditional
				if (cur.cond != null) paintNode(cur, cur.cond, "cond", nodes, edges, visitedNodes, visitedEdges);
				if (cur.yes != null) paintNode(cur, cur.yes, "yes", nodes, edges, visitedNodes, visitedEdges);
				if (cur.not != null) paintNode(cur, cur.not, "not", nodes, edges, visitedNodes, visitedEdges);
				// Next
				if (cur.next != null) paintNode(cur, cur.next, "next", nodes, edges, visitedNodes, visitedEdges);
				if (cur.next_self != null) paintNode(cur, cur.next_self, "next_self", nodes, edges, visitedNodes, visitedEdges);
			} else { // If node is visited, only save the edge
				// prev->cur Node prev outdegree = 0
				if (!visitedEdges.containsKey(prev)) {
					int erid = edges.addRow();
					edges.set(erid, "from", visitedNodes.get(prev));
					edges.set(erid, "to", visitedNodes.get(cur));
					edges.set(erid, "name", edgename);
					
					ArrayList<Node> tos = new ArrayList<Node>();
					tos.add(cur);
					visitedEdges.put(cur, tos);
				// prev->cur Node prev outdegree > 0
				} else if (!visitedEdges.get(prev).contains(cur)) {
					int erid = edges.addRow();
					edges.set(erid, "from", visitedNodes.get(prev));
					edges.set(erid, "to", visitedNodes.get(cur));
					edges.set(erid, "name", edgename);
					
					visitedEdges.get(prev).add(cur);
				}
			}
    	}
    }
}
