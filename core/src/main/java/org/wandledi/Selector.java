package org.wandledi;

import org.xml.sax.Attributes;

/**Aside from the methods declared here, a Selector needs to implement
 * equals() and hashCode() properly.
 *
 */
public interface Selector extends Comparable {

    /**Tries to match this selector against the element with the given
     * label and attributes.
     *
     * @param label
     * @param attributes
     * @return
     */
    boolean matches(String label, Attributes attributes);
}
