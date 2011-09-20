package org.wandledi;

import org.xml.sax.Attributes;

import java.util.List;

/**Aside from the methods declared here, a Selector needs to implement
 * equals() and hashCode() properly.
 *
 */
public interface Selector extends Comparable<Selector> {

    /**Tries to match this selector against the element with the given
     * label and attributes.
     *
     *
     * @param label
     * @param attributes
     * @param elementPath This element's context, that is path from document root down to it.
     * @return
     */
    boolean matches(String label, Attributes attributes, List<ElementStart> elementPath);
}
