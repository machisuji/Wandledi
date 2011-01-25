package wandledi.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

/**A route matches a certain request and maps it onto a controller and respective action.
 *
 * @author Markus Kahl
 */
public class Route {

    protected Pattern pattern;
    protected int controller;
    protected int action;
    protected ParameterMapping[] parameters;

    /**Creates a new route matching the given pattern.
     *
     * @param pattern The pattern to be matched.
     * @param controller The group which captures the controller.
     * @param action The group which captures the action.
     * @param parameters Additional (optional) groups to be mapped onto parameters.
     */
    public Route(Pattern pattern, int controller, int action, ParameterMapping... parameters) {

        this.pattern = pattern;
        this.controller = controller;
        this.action = action;
        this.parameters = parameters;
    }

    /**Creates a new route matching the given regex.
     *
     * @param regex The regex to be matched.
     * @param controller The group which captures the controller.
     * @param action The group which captures the action.
     * @param parameters Additional (optional) groups to be mapped onto parameters.
     */
    public Route(String regex, int controller, int action, ParameterMapping... parameters) {

        this(Pattern.compile(regex), controller, action, parameters);
    }

    public Action match(HttpServletRequest request) {

        String uri = Switchboard.uri(request);
        Matcher matcher = matcher = pattern.matcher(uri);
        if (matcher.matches()) {
            Action match = new Action(getController(matcher), getAction(matcher));
            for (ParameterMapping mapping: parameters) {
                String value = matcher.group(mapping.getGroup());
                if (value != null) {
                    match.getParameters().add(new Parameter(mapping.getName(), value));
                }
            }
            checkAction(match, matcher);
            return match;
        }
        return null;
    }

    /**Checks the created action before returning it as a match for this route.
     *
     * @param match
     * @param matcher
     */
    public void checkAction(Action match, Matcher matcher) {

    }

    /**Returns the name of the matched action.
     *
     * @param matcher
     * @return The action's name or 'index' if there was no match.
     */
    public String getAction(Matcher matcher) {

        String actionName = matcher.group(action);
        return actionName != null ? actionName : "index";
    }

    /**Returns the name of the matched controller.
     *
     * @param matcher
     * @return The controller's name or 'home' if there was no match.
     */
    public String getController(Matcher matcher) {

        String controllerName = matcher.group(controller);
        return controllerName != null ? controllerName : "home";
    }

    @Override
    public String toString() {

        return "Route " + pattern.pattern();
    }
}
