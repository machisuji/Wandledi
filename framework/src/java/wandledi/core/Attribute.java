package wandledi.core;

/**
 *
 * @author Markus Kahl
 */
public class Attribute {

    private String name;
    private String value;

    public Attribute(String name, String value) {

        this.name = name;
        this.value = value;
    }

    public Attribute(String name) {

        this.name = name;
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

    public void setValue(String value) {

        this.value = value;
    }
}
