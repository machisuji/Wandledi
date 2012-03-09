package org.wandledi;

import org.wandledi.io.MagicReader;

/**
 * They see right through the magic.
 *
 * @author Markus Kahl
 * @version: 0.1
 * <p/>
 * 23.10.11
 */
public class DisenchantedAttributes implements org.xml.sax.Attributes {

    private org.xml.sax.Attributes attr;

    public DisenchantedAttributes(org.xml.sax.Attributes attr) {
        this.attr = attr;
    }

    protected String disenchant(String value) {
        return value != null ? value.replace(MagicReader.MAGIC_CHARACTER, '&') : null;
    }

    public String getValue(int index) {
        return disenchant(attr.getValue(index));
    }

    public String getValue(String uri, String localName) {
        return disenchant(attr.getValue(uri, localName));
    }

    public String getValue(String qName) {
        return disenchant(attr.getValue(qName));
    }

    public final int getLength() {
        return attr.getLength();
    }

    public final String getURI(int i) {
        return attr.getURI(i);
    }

    public final String getLocalName(int i) {
        return attr.getLocalName(i);
    }

    public final String getQName(int i) {
        return attr.getQName(i);
    }

    public final String getType(int i) {
        return attr.getType(i);
    }

    public final int getIndex(String s, String s1) {
        return attr.getIndex(s, s1);
    }

    public final int getIndex(String s) {
        return attr.getIndex(s);
    }

    public final String getType(String s, String s1) {
        return attr.getType(s, s1);
    }

    public final String getType(String s) {
        return attr.getType(s);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DisenchantedAttributes(");
        for (int i = 0; i < attr.getLength(); ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(attr.getLocalName(i));
            sb.append("=");
            sb.append(attr.getValue(i));
        }
        sb.append(")");
        return sb.toString();
    }
}
