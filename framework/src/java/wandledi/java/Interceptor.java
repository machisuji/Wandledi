package wandledi.java;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Markus Kahl
 */
public abstract class Interceptor extends Controller {

    protected String controller;
    protected String action;
    protected Callable call;

    /**Called before any accordingly annotated action.
     * 
     * @param controller
     * @param action
     * @return True if the request is to be intercepted in the sense that the target action
     * will not be called. False otherwise.
     */
    public abstract void intercept();

    /**Calls the intercepted action.
     */
    protected void callInterceptedAction() {

        try {
            call.call();
        } catch (Exception ex) {
            Logger.getLogger(Interceptor.class.getName()).log(Level.SEVERE, null, ex);
            error(500, ex.getMessage());
        }
    }

    /**Returns the intercepted action complete with parameters.
     *
     * @return
     */
    protected Action getInterceptedAction() {

        return new Action(controller, action, Parameter.listFrom(request));
    }

    protected String getInterceptedUrl() {

        return Switchboard.getUrlPlusQuery(request);
    }

    /**Returns the name of the controller whose call has been intercepted.
     *
     * @return
     */
    protected String getController() {

        return controller;
    }

    /**Returns the name of the action which has been intercepted.
     *
     * @return
     */
    protected String getAction() {

        return action;
    }
}
