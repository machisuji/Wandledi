package wandledi.core;

import org.xml.sax.Attributes;

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
