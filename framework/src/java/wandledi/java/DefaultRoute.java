package wandledi.java;

/**A default route matching URIs for controllers:
 *
 * /:controller/:id/:action
 *
 * The only mandatory value is :controller.
 * There are certain restrictions as to what values are allowed
 * for the different parts.
 * For :controller and :action any valid Java identifier is allowed.
 * The :id has to be number.
 *
 * Example for a URI matched by a default route:
 *
 * http://wandledi.com/user/33/show/Machisuji?highlight=something
 *
 * The resulting fields of the matching default route would be:
 *
 *      contoller   - user
 *      id          - 33
 *      action      - show
 *
 * @author Markus Kahl
 */
public class DefaultRoute extends Route {

    public DefaultRoute() {

        super(
                "/([a-zA-Z][a-zA-Z0-9_-]*)/" + // controller
                "((\\d+)/)?" + // ID
                "([a-zA-Z_][a-zA-Z0-9_-]*)?" + // action
                "(/.+)?", // nonsense
                1, 4, new ParameterMapping("id", 3));
    }

    /**Gets the URI to the given controller's action called with the given id.
     *
     * @param controller
     * @param action
     * @param id
     * @return
     */
    public static String getURI(String controller, String action, long id) {

        return "/" + controller + "/" + id + "/" + (!action.equals("index") ? action : "");
    }

    /**Gets the URI to the given controller's action.
     *
     * @param controller
     * @param action
     * @return
     */
    public static String getURI(String controller, String action) {

        return "/" + controller + "/" + (!action.equals("index") ? action : "");
    }
}
