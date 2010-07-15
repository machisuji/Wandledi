package wandledi.example.controllers;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import wandledi.example.models.BlogEntry;
import wandledi.example.pages.HomePages;
import wandledi.java.PageController;
import wandledi.java.html.Pages;

/**
 *
 * @author Markus Kahl
 */
public class Home extends PageController {

    private HomePages pages = new HomePages();

    public void index() {

        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        Boolean login = (Boolean) session.get("login");
        Collection<BlogEntry> entries = new LinkedList<BlogEntry>();
        addTestEntries(entries);

        pages.index(time, (String) flash.get("msg"), login != null ? login : false, entries);
    }

    private void addTestEntries(Collection<BlogEntry> entries) {

        BlogEntry entry1 = new BlogEntry();
        entry1.setTitle("Is young Gouda really better?");
        entry1.setContent("Scientists have found out, that this matter is entirely one of taste.");

        BlogEntry entry2 = new BlogEntry();
        entry2.setTitle("Left 4 Dead");
        entry2.setContent("The Plant I have been given as a present is about to die. " +
                "Unfortunately I didn't seem to be able to provide it with enough water.");

        entries.add(entry1);
        entries.add(entry2);
    }

    public void login() {

        if (request.getMethod().equalsIgnoreCase("get")) {
            pages.login();
        } else {
            String user = param("user");
            String password = param("password");

            if ("blogger".equals(user) && "reggolb".equals(password)) {
                session.put("login", true);
                flash.put("msg", "Login successful");
            } else {
                flash.put("msg", "Login failed");
            }
            redirectTo("home");
        }
    }

    public void post() {

        if (request.getMethod().equalsIgnoreCase("get")) {
            pages.post();
        } else {
            
        }
    }

    @Override
    public Pages getPages() {

        return pages;
    }
}
