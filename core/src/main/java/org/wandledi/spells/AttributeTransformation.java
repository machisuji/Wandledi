package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.wandledi.AbstractSpell;
import org.wandledi.Attribute;
import org.wandledi.SimpleAttributes;
import org.wandledi.Spell;
import java.util.List;
import java.util.LinkedList;

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
                List<Attribute> transformed = new LinkedList<Attribute>();
                for (TransformedAttribute attr : attributes) {
                    Attribute tAttr = attr.toAttribute(atts);
                    if (tAttr != null) {
                        transformed.add(tAttr);
                    }
                }
                return transformed.toArray(new Attribute[transformed.size()]);
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
