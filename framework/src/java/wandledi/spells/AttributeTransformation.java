package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;
import wandledi.core.Attribute;
import wandledi.core.SimpleAttributes;
import wandledi.core.Spell;

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

    @Override
    public Spell clone() {

        return new AttributeTransformation(intent);
    }

    @Override
    public void startTransformedElement(String name, Attributes attributes) {

        super.startTransformedElement(name, new SimpleAttributes(attributes,
                intent.getAttributes(name, attributes)));
    }
}
