package wandledi.java;

import wandledi.jetty.Application;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**Base class for any Controller. Provides access to important objects
 * and useful methods for all sorts of stuff.
 *
 * Note that except for stateful controllers (annotated with @Stateful) for every single
 * request a new instance is created to handle it.
 * So you can't rely on any member variables.
 *
 * With stateful controllers, you can. A class extending Controller inherits its
 * "serializablity". So here you can actually make use of member variables.
 * These, however, have to be serializable, too.
 * Which you only have to pay attention to, when you use complex types, though.
 * Strings and all primitive types are serializable by default and
 * therefore can be stored in the session.
 *
 * @author Markus Kahl
 */
public class Controller implements Serializable {

    protected static final long serialVersionUID = 20234289L;

    protected transient HttpServletRequest request;
    protected transient HttpServletResponse response;
    protected transient SessionData session;
    protected transient RequestData model;
    protected transient Flash flash;
    protected transient Database database;
    protected transient WandlediRequest wandlediRequest;

    public Controller() {

        wandlediRequest = new WandlediRequest();
    }

    /**Creates a new controller based on the given instance,
     * that is that instance has got to be initialized (#init has had to be called).
     *
     * @param parent
     */
    protected Controller(Controller parent) {

        this.request = parent.request;
        this.response = parent.response;
        this.session = parent.session;
        this.model = parent.model;
        this.flash = parent.flash;
        this.wandlediRequest = parent.wandlediRequest;
        this.database = parent.database;
    }

    /**Starts this controller as the whole application.
     *
     * @param nio Use NIO
     */
    public void runStandAlone(boolean nio) {

        Switchboard.getInstance().addController(getClass());
        Switchboard.getInstance().setSkipBootstrap(true);
        new Application().start(nio);
    }
    
    /**Called when a Controller is added to the Switchboard,
     * which usually happens only once.
     *
     */
    protected void controllerRegistered() {

    }

    /**Called before each action.
     * Override to do stuff before each action.
     */
    protected void beforeAction() {

    }

    /**Called after each action.
     * Override to do stuff after each action.
     */
    protected void afterAction() {

    }

    /**Returns this controller's name under which it is called in the URL.
     *
     * @return
     */
    public String getName() {

        String simpleName = getClass().getSimpleName();
        String name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        String pattern = "Controller";
        if (name.length() > pattern.length() && name.endsWith(pattern)) {
            name = name.substring(0, name.length() - pattern.length());
        }
        return name;
    }

    /**Marks the current request as viewless.
     * A viewless request does not have a JSP rendered as its response.
     *
     */
    public void setViewless() {

        wandlediRequest.setViewless(true);
    }

    /**Initializes the controller. This is called by the Switchboard
     * before any action is performed.
     *
     * @param request
     * @param response
     */
    public void init(HttpServletRequest request, HttpServletResponse response) {

        this.request = request;
        this.response = response;
        this.session = new SessionData(request.getSession());
        this.model = new RequestData(request);
        this.flash = Switchboard.getFlash(request);
    }

    protected WandlediRequest getWandlediRequest() {

        return wandlediRequest;
    }

    protected void setParam(String name, String value) {

        ((RequestWrapper)request).setParameter(name, value);
    }

    /**Returns the value for the parameter with the given name.
     *
     * @param name
     * @return
     */
    protected String param(String name) {

        return request.getParameter(name);
    }

    /**Returns the values for the parameter with the given name.
     *
     * @param name
     * @return
     */
    protected String[] params(String name) {

        return request.getParameterValues(name);
    }

    public boolean isSpellController() {

        return false;
    }

    /**Signals that the action wants to write output directly
     * with the response object rather than displaying a template.
     *
     * If this is not called, Wandledi will try to find and display
     * the respective template for the action.
     *
     */
    protected void doOutput() {

        setViewless();
    }

    /**Returns a PrintWriter to write the response without a JSP file.
     * I sets the current request viewless implicitly.
     *
     * @throws IOException
     */
    protected PrintWriter getWriter() {

        doOutput();
        try {
            return response.getWriter();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException("Failed to get the response writer.");
        }
    }

    /**Usually Wandledi tries to render the file <viewDirectory>/<controller>/<action>.jsp
     * for an action. By calling this method you can make Wandledi use another
     * file within the view directory instead.
     *
     * @param file
     */
    protected void renderFile(String file) {

        wandlediRequest.setView(file);
    }

    protected void renderAction(String action) {

        wandlediRequest.setView(getName() + "/" + action + ".jsp");
    }

    /**Redirects the current request to the given Controller's index action.
     * Only works if the controller is covered by the default route.
     *
     * @param controller
     */
    protected void redirectTo(String controller) {

        redirectTo(controller, "");
    }

    /**Redirects to the given Controller's Action.
     * Only works if the controller is covered by the default route.
     *
     * @param controller Target controller.
     * @param action Target controller's action to be called.
     * @param parameters Parameters to be appended.
     */
    protected void redirectTo(String controller, String action, Parameter... parameters) {

        redirectToUri(DefaultRoute.getURI(controller, action), parameters);
    }

    /**Redirects to the given Action.
     * This is equivalent to calling
     * Controller#redirectTo(action.getController(), action.getName(), action.getParameters())
     *
     * @param action The Action to be redirected to.
     */
    protected void redirectTo(Action action) {

        redirectTo(action.getController(), action.getName(),
                action.getParameters().toArray(new Parameter[action.getParameters().size()]));
    }

    /**Sends a redirect to the given url.
     *
     * @param url
     */
    protected void redirectToUrl(String url) {

        try {
            setViewless();
            response.sendRedirect(url);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**Sends a redirect to the given uri (string starting with '/').
     *
     * @param uri URI to be redirected to.
     * @param parameters GET parameters.
     */
    protected void redirectToUri(String uri, Parameter... parameters) {

        try {
            setViewless();
            StringBuilder url = new StringBuilder(request.getContextPath() + uri);
            if (parameters.length > 0) {
                Parameter first = parameters[0];
                url.append("?"); url.append(encode(first.getName()));
                url.append("="); url.append(encode(first.getValue()));
                for (int i = 1; i < parameters.length; ++i) {
                    Parameter param = parameters[i];
                    url.append("&"); url.append(encode(param.getName()));
                    url.append("="); url.append(encode(param.getValue()));
                }
            }
            response.sendRedirect(url.toString());
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**Redirects the current request to the action of the given controller
     * covered by the default route.
     *
     * @param controller
     * @param action
     * @param id
     */
    protected void redirectTo(String controller, String action, long id) {

        try {
            setViewless();
            response.sendRedirect(request.getContextPath() +
                    DefaultRoute.getURI(controller, action, id));
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**Sends an error with the given message and (HTTP) code.
     *
     * @param code
     * @param message
     */
    protected void error(int code, String message) {
        
        try {
            setViewless();
            response.sendError(code, message);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**Encodes the given string in UTF-8 for being passed as a GET parameter in a URL.
     *
     * @param value
     * @return
     */
    public static String encode(String value) {

        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String decode(String value) {

        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected ServletContext getServletContext() {

        return Switchboard.getInstance().getServletContext();
    }

    /**Returns a Database instance to use for this request.
     * It will be closed implicitly at the end of the request.
     *
     * @return
     */
    public Database getDatabase() {

        if (database == null) {
            database = (Database)request.getAttribute("database");
            if (database == null) {
                database = new Database();
                request.setAttribute("database", database);
            }
        }
        return database;
    }

    protected Messages getMessages() {

        return Switchboard.getInstance().getMessages(request);
    }
}
