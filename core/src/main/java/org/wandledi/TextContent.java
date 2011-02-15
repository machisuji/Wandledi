package org.wandledi;

import org.wandledi.spells.SpotMapping;
import org.wandledi.spells.StringTransformation;

/**Provides convenience methods for several TextTransformations
 * for a given Element.
 *
 * @author Markus Kahl
 */
public interface TextContent {

    /**Each chunk of the target element's text is passed to the
     * given StringTransformation and replaced with its result.
     *
     * @param st
     */
    void transform(StringTransformation st);

    /**Each match of the given regex is passed to the given
     * StringTransformation and replaced with its result.
     *
     * @param regex
     * @param st
     */
    void transform(String regex, StringTransformation st);

    /**Same as String#replacAll(String, String) where the string
     * is each chunk of the target element's text.
     *
     * @param regex
     * @param replacement
     */
    void replaceAll(String regex, String replacement);

    /**Discards each chunk of text of the target element and replaces
     * each one with the given String.
     *
     * @param content
     */
    void setContent(String content);

    /**Inserts the given values at 'spots' within the target element's
     * text. A spot is any part of the text that is enclosed with parenthesis.
     *
     * @param values
     */
    void insert(String... values);

    /**Inserts values at the 'spots' within the target element's text
     * according to the given SpotMappings.
     *
     * @param mappings
     */
    void insert(SpotMapping... mappings);
}
