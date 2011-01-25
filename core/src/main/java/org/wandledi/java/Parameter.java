package wandledi.java;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**Route Parameter for custom Routes.
 * For instance: /subject/show/5/page/:page
 *
 * In this case :page would be such a route parameter.
 * For a controller this parameter will look like a normal
 * GET parameter and can, in fact, also be used as such.
 *
 * @author Markus Kahl
 */
public class Parameter implements Serializable {

    protected static final long serialVersionUID = 94989232323849L;

    private String name;
    private String value;

    public Parameter(String name, String value) {

        this.name = name;
        this.value = value;
    }

    public String getName() {

        return name;
    }

    public String getValue() {

        return value;
    }

    /**Extracts all GET parameters from the given request.
     *
     * @param request The request whose parameters are to be listed.
     * @return A list of the extracted GET parameters.
     */
    public static List<Parameter> listFrom(HttpServletRequest request) {

        List<Parameter> parameters = new LinkedList<Parameter>();
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            parameters.add(new Parameter(name, request.getParameter(name)));
        }
        return parameters;
    }
}
