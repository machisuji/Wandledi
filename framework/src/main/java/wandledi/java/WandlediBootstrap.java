package wandledi.java;

import javax.servlet.ServletContext;

/**For a Wandledi application to work a class wandledi.config.Bootstrap
 * has to be defined which extends this abstract class.
 *
 * In #init() certain things may have to be set. These are:
 *
 * persistence unit - If you want to use wandledi.java.Database you have to set the persistence unit to use
 * message base name - If you want to use i18n you have to set the message base name in the Switchboard
 * controllers - You have to add all used controllers to the Switchboard
 *
 * In general if there is something you may need to set at startup,
 * WandlediBootstrap has a setter (or an "adder") for it. Refer to the respective method's
 * comments to find out whether or not you need them.
 *
 * @author Markus Kahl
 */
public abstract class WandlediBootstrap {

    public WandlediBootstrap() {
        
    }

    /**Init is called during start of the Wandledi application.
     * If there are tasks that have to be performed only once
     * in the lifecycle of an application, this is the place to call them.
     *
     * @param servletContext ServletContext of the Java Web Application
     */
    public abstract void init(ServletContext servletContext);

    /**Destroy is called at the end of a Wandledi application's lifecycle.
     */
    public abstract void destroy();

    /**Sets the OpenIDController's jsp file in which the OpenID forward
     * form is to be included.
     * You can also do this by using OpenIDController's respective method.
     *
     * @param file JSP file within the view directory.
     */
    public void setOpenIDFormJSP(String file) {

        OpenIDController.setFormJsp("/user/openid_forward.jsp");
    }

    /**Sets the persistence unit to be used for JPA-based database access.
     * You have to set the persistence unit before you can use wandledi.java.Database.
     * You can also do this by using Database's respective method.
     *
     * @param name The name of the persistence unit.
     */
    public void setPersistenceUnit(String name) {

        Database.setPersistenceUnit(name);
    }

    /**Sets the message base name of the ResourceBundle to be used for i18n
     * within the Wandledi application.
     * You can also do this by using Switchboard's respective method.
     *
     * @param messageBaseName ResourceBundles message base name
     */
    public void setMessageBaseName(String messageBaseName) {

        Switchboard.getInstance().setMessageBaseName(messageBaseName);
    }

    /**Adds the given controllers to the Switchboard.
     * A Controller *has* to be registered this way or it cannot
     * be used.
     * You can also do this directly by using Switchboard's respective methods.
     *
     * @param controllers The controller's classes
     */
    public void addControllers(Class<? extends Controller>... controllers) {

        Switchboard.getInstance().addControllers(controllers);
    }

    /**Adds the given routes to the Switchboard.
     * You can also do this by using wandledi.java.Switchboard#getRoutes().
     *
     * @param routes The routes to be added (the order matters!)
     */
    public void addRoutes(Route... routes) {

        for (int i = routes.length - 1; i >= 0; --i) {
            Switchboard.getInstance().getRoutes().insert(routes[i]);
        }
    }
}
