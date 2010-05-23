package wandledi.java.resources;

/**
 *
 * @author markus
 */
public class Options {

    protected String field;
    protected boolean useTextArea = false;

    public Options(String field) {

        this.field = field;
    }

    public Options useTextArea() {

        this.useTextArea = true;
        return this;
    }
}
