package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.wandledi.AbstractSpell;
import org.wandledi.Attribute;
import org.wandledi.SimpleAttributes;
import org.wandledi.Spell;

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

    public AttributeTransformation(final TransformedAttribute... attributes) {

        this(new AttributeTransformationIntent() {
            public Attribute[] getAttributes(String element, Attributes atts) {
                Attribute[] ret = new Attribute[attributes.length];
                for (int i = 0; i < ret.length; ++i) {
                    ret[i] = attributes[i].toAttribute(atts);
                }
                return ret;
            }
        });
    }

    @Override
    public Spell clone() {

        return new AttributeTransformation(intent);
    }

    @Override
    public void startTransformedElement(String name, Attributes attributes) {

        if (ignoreBounds()) {
            startElement(name, attributes);
        } else {
            super.startTransformedElement(name, new SimpleAttributes(attributes,
                    intent.getAttributes(name, attributes)));
        }
    }

    public String toString() {

        StringBuilder sb = new StringBuilder("AttrTrans [");
        Attribute[] attributes = intent.getAttributes("", new AttributesImpl());
        for (int i = 0; i < attributes.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(attributes[i].getName());
            sb.append(" -> '");
            sb.append(attributes[i].getValue());
            sb.append("'");
        }
        sb.append("]");

        return sb.toString();
    }
}
