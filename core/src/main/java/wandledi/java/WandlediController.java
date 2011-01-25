package wandledi.java;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**This interface has to be implemented by each class that wants to
 * be a Controller for Wandledi.
 *
 * @author Markus Kahl
 */
public interface WandlediController {

    /**
     * Returns this controller's name under which it is called in the URL.
     *
     * @return
     */
    String getName();

    /**Initializes the controller. This is called by the Switchboard
     * before any action is performed.
     *
     * @param request
     * @param response
     */
    void init(HttpServletRequest request, HttpServletResponse response);

    /**Indicates whether this controller uses JSP or Wandledi's Spells.
     *
     * @return
     */
    boolean isSpellController();

    /**Called when the controller is registered with the Switchboard.
     */
    void controllerRegistered();

    /**Called before each action.
     * Override to do stuff before each action.
     */
    void beforeAction();

    /**Called after each action.
     * Override to do stuff after each action.
     */
    void afterAction();

    /**Returns the current WandlediRequest which contains information about
     * Wandledi specific settings.
     *
     * @return
     */
    WandlediRequest getWandlediRequest();
}
