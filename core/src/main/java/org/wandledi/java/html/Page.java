package wandledi.java.html;

import wandledi.core.Scroll;
import wandledi.java.Messages;

/**
 *
 * @author Markus Kahl
 */
public interface Page {

    public void setFile(String file);
    public String getFile();
    public void setMessages(Messages messages);
    public Messages getMessages();
    public Scroll getScroll();
}
