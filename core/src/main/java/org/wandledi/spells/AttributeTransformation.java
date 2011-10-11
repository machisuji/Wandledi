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
    private boolean merge; // merge Intent's result with actual attributes?

    public AttributeTransformation(AttributeTransformationIntent intent, boolean merge) {
        this.intent = intent;
        this.merge = merge;
    }

    public AttributeTransformation(AttributeTransformationIntent intent) {
        this(intent, true);
    }

    /**Creates a new AttributeTransformation which sets an Element's attributes.
     * Already existing attributes will be overridden.
     *
     * @param attributes Attributes to set.
     */
    public AttributeTransformation(final Attribute... attributes) {
        this(new AttributeTransformationIntent() {
            public Attribute[] getAttributes(String element, Attributes atts) {
                return attributes;
            }

            public String toString() {
                return "AttributeTransformationIntent(" + attributes.toString() + ")";
            }
        });
    }

    /**Creates a new AttributeTransformation which changes existing attributes.
     * Transformations for attributes which don't already exist will have no effect.
     *
     * @param attributes Transformations to be applied to an Element's attributes.
     */
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

            public String toString() {
                return "AttributeTransformationIntent(" + attributes + ")";
            }
        });
    }

    /**Creates an AttributeTransformation that _removes_ an Element's attributes.
     *
     * @param attributesToBeRemoved The names of the attributes to be removed.
     */
    public AttributeTransformation(final String... attributesToBeRemoved) {
        this(new AttributeTransformationIntent() {
            public Attribute[] getAttributes(String element, Attributes attributes) {
                List<Attribute> remains = new LinkedList<Attribute>();
                main: for (int i = 0; i < attributes.getLength(); ++i) {
                    String attr = attributes.getLocalName(i);
                    for (int j = 0; j < attributesToBeRemoved.length; ++j) {
                        if (attr.equalsIgnoreCase(attributesToBeRemoved[j])) continue main; // don't add (->remove) this one
                    }
                    remains.add(new Attribute(attr, attributes.getValue(i)));
                }
                return remains.toArray(new Attribute[remains.size()]);
            }

            public String toString() {
                return "AttributeTransformationIntent(remove: " + attributesToBeRemoved + ")";
            }
        }, false);
    }

    @Override
    public Spell clone() {
        return new AttributeTransformation(intent, merge);
    }

    @Override
    public void startTransformedElement(String name, Attributes attributes) {
        if (ignoreBounds()) {
            startElement(name, attributes);
        } else {
            Attributes newAttributes = merge ?
                    new SimpleAttributes(attributes, intent.getAttributes(name, attributes)) :
                    new SimpleAttributes(intent.getAttributes(name, attributes));
            super.startTransformedElement(name, newAttributes);
        }
    }

    public String toString() {
        return "AttributeTransformation(merge: " + merge + ", intent: " + intent + ")";
    }
}
