package wandledi.java;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**A Controller's action.
 *
 * @author Markus Kahl
 */
public class Action implements Serializable {

    protected static final long serialVersionUID = 2033090193L;
    private String controller;
    private String name;
    private Collection<Parameter> parameters = new LinkedList<Parameter>();

    public Action(String controller, String name) {

        this.controller = controller;
        this.name = name;
    }

    public Action(String controller, String name, Collection<Parameter> parameters) {

        this(controller, name);
        this.parameters = parameters;
    }

    public Collection<Parameter> getParameters() {

        return parameters;
    }

    public String getController() {

        return controller;
    }

    public void setController(String controller) {

        this.controller = controller;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
