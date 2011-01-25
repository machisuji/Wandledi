package wandledi.taglib;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import wandledi.java.DefaultRoute;
import wandledi.java.Switchboard;

/**
 *
 * @author Markus Kahl
 */
public class Core {

    /**Returns a link to the given URI which is relative to the context of
     * the web application.
     *
     * @param uri URI starting with '/'
     * @return
     */
    public static String linkToUri(String uri) {

        return Switchboard.getInstance().getServletContext().getContextPath() + uri;
    }

    public static String linkTo(String controller, String action) {

        return Switchboard.getInstance().getServletContext().getContextPath() +
                DefaultRoute.getURI(controller, action);
    }

    public static String linkToId(String controller, String action, Long id) {

        return Switchboard.getInstance().getServletContext().getContextPath() +
                DefaultRoute.getURI(controller, action, id);
    }

    public static String message(String lang, String key, Object... arguments) {

        return MessageFormat.format(
                ResourceBundle.getBundle(Switchboard.getInstance().getMessageBaseName(),
                    new Locale(lang)).getString(key),
                arguments);
    }

    public static String message1(String lang, String key, Object argument) {

        return MessageFormat.format(
                ResourceBundle.getBundle(Switchboard.getInstance().getMessageBaseName(),
                    new Locale(lang)).getString(key),
                new Object[] { argument });
    }

    public static String inputSafe(String text) {
        String safe = text.replace(">", "&gt;");
        safe = safe.replace("<", "&lt;");
        safe = safe.replace("\"", "&quot;");
        return safe;
    }
}
