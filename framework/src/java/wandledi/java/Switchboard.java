package wandledi.java;

import java.io.FileReader;
import wandledi.java.annotations.Stateful;
import wandledi.java.annotations.InterceptWith;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import wandledi.core.Scroll;
import wandledi.core.Wandler;

/**The Switchboard is the central Wandledi component.
 * It dispatches requests to Wandledi controllers.
 *
 * @author Markus Kahl
 */
public class Switchboard {

    private static Switchboard singleton = new Switchboard();
    private Controllers controllers = new Controllers();
    private Routes routes = new Routes();
    private ServletContext servletContext;
    private String messageBaseName;
    private Map<String, Messages> messages = new HashMap<String, Messages>();
    private String viewDirectory = "/WEB-INF/view/";
    private boolean skipBootstrap = false;

    public static Switchboard getInstance() {

        return singleton;
    }

    public static void setInstance(Switchboard switchboard) {

        singleton = switchboard;
    }

    /**Set by stand-alone controllers for instance.
     * This way the controlelrs are registered with the Switchboard from somewhere
     * else than the Bootstrap.
     *
     * @param skipBootstrap
     */
    protected void setSkipBootstrap(boolean skipBootstrap) {

        this.skipBootstrap = skipBootstrap;
    }

    protected boolean getSkipBootstrap() {

        return skipBootstrap;
    }

    public static String getUrlPlusQuery(HttpServletRequest request) {

        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        if (query != null && query.length() > 0) {
            url.append("?");
            url.append(query);
        }
        return url.toString();
    }

    /**Adds the given controllers to the switchboard.
     *
     * @param controllers
     */
    public void addControllers(Class<? extends Controller>... controllers) {

        for (Class<? extends Controller> controller : controllers) {
            addController(controller);
        }
    }

    /**Adds the given controller to the switchboard.
     *
     * @param controller
     */
    public void addController(Class<? extends Controller> controller) {

        try {
            Controller instance = controller.newInstance();
            addController(instance.getName(), controller);
            instance.controllerRegistered();
        } catch (InstantiationException ex) {
            throw new RuntimeException("Cannot create controller!", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(
                    "A controller needs to have a public empty constructor!", ex);
        }
    }

    /**Adds the given controller to the switchboard.
     *
     * @param name A custom name for the controller.
     * @param controller
     */
    public void addController(String name, Class<? extends Controller> controller) {

        controllers.put(name, controller, isStateful(controller));
    }

    /**Tries dispatch current request to a Wandledi controller.
     *
     * @param request
     * @param response
     * @return True if the request has been dispatched to a Wandledi controller, false otherwise.
     * @throws IOException
     * @throws ServletException
     */
    public boolean dispatch(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Action action = routes.match(request);
        if (action == null || !controllers.contains(action.getController())) {
            return false;
        } else {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
        }
        boolean dispatched = false;
        try {
            HttpServletRequest wRequest = new RequestWrapper(request, action.getParameters());
            Controller controller = controller(action.getController(), wRequest, response);
            if (controller != null) {
                setImplicitObjects(wRequest);
                if (controller.isSpellController()) {
                    PageController pc = (PageController) controller;
                    pc.getPages().setRequest(request);
                    if (i18n()) {
                        pc.getPages().setMessages(getMessages(request));
                    }
                }
                if (performAction(controller, action.getController(), action.getName(),
                        request, response)) {
                    if (!controller.getWandlediRequest().isViewless()) {
                        if (!controller.isSpellController()) {
                            if (i18n()) {
                                request.setAttribute("message", getMessages(request));
                            }
                            String template = controller.getWandlediRequest().getView() == null
                                    ? template(action.getController(), action.getName())
                                    : template(controller.getWandlediRequest().getView());
                            request.getRequestDispatcher(template).include(wRequest, response);
                        } else {
                            wandle(controller, action, 
                                    template(action.getController(),
                                    action.getName()), request, response);
                        }
                    }
                    dispatched = true;
                }
                getFlash(request).purge();
            }
        } finally {
            closeDatabaseConnection(request);
        }
        return dispatched;
    }

    private void wandle(Controller controller, Action action, String template,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        PageController pc = (PageController) controller;
        Scroll scroll = pc.getPages().getScroll();
        Wandler wandler = new Wandler();
        if (pc.getPages().getFile() != null) {
            template = viewDirectory + pc.getPages().getFile();
        }
        template = template.substring(viewDirectory.length());
        if (!template.startsWith("/")) {
            template = "/" + template;
        }
        template = servletContext.getRealPath(template.replace(".jsp", ".xhtml"));
        wandler.useScroll(scroll);
        wandler.wandle(new FileReader(template), response.getWriter());
    }

    private void closeDatabaseConnection(HttpServletRequest request) {

        Database database = (Database) request.getAttribute("database");
        if (database != null) {
            database.close();
        }
    }

    protected Messages getMessages(HttpServletRequest request) {

        String lang = (String) request.getSession().getAttribute("lang");
        if (lang == null) {
            lang = Locale.getDefault().getLanguage();
        }
        // not thread safe @TODO fix it (get away from lazy initialization)
        if (!messages.containsKey(lang)) {
            messages.put(lang, new Messages(messageBaseName, lang));
        }
        return messages.get(lang);
    }

    protected void setImplicitObjects(HttpServletRequest request) {

        request.setAttribute("flash", getFlash(request));
        request.setAttribute("root", "http://" + request.getServerName()
                + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "")
                + request.getContextPath() + "/");
        request.setAttribute("this", request.getRequestURI());
    }

    public static Flash getFlash(HttpServletRequest request) {

        HttpSession session = request.getSession(true); // @TODO abhängig von @Stateful
        if (session != null) {
            Flash flash = (Flash) session.getAttribute("flash");
            if (flash == null) {
                flash = new Flash();
                session.setAttribute("flash", flash);
            }
            return flash;
        }
        return null;
    }

    private void callAction(Callable call, HttpServletResponse response) throws IOException {
        try {
            call.call();
        } catch (Exception ex) {
            Logger.getLogger(Switchboard.class.getName()).log(Level.SEVERE, null, ex);
            response.sendError(500, ex.getMessage());
        }
    }

    /**Gets the controller for the given path (url).
     *
     * @param request
     * @param response
     * @return
     */
    private Controller controller(String name, HttpServletRequest request,
            HttpServletResponse response) {

        if (name != null) {
            if (controllers.isStateful(name)) {
                return savedController(name, request, response);
            } else {
                return newController(name, request, response);
            }
        }
        return null;
    }

    private void intercept(InterceptWith interceptWith, HttpServletRequest request,
            HttpServletResponse response, Callable call, Method action, Controller controller) {
        try {
            Interceptor interceptor = interceptWith.value().newInstance();
            interceptor.init(request, response);
            interceptor.call = call;
            interceptor.action = action.getName();
            interceptor.controller = controller.getName();
            interceptor.intercept();
        } catch (InstantiationException ex) {
            Logger.getLogger(Switchboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Switchboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**Creates a new controller instance to handle the given request.
     *
     * @param request
     * @param response
     * @return
     */
    private Controller newController(String name, HttpServletRequest request,
            HttpServletResponse response) {

        try {
            Controller controller = (Controller) controllers.get(name).newInstance();
            controller.init(request, response);

            return controller;
        } catch (InstantiationException ex) {
            Logger.getLogger(Switchboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Switchboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Switchboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Switchboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void execute(Controller controller, Method action, Callable call,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (action.isAnnotationPresent(InterceptWith.class)) {
            InterceptWith interceptWith = action.getAnnotation(InterceptWith.class);
            intercept(interceptWith, request, response, call, action, controller);
        } else if (controller.getClass().isAnnotationPresent(InterceptWith.class)) {
            InterceptWith interceptWith = controller.getClass().getAnnotation(InterceptWith.class);
            boolean exception = false;
            for (String except : interceptWith.except()) {
                if (action.getName().equals(except)) {
                    exception = true;
                }
            }
            if (!exception) {
                intercept(interceptWith, request, response, call, action, controller);
            } else {
                callAction(call, response);
            }
        } else {
            callAction(call, response);
        }
    }

    /**Retrieves an existing controller instance from the session to handle the given request.
     *
     * @param request
     * @param response
     * @return
     */
    private Controller savedController(String name, HttpServletRequest request,
            HttpServletResponse response) {
        Controller ec = (Controller) request.getSession().getAttribute(name);
        if (ec == null) {
            ec = newController(name, request, response);
        } else {
            ec.init(request, response);
        }
        return ec;
    }

    /**Performs the given action of a controller.
     *
     * @param controller The controller whose action is to be performed.
     * @param action The name of the action (method) to be called.
     */
    private boolean performAction(final Controller controller, String controllerName, String action,
            HttpServletRequest request, HttpServletResponse response) throws IOException {

        Method m = controllers.get(controllerName, action);
        if (m == null) {
            m = lookupMethod(controller, action);
            controllers.put(controllerName, action, m);
        }
        final Method method = m;
        if (method == null) {
            return false;
        }
        Callable call = new Callable() {

            public Object call() throws Exception {
                controller.beforeAction();
                method.invoke(controller);
                controller.afterAction();
                return null;
            }
        };
        execute(controller, method, call, request, response);
        return true;
    }

    protected Method lookupMethod(Controller controller, String action) {

        Method[] methods = controller.getClass().getMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers()) && method.getName().equals(action)
                    && method.getReturnType().isAssignableFrom(void.class)
                    && method.getParameterTypes().length == 0) {
                return method;
            }
        }
        return null;
    }

    protected final boolean relation(Class<?> classA, Class<?> classB) {

        return (classA == classB) || (contains(classA.getInterfaces(), classB));
    }

    protected final boolean contains(Class[] array, Class clazz) {

        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    protected static String uri(HttpServletRequest request) {

        return request.getRequestURI().substring(request.getContextPath().length()).trim();
    }

    protected String template(String controller, String action) {

        String file = controller + "/" + action + ".jsp";
        return viewDirectory + file;
    }

    /**
     * @param file file relative to the view directory
     * @return
     */
    protected String template(String file) {

        return viewDirectory + file;
    }

    private boolean isStateful(Class<?> controller) {

        return controller.isAnnotationPresent(Stateful.class);
    }

    public ServletContext getServletContext() {

        return servletContext;
    }

    protected void setServletContext(ServletContext servletContext) {

        this.servletContext = servletContext;
    }

    public Routes getRoutes() {

        return routes;
    }

    /**Sets the default baseName for the ResourceBundle to be used for i18n
     * within the Wandledi application.
     *
     * @param messageBaseName
     */
    public void setMessageBaseName(String messageBaseName) {

        this.messageBaseName = messageBaseName;
        this.messages.put(Locale.getDefault().getLanguage(), new Messages(messageBaseName));
    }

    public String getMessageBaseName() {

        return this.messageBaseName;
    }

    protected boolean i18n() {

        return messageBaseName != null;
    }

    /**Sets the directory in which the JSP files for the controllers' views
     * are searched for. The default value is "/WEB-INF/view/".
     * Wandledi expects to find an action's JSP file at the following
     * location: /WEB-INF/view/controller/action.jsp
     *
     * @param viewDirectory
     */
    public void setViewDirectory(String viewDirectory) {

        this.viewDirectory = viewDirectory;
    }

    public String getViewDirectory() {

        return viewDirectory;
    }
}
