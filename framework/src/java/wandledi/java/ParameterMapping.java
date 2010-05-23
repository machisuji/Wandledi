package wandledi.java;

/**
 *
 * @author Markus Kahl
 */
public class ParameterMapping {

    private String name;
    private int group;

    public ParameterMapping(String name, int group) {

        this.name = name;
        this.group = group;
    }

    public String getName() {

        return name;
    }

    public int getGroup() {

        return group;
    }
}
