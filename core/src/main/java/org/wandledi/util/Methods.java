package org.wandledi.util;

import org.wandledi.Attribute;

public class Methods {

    /**Selects the attribute with the given name from an array of attributes.
     *
     * @param name
     * @param attributes
     * @return The attribute's value or null if there is no such attribute.
     */
    public static final String select(String name, Attribute[] attributes) {

        for (Attribute attr: attributes) {
            if (attr.getName().equals(name)) {
                return attr.getValue();
            }
        }
        return null;
    }
}
