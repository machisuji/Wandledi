package org.wandledi;

import org.wandledi.spells.SpotMapping;
import org.wandledi.spells.StringTransformation;
import org.wandledi.spells.TextTransformation;

/**Provides convenience methods for several TextTransformations
 * for a given Element.
 *
 * @author Markus Kahl
 */
public class TextContentImpl implements TextContent {
    private Element element;

    public TextContentImpl(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public void transform(StringTransformation st) {
        element.cast(new TextTransformation(st));
    }

    public void transform(String regex, StringTransformation st) {
        element.cast(new TextTransformation(regex, st));
    }

    public void replaceAll(String regex, String replacement) {
        element.cast(new TextTransformation(regex, replacement));
    }

    public void setContent(String content) {
        TextTransformation tt = new TextTransformation(null, content);
        tt.setConsiderEmptyText(true);
        element.cast(tt);
    }

    public void insert(String... values) {
        element.cast(new TextTransformation(values));
    }

    public void insert(SpotMapping... mappings) {
        element.cast(new TextTransformation(mappings));
    }
}
