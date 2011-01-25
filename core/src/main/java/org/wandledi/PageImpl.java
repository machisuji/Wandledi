package wandledi.java.html;

import wandledi.core.*;
import wandledi.java.Messages;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

/**An HTML page. Normally it should provide one public method for each
 * page that is going to be displayed by the associated PageController.
 *
 * @author Markus Kahl
 */
public class PageImpl extends SelectableImpl implements Page {

    private HttpServletRequest request;
    private Messages messages;
    private String file;

    public PageImpl() {

        super(new Scroll());
    }

    public String msg(Object key, Object... arguments) {

        String msg = arguments.length == 0 ?
                getMessages().get(key.toString()) :
                MessageFormat.format(getMessages().get(key.toString()), arguments);
        return msg != null ? msg : "";
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
