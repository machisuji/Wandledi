package org.wandledi;

import org.xml.sax.Attributes;

import java.util.List;

import static org.wandledi.util.Methods.select;


/**Note: This class has a natural ordering that is inconsistent with equals.
 *
 * This class implements simple CSS Selectors.
 * It does not support selection after arbitrary attributes such as in
 * 'input[type="submit"]' for now.
 *
 * @author Markus Kahl
 */
public class CssSelector implements Selector {

    private String id;
    private String label;
    private String elementClass;
    private CssSelector[] parents = new CssSelector[0];

    protected CssSelector(String label, String elementClass, String id) {

        this.label = label;
        this.elementClass = elementClass;
        this.id = id;
    }

    public CssSelector(String id) {
        this(null, null, id);
    }

    public CssSelector(String label, Attributes attributes) {
        this(label, attributes.getValue("class"), attributes.getValue("id"));
    }

    public CssSelector(String label, Attribute... attributes) {
        this(label, select("class", attributes), select("id", attributes));
    }

    public static CssSelector valueOf(String selector) {
        String[] selectors = selector.split(" ");
        CssSelector sel = parseSingleSelector(selectors[selectors.length - 1]);
        if (selectors.length > 1) {
            sel.parents = new CssSelector[selectors.length - 1];
            for (int i = 0; i < selectors.length - 1; ++i) {
                sel.parents[i] = parseSingleSelector(selectors[i]);
            }
        }
        return sel;
    }

    private static CssSelector parseSingleSelector(String selector) {
        CssSelector ret;
        int attrListIndex = selector.indexOf("[");
        String attrList = null;
        if (attrListIndex != -1) {
            attrList = selector.substring(attrListIndex, selector.length() - 1);
            selector = selector.substring(0, attrListIndex);
        }
        if (selector.indexOf("#") != -1) { // ids
            String id = selector.substring(selector.indexOf('#') + 1);
            ret = new CssSelector(id);
        } else if (selector.indexOf(".") != -1) { // classes
            String klass = selector.substring(selector.indexOf('.') + 1);
            String label = selector.substring(0, selector.indexOf('.'));
            ret = new CssSelector(label.length() > 0 ? label : null, klass, null);
        } else { // labels
            String label = selector;
            ret = new CssSelector(label, null, null);
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CssSelector parent: parents) {
            sb.append(parent.toString());
            sb.append(" ");
        }
        if (isId()) {
            sb.append("#");
            sb.append(id);
        } else {
            if (gotLabel()) {
                sb.append(label);
            }
            if (gotElementClass()) {
                sb.append(".");
                sb.append(elementClass);
            }
        }
        return sb.toString();
    }

    public boolean matches(String label, Attributes attributes, List<ElementStart> elementPath) {
        if (parents.length > 0) {
            int i = 0;
            for (ElementStart e: elementPath) {
                if (i == parents.length) break;
                if (parents[i].equals(e.getName(), e.getAttributes())) {
                    ++i;
                }
            }
            if (i != parents.length) return false;
        }
        return equals(label, attributes);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CssSelector) {
            CssSelector selector = (CssSelector) o;
            if (isId()) {
                return this.id.equals(selector.id);
            } else {
                boolean equals = gotLabel() || gotElementClass();
                if (gotLabel()) {
                    equals &= this.label.equals(selector.label);
                }
                if (gotElementClass()) {
                    equals &= this.elementClass.equals(selector.elementClass);
                }
                return equals;
            }
        }
        return false;
    }

    protected boolean equals(String label, Attributes attributes) {
        if (isId()) {
            return this.id.equals(attributes.getValue("id"));
        } else {
            boolean equals = gotLabel() || gotElementClass();
            if (gotLabel()) {
                equals &= this.label.equals(label);
            }
            if (gotElementClass()) {
                equals &= this.elementClass.equals(attributes.getValue("class"));
            }
            return equals;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        if (isId()) {
            hash += 5 * id.hashCode();
        } else {
            if (gotLabel()) {
                hash += 7 * label.hashCode();
            }
            if (gotElementClass()) {
                hash += 11 * elementClass.hashCode();
            }
        }
        return hash;
    }

    public int compareTo(Object o) {
        if (o instanceof CssSelector) {
            CssSelector selector = (CssSelector) o;
            if (this.isId() && selector.isId()) {
                return 0;
            } else if (this.isId() && !selector.isId()) {
                return -2;
            } else if (!this.isId() && selector.isId()) {
                return 2;
            } else {
                if (this.gotElementClass() && !selector.gotElementClass()) {
                    return -1;
                } else if (!this.gotElementClass() && selector.gotElementClass()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return -10;
        }
    }

    public final boolean isId() {
        return id != null;
    }

    public final boolean gotLabel() {
        return label != null;
    }

    public final boolean gotElementClass() {
        return elementClass != null;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the elementClass
     */
    public String getElementClass() {
        return elementClass;
    }
}
