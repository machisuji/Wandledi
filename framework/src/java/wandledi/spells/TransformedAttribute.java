package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.Attribute;

/**
 *
 * @author Markus Kahl
 */
public class TransformedAttribute {

    private String name;
    private StringTransformation transformation;

    public TransformedAttribute(String name, StringTransformation transformation) {

        this.name = name;
        this.transformation = transformation;
    }

    public Attribute toAttribute(Attributes attributes) {

        return new Attribute(name, transformation.transform(attributes.getValue(name)));
    }
}
