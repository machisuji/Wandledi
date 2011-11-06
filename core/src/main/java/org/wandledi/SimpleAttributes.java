package org.wandledi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;

/**
 *
 * @author Markus Kahl
 */
public class SimpleAttributes implements Attributes {

    private Attribute[] attributes;

    public SimpleAttributes(Attribute... attributes) {

        this.attributes = attributes;
    }

    public SimpleAttributes(Attributes attributes) {

        this.attributes = new Attribute[attributes.getLength()];
        for (int i = 0; i < this.attributes.length; ++i) {
            this.attributes[i] = new Attribute(attributes.getLocalName(i), attributes.getValue(i));
        }
    }

    public SimpleAttributes(Attributes attributes, Attribute... more) {

        List<Attribute> attrList = linkedListFrom(more);
        List<Attribute> duplicates = new LinkedList<Attribute>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            int index = indexOf(more, attributes.getLocalName(i));
            if (index != -1) {
                duplicates.add(attrList.remove(index));
            }
        }
        this.attributes = new Attribute[attributes.getLength() + more.length - duplicates.size()];
        int offset = attributes.getLength();
        for (int i = 0; i < offset; ++i) {
            this.attributes[i] = new Attribute(attributes.getLocalName(i), attributes.getValue(i));
            if (duplicates.size() > 0) {
                Attribute duplicate = findAttribute(duplicates, this.attributes[i].getName());
                if (duplicate != null) {
                    this.attributes[i].setName(duplicate.getName());
                    this.attributes[i].setValue(duplicate.getValue());
                }
            }
        }
        for (int i = 0; i < attrList.size(); ++i) {
            this.attributes[i + offset] = attrList.get(i);
        }
    }

    private LinkedList<Attribute> linkedListFrom(Attribute... attributes) {

        LinkedList<Attribute> list = new LinkedList<Attribute>();
        for (Attribute attr: attributes) {
            list.add(attr);
        }
        return list;
    }

    private int indexOf(Attribute[] attributes, String name) {

        for (int i = 0; i < attributes.length; ++i) {
            if (attributes[i].getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private Attribute findAttribute(Collection<Attribute> attributes, String attributeName) {

        Iterator<Attribute> a = attributes.iterator();
        while (a.hasNext()) {
            Attribute attr = a.next();
            if (attr.getName().equals(attributeName)) {
                a.remove();
                return attr;
            }
        }
        return null;
    }

    public int getLength() {
        return attributes.length;
    }

    public String getLocalName(int index) {
        return attributes[index].getName();
    }

    public String getQName(int index) {
        return getLocalName(index);
    }

    public String getValue(int index) {
        return attributes[index].getValue();
    }

    public int getIndex(String uri, String localName) {
        return getIndex(localName);
    }

    public int getIndex(String qName) {
        for (int i = 0; i < attributes.length; ++i) {
            if (attributes[i].getName().equals(qName)) {
                return i;
            }
        }
        return -1;
    }

    public String getValue(String uri, String localName) {
        return getValue(localName);
    }

    public String getValue(String qName) {
        for (int i = 0; i < attributes.length; ++i) {
            if (attributes[i].getName().equals(qName)) {
                return attributes[i].getValue();
            }
        }
        return null;
    }

    public String getURI(int index) {
        return "";
    }

    public String getType(int index) {
        return "CDATA";
    }

    public String getType(String uri, String localName) {
        return "CDATA";
    }

    public String getType(String qName) {
        return "CDATA";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < attributes.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(attributes[i].toString());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        for (Attribute attr: attributes) {
            hash += 7 * (attr.getName().hashCode() + 5 * attr.getValue().hashCode());
        }
        return hash;
    }
}
