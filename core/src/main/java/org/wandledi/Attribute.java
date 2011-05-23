package org.wandledi;

/**
 *
 * @author Markus Kahl
 */
public class Attribute {

    private String name;
    private String value;

    /**Creates a new Attribute.
     *
     * @param name
     * @param value Cannot be null, if you give in null here the value is set to the empty string implicitly.
     */
    public Attribute(String name, String value) {

        this.name = name;
        this.value = value != null ? value : "";
    }

    public Attribute(String name) {

        this.name = name;
        this.value = "";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**Sets this Attribute's value.
     *
     * @param value Cannot be null, if you give in null here the value is set to the empty string implicitly.
     */
    public void setValue(String value) {

        this.value = value != null ? value : "";
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
