package org.wandledi.spells;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.wandledi.AbstractSpell;
import org.wandledi.Spell;
import org.xml.sax.Attributes;

/**<p>A TextTransformation is used to transform the text part of any element's body.
 * This will not affect any nested elements' text.</p>
 * Example:
 *
 * <pre>&lt;p id="welcome"&gt;
 *   Welcome (User), to your &lt;span class="fat"&gt;Profile&lt;span&gt; at (siteName)!
 * &lt;/p&gt;</pre>
 *
 * <p>Now if you apply a TextTransformation this will affect the following parts only:</p>
 *
 * <p>'Welcome (User), to your ' and ' at (siteName)!'</p>
 *
 * <p>
 * A TextTransformation allows you to perform several different
 * insertions:</p>
 *
 * <ul>
 *   <li>
 *     <b>Index- and name-based 'spot insertion'</b><br/>
 *     Spots are any part of the text that is enclosed with parenthesis.
 *     Contents can be inserted into these spots by referring to them via
 *     their index (they are numbered in order of appearance starting at 0)
 *     or a regex matching the text that is inside them.<br/>
 *     Example:
 *     <pre>    &lt;p&gt;Hello (user name)!&lt;p/&gt;</pre>
 *     Which could be transformed like this:
 *     <pre>    get("p").text.insert(user.name); // index based (vararg string array)
 *              Map<Integer, String> intMapped = new HashMap<Integer, String>();
 *              Map<String, String> regexMapped = new HashMap<String, String>();
 *              intMapped.put(0, user.name); // insert at first occurence
 *              regexMapped.put("user name", user.name); // insert at matched name OR ...
 *              regexMapped.put(".*name", user.name); // ... at name ending accordingly OR ...
 *              regexMapped.put("user.*", user.name); // ... at name starting accordingly
 *              get("p").text.insert(intMapped);
 *              get("p").text.insert(strMapped);</pre>
 *   </li>
 *   <li>
 *     <b>Regex-based insertion</b><br/>
 *     You can either overwrite the matched spots directly or provide
 *     a StringTransformation that will take the matched spot's content
 *     and transform it into the result which will be placed there instead.<br/>
 *     Example:
 *     <pre>    $("p").text.insertAt("\\(.+?\\)", user.name); // overwrite
 *              $("p").text.insertAt("\\(.+?\\)", new StringTransformation() {
 *                      public String transform(String input) {
 *                          return input.toUpperCase(); // shout!
 *                      }); // transform</pre>
 *     Both of those methods again work with vararg values, meaning that if the given regex
 *     matches several spots the values will be inserted at the order they were passed.
 *     If you want to perform name-based regex insertion you can simply use a single
 *     StringTransformation that will do the matching.
 *   </li>
 * </ul>
 *
 * @author Markus Kahl
 */
public class TextTransformation extends AbstractSpell {
    // Test Note: Test Case for this one inside the Scala Wrapper Tests
    // it's just more comfortable to write down!

    private String regex;
    private StringTransformation transformation;
    private StringBuilder buffer = new StringBuilder();
    private int nestingLevel = -1;
    private boolean considerEmptyText = false;

    /**Creates a new TextTransformation that will transform
     * any one occurence of a given regex.
     *
     * If the given regex contains a single capturing group
     * the StringTransformation will be passed that group
     * instead of the whole match.
     *
     * @param regex The regex used to match the parts of the text to be transformed.
     * @param transformation The StringTransformation transforming each match.
     */
    public TextTransformation(String regex, StringTransformation transformation) {
        this.regex = regex;
        this.transformation = transformation;
    }

    /**Creates a new TextTransformation that will transform each chunk of text
     * using a given StringTransformation. An element that only contains text has
     * one chunk of text. A single nested element possibly splits that text into two chunks.
     *
     * @param transformation The StringTransformation transforming each chunk.
     */
    public TextTransformation(StringTransformation transformation) {
        this.transformation = transformation;
    }

    /**Creates a new TextTransformation that discards each chunk of text
     * and replaces it with the given replacement.
     *
     * @param replacement The String the text is to be replaced with.
     */
    public TextTransformation(String replacement) {
        this.transformation = new Replacement(replacement);
    }

    /**Creates a new TextTransformation that will replace
     * any one occurence of a given regex with a given text.
     * The replacement may refer to groups of the regex
     * just as if using String#replaceAll().
     *
     * If the given regex contains a single capturing group
     * only that group will be replaced instead of the whole match.
     *
     * @param regex The regex used to match the parts of the text to be replaced.
     * @param value The value to be inserted instead of the matches.
     */
    public TextTransformation(String regex, String value) {
        this.regex = regex;
        this.transformation = new Replacement(value);
    }

    /**Creates an index-based 'spot' TextTransformation.
     * A spot is any part of the text that is enclosed with parenthesis.
     *
     * @param values The values to be inserted into the spots. The first element
     *               will be inserted at the first spot, the second element at the
     *               second spot etc.. If there are more spots than values the last
     *               element will be inserted for every additional spot.
     */
    public TextTransformation(String[] values) {
        this.regex = "\\(.+?\\)";
        this.transformation = new ArrayWriter(values);
    }

    /**Creates an index-based 'spot' TextTransformation.
     * A spot is any part of the text that is enclosed with parenthesis.
     *
     * @param order The order in which the given values are to be inserted.
     *              The normal order would be 0, 1, 2, 3, ..., n.
     * @param values The values to be inserted into the spots. The first element
     *               will be inserted at the first spot, the second element at the
     *               second spot etc.. If there are more spots than values the last
     *               element will be inserted for every additional spot.
     */
    public TextTransformation(int[] order, String[] values) {
        this.regex = "\\(.+?\\)";
        String[] orderedValues = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            orderedValues[order[i]] = values[i];
        }
        this.transformation = new ArrayWriter(orderedValues);
    }

    /**Creates a name- or regex-based 'spot' TextTransformation.
     * A spot is any part of the text that is enclosed with parenthesis.
     *
     * @param spotMappings Each SpotMapping maps a name or regex to a certain value
     *                     to be inserted at spots that match that name or regex.
     */
    public TextTransformation(final SpotMapping... spotMappings) {
        final Pattern[] patterns = new Pattern[spotMappings.length];
        for (int i = 0; i < spotMappings.length; ++i) {
            if (spotMappings[i].isRegex()) {
                patterns[i] = Pattern.compile(spotMappings[i].getPattern());
            }
        }
        this.regex = "\\(.+?\\)"; // match everything
        this.transformation = new StringTransformation() {
            public String transform(String inputRaw) {
                String input = inputRaw.substring(1, inputRaw.length() - 1);
                for (int i = 0; i < spotMappings.length; ++i) {
                    if (spotMappings[i].isRegex()) {
                        Matcher matcher = patterns[i].matcher(input);
                        if (matcher.matches()) {
                            return spotMappings[i].nextTransformation().transform(input);
                        }
                    } else {
                        String pattern = spotMappings[i].getPattern();
                        boolean match = false;
                        match |= pattern.startsWith("*") && input.endsWith(pattern.substring(1));
                        match |= match || (pattern.endsWith("*") &&
                                input.startsWith(pattern.substring(0, pattern.length() - 1)));
                        match |= match || pattern.equals(input);
                        if (match) {
                            return spotMappings[i].nextTransformation().transform(input);
                        }
                    }
                }
                return inputRaw;
            }
        };
    }

    public static TextTransformation inserting(String... values) {
        return new TextTransformation(values);
    }

    protected String transform(CharSequence text) {
        if (regex == null) {
            return transformation.transform(text.toString());
        }
        if (transformation instanceof Replacement) {
            return text.toString().replaceAll(regex, transformation.transform(null));
        }
        StringBuilder result = new StringBuilder(text);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        int lengthDelta = 0;
        while (matcher.find()) {
            String insertion = transformation.transform(matcher.group(
                    matcher.groupCount() == 1 ? 1 : 0));
            int matchLength = matcher.end() - matcher.start();
            result.replace(matcher.start() + lengthDelta, matcher.end() + lengthDelta, insertion);
            lengthDelta += (insertion.length() - matchLength);
        }
        return result.toString();
    }

    protected void flush() {
        if (buffer.length() > 0 || considerEmptyText) {
            String result = transform(buffer);
            super.writeCharacters(result.toCharArray(), 0, result.length(), true);
            buffer.setLength(0);
        }
    }

    @Override
    public void startTransformedElement(String name, Attributes attributes) {
        ++nestingLevel;
        if (nestingLevel == 1) {
            flush();
        }
        super.startTransformedElement(name, attributes);
    }

    @Override
    public void endTransformedElement(String name) {
        if (nestingLevel - 1 == -1) {
            flush();
        }
        super.endTransformedElement(name);
        --nestingLevel;
    }

    @Override
    public void startElement(String name, Attributes attributes) {
        ++nestingLevel;
        if (nestingLevel == 1) {
            flush();
        }
        super.startElement(name, attributes);
    }

    @Override
    public void endElement(String name) {
        super.endElement(name);
        --nestingLevel;
    }

    @Override
    public void writeCharacters(char[] characters, int offset, int length, boolean safe) {
        if (nestingLevel <= 0) { // inside text body of element
            buffer.append(characters, offset, length);
        } else {
            super.writeCharacters(characters, offset, length, safe);
        }
    }

    @Override
    public Spell clone() {
        return new TextTransformation(regex, transformation);
    }

    @Override
    public String toString() {
        return "TextTransformation(regex: " + regex + ", transformation: " + transformation + ")";
    }

    /**Indicates whether or not empty text is considered for transformation.
     * Per default it is not. That is with the following element
     * the transformation will have no effect:
     *
     *     <p></p>
     *
     * This is because there is no text.
     * If there was even only a single space in between there would be text.
     *
     * If this flag is set to true empty text will be considered,
     * meaning that #transform will be called with an empty string.
     *
     * @return the considerEmptyText
     */
    public boolean isConsiderEmptyText() {
        return considerEmptyText;
    }

    /**
     * @param considerEmptyText the considerEmptyText to set
     */
    public void setConsiderEmptyText(boolean considerEmptyText) {
        this.considerEmptyText = considerEmptyText;
    }

    private static class Replacement implements StringTransformation {
        private String value;

        public Replacement(String value) {
            this.value = value;
        }
        
        public String transform(String input) {
            return value;
        }
    }
}
