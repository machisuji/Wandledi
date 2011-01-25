package wandledi.java;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author Markus Kahl
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private Collection<Parameter> parameters;
    private Map<String, String> map;
    private boolean joined = false;
    private List<String> names;

    public RequestWrapper(HttpServletRequest request, Collection<Parameter> parameters) {

        super(request);
        this.parameters = parameters;
        this.map = new HashMap<String, String>(parameters.size());
        for (Parameter parameter: parameters) {
            map.put(parameter.getName(), parameter.getValue());
        }
    }

    public void setParameter(String name, String value) {

        this.map.put(name, value);
    }

    @Override
    public String getParameter(String name) {
        
        String value = super.getParameter(name);
        if (value == null) {
            value = map.get(name);
        }
        return value;
    }

    @Override
    public Map getParameterMap() {
        
        if (!joined) {
            map.putAll(super.getParameterMap());
            joined = true;
        }
        return map;
    }

    @Override
    public Enumeration getParameterNames() {

        if (names == null) {
            names = new LinkedList<String>();
            Enumeration parameterNames = super.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                names.add((String)parameterNames.nextElement());
            }
            for (Parameter parameter: parameters) {
                names.add(parameter.getName());
            }
        }
        return Collections.enumeration(names);
    }
}
