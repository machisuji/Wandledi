package wandledi.java.html;

import java.util.Collection;

/**
 *
 * @author markus
 */
public class ExampleController {

    private ExamplePages pages = new ExamplePages();

    public void index() {

        pages.index();
    }

    public void guestbook() {

        // logic
        Collection<Message> messages = findMessages();
        // ... even more logic
        // in the end, call the pages and provide the necessary data
        pages.guestbook(messages);
    }

    public Collection<Message> findMessages() {

        return null;
    }

    public Pages getPages() {

        return pages;
    }
}
