package wandledi.core;

import org.xml.sax.Attributes;
import static wandledi.util.Methods.select;


/**Note: This class has a natural ordering that is inconsistent with equals.
 *
 * @author Markus Kahl
 */
public class CssSelector implements Selector {

    private String id;
    private String label;
    private String elementClass;

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

        CssSelector ret;
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

    public boolean matches(String label, Attributes attributes) {

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
    public boolean equals(Object o) {

        if (o instanceof CssSelector) {
            CssSelector selector = (CssSelector) o;
            if (isId()) {
                return this.id.equals(selector.id);
            } else {
                boolean equals = true;
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
