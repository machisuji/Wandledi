package wandledi.core;

import org.xml.sax.Attributes;


/**Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @author Markus Kahl
 */
public class Selector implements Comparable {

    private String id;
    private String label;
    private String elementClass;

    protected Selector(String label, String elementClass, String id) {

        this.label = label;
        this.elementClass = elementClass;
        this.id = id;
    }

    public Selector(String id) {

        this(null, null, id);
    }

    public Selector(String label, Attributes attributes) {

        this(label, attributes.getValue("class"), attributes.getValue("id"));
    }

    public static Selector valueOf(String selector) {

        Selector ret;
        if (selector.indexOf("#") != -1) { // ids
            String id = selector.substring(selector.indexOf('#') + 1);
            ret = new Selector(id);
        } else if (selector.indexOf(".") != -1) { // classes
            String klass = selector.substring(selector.indexOf('.') + 1);
            String label = selector.substring(0, selector.indexOf('.'));
            ret = new Selector(label.length() > 0 ? label : null, klass, null);
        } else { // labels
            String label = selector;
            ret = new Selector(label, null, null);
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

    /**DOES NOT GIVE A DAMN ABOUT THE EQUALS-HASHCODE CONTRACT !!!
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {

        if (o instanceof Selector) {
            Selector selector = (Selector) o;
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
                /*
                boolean gotLabel = selector.gotLabel();
                boolean gotClass = selector.gotElementClass();
                boolean equals = gotLabel || gotClass;
                if (gotLabel) {
                    equals &= selector.label.equals(this.label);
                }
                if (gotClass) {
                    equals &= selector.elementClass.equals(this.elementClass);
                }
                */
                return equals;
            }
        }
        return false;
    }

    public int compareTo(Object o) {

        if (o instanceof Selector) {
            Selector selector = (Selector) o;
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
