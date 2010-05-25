package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;
import wandledi.core.Attribute;
import wandledi.core.SimpleAttributes;

/**
 *
 * @author Markus Kahl
 */
public class AttributeTransformation extends AbstractSpell {

    private AttributeTransformationIntent intent;

    public AttributeTransformation(AttributeTransformationIntent intent) {

        this.intent = intent;
    }

    public AttributeTransformation(final Attribute... attributes) {

        this(new AttributeTransformationIntent() {
            public Attribute[] getAttributes(String element, Attributes atts) {
                return attributes;
            }
        });
    }

    public void startTransformedElement(String name, Attributes attributes) {

        super.startElement(name, new SimpleAttributes(attributes, 
                intent.getAttributes(name, attributes)));
    }

    public void endTransformedElement(String name) {
        super.endElement(name);
    }
}
