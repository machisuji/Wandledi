package org.wandledi.spells;

/**A mapping for a so called 'spot' inside a text to be transformed.
 *
 * @author Markus Kahl
 */
public class SpotMapping {
    private String pattern;
    private StringTransformation[] transformations;
    private int transformationIndex = -1;
    private boolean regex = false;

    /**Creates a new SpotMappping which can be either name-based or regex-based.
     * A spot being any part of the text that is enclosed with parenthesis.
     * The text between those parenthesis is matched against a pattern.
     *
     * If the matching is name-based the pattern can have the following three functions:
     * <ul>
     *   <li>equals - The content must match the pattern (string) exactly.</li>
     *   <li>startsWith* - If the pattern ends with a * the content must start with the pattern.</li>
     *   <li>*endsWith - If the pattern starts with a * the content must end with the pattern.</li>
     * </ul>
     *
     * If the matching is regex-based the text between the parenthesis must simply match
     * the pattern which is interpreted as a regex.
     *
     * @param pattern The pattern against which to match the spots.
     * @param regex Indicates whether this mapping should be regex-based or name-based.
     * @param transformations The transformations to be applied to the matched spots' contents.
     */
    public SpotMapping(String pattern, boolean regex, StringTransformation... transformations) {
        this.pattern = pattern;
        this.transformations = transformations;
        this.regex = regex;
    }
    public SpotMapping(String pattern, StringTransformation... transformations) {
        this(pattern, false, transformations);
    }

    public SpotMapping(String pattern, boolean regex, String... values) {
        this(pattern, regex, new ArrayWriter(values));
    }
    public SpotMapping(String pattern, String... values) {
        this(pattern, false, values);
    }

    public StringTransformation nextTransformation() {
        if (transformationIndex + 1 < transformations.length) {
            ++transformationIndex;
        }
        return transformations[transformationIndex];
    }

    public String getPattern() {
        return pattern;
    }

    public boolean isRegex() {
        return regex;
    }

    public StringTransformation[] getTransformations() {
        return transformations;
    }
}
