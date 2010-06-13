package wandledi.java.html;

import wandledi.core.*;
import wandledi.java.DefaultRoute;
import wandledi.java.Messages;
import wandledi.java.Switchboard;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**An HTML page.
 *
 * @author Markus Kahl
 */
public class Pages extends SelectableImpl {

    private HttpServletRequest request;
    private Messages messages;
    private String file;

    public Pages() {

        super(new Scroll());
    }

    public String msg(Object key, Object... arguments) {

        String msg = arguments.length == 0 ?
                getMessages().get(key) :
                MessageFormat.format(getMessages().get(key), arguments);
        return msg != null ? msg : "";
    }

    public String linkToUri(String uri) {

        return Switchboard.getInstance().getServletContext().getContextPath() + uri;
    }

    public String linkTo(String controller, String action) {

        return Switchboard.getInstance().getServletContext().getContextPath() +
                DefaultRoute.getURI(controller, action);
    }

    public String linkToId(String controller, String action, Long id) {

        return Switchboard.getInstance().getServletContext().getContextPath() +
                DefaultRoute.getURI(controller, action, id);
    }

    public void setFile(String file) {
        
        this.file = file;
    }

    public String getFile() {

        return file;
    }

    public void setMessages(Messages messages) {

        this.messages = messages;
    }

    public Messages getMessages() {

        return messages;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
