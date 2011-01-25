package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.Attribute;

/**
 *
 * @author markus
 */
public interface AttributeTransformationIntent {

    /**Returns an array of attributes to be added the element.
     * Already existing attributes will be overriden.
     * 
     * @param element Element whose attributes are transformed.
     * @param attributes The element's existing attributes.
     * @return The new attributes.
     */
    public Attribute[] getAttributes(String element, Attributes attributes);
}
