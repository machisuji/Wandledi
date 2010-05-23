package wandledi.java;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Markus Kahl
 */
public class Routes {
    
    private LinkedList<Route> routes = new LinkedList<Route>();

    public Routes() {

        routes.add(new DefaultRoute());
        routes.add(new Route(
                "/(home/)?" + // controller
                "([a-zA-Z_][a-zA-Z0-9_-]*)?", 1, 2)); // action
    }

    public Action match(HttpServletRequest request) {

        for (Route route: routes) {
            Action action = route.match(request);
            if (action != null) {
                return action;
            }
        }
        return null;
    }

    public void insert(Route route) {

        routes.addFirst(route);
    }

    public void append(Route route) {

        routes.addLast(route);
    }

    public List<Route> list() {

        return routes;
    }
}
