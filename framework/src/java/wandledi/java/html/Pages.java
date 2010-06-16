package wandledi.java.html;

import wandledi.core.*;
import wandledi.java.DefaultRoute;
import wandledi.java.Messages;
import wandledi.java.Switchboard;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

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

    /**Returns a new Selectable to select elements with.
     * The Selection is relative to the element matched by the
     * given selector and works only below it in the element tree.
     *
     * @param selector CSS selector
     * @return A new Selectable
     */
    public Selectable at(String selector) {

        return at(CssSelector.valueOf(selector));
    }

    /**Returns a new Selectable to select elements with.
     * The Selection is relative to the element matched by the
     * given selector and works only below it in the element tree.
     *
     * @param selector a selector
     * @return A new Selectable
     */
    public Selectable at(Selector selector) {

        Scroll nestedScroll = new Scroll();
        LocalSpells localSpell = new LocalSpells(scroll, nestedScroll);
        scroll.addSpell(selector, localSpell);

        return new SelectableImpl(nestedScroll);
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
