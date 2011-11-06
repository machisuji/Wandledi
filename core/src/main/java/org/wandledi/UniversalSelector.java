package org.wandledi;

import org.xml.sax.Attributes;

import java.util.List;

import static org.wandledi.util.Methods.select;

/**
 * A UniversalSelector can match an element against arbitrary attributes.
 */
public class UniversalSelector implements Selector {

    protected String label;
    protected Attribute[] attributes;

    public UniversalSelector(String label, String attrName, String attrValue) {

        this(label, new Attribute(attrName, attrValue));
    }

    /**
     * Creates a new UniversalSelector
     *
     * @param label (optional) element label, if null, any element will be matched regardless of its label
     * @param attributes element attributes, must contain all attributes of this selector to match
     */
    public UniversalSelector(String label, Attribute... attributes) {
        this.label = label;
        this.attributes = attributes;
    }

    /**Creates a new UniversalSelector without a label.
     *
     * @param attributes If this array is empty, the selector will match any element.
     */
    public UniversalSelector(Attribute... attributes) {

        this.attributes = attributes;
    }

    /**Tries to match this selector against the element with the given
     * label and attributes.
     *
     *
     * @param label
     * @param attributes
     * @param elementPath
     * @return
     */
    public boolean matches(String label, Attributes attributes, List<ElementStart> elementPath) {

        boolean matches = this.label != null ? this.label.equals(label) : true;
        if (matches) {
            for (Attribute attr: this.attributes) {
                matches &= attr.getValue().equals(attributes.getValue(attr.getName()));
            }
        }
        return matches;
    }

    public int compareTo(Selector o) {

        if (o instanceof CssSelector) {
            return 10;
        } else if (o instanceof UniversalSelector) {
            return 0;
        } else {
            return -10;
        }
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder("UniversalSelector(<");
        if (label != null) {
            sb.append(label);
        } else {
            sb.append("*");
        }
        for (Attribute attr: attributes) {
            sb.append(" ");
            sb.append(attr.getName());
            sb.append("=\"");
            sb.append(attr.getValue());
            sb.append("\"");
        }
        sb.append("/>)");
        return sb.toString();
    }

    @Override
    public int hashCode() {

        int hash = label != null ? 3 * label.hashCode() : 3;
        for (Attribute attr: attributes) {
            hash += 5 * attr.getName().hashCode();
            hash += 7 * attr.getValue().hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof UniversalSelector) {
            UniversalSelector that = (UniversalSelector) o;
            boolean equal = this.label != null ?
                    this.label.equals(that.label) :
                    that.label == null;
            equal &= this.attributes.length == that.attributes.length;
            for (int i = 0; equal && i < attributes.length; ++i) {
                Attribute attr = this.attributes[i];
                equal &= attr.getValue().equals(select(attr.getName(), that.attributes));
            }
            return equal;
        }
        return false;
    }
}
