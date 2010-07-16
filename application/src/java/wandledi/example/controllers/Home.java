package wandledi.example.controllers;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
        String msg = (String) flash.get("msg");
        Boolean login = (Boolean) session.get("login");
        Collection<BlogEntry> entries = getEntries(param("test") != null);

        pages.index(time, msg, login != null ? login : false, entries);
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
            BlogEntry entry = new BlogEntry(param("author"), param("title"), param("content"));
            if (entry.validate()) {
                getDatabase().persist(entry);
                flash.put("msg", "Entry saved");
                redirectTo("home");
            } else {
                pages.post("Missing field", entry.getAuthor(), entry.getTitle(), entry.getContent());
            }
        }
    }

    @Override
    public Pages getPages() {

        return pages;
    }

    private Collection<BlogEntry> getEntries(boolean testData) {

        List<BlogEntry> entries;
        if (testData) {
            entries = new LinkedList<BlogEntry>();
            addTestEntries(entries);
            Collections.reverse(entries);
        } else {
            entries = getDatabase().query(BlogEntry.class,
                    "SELECT e FROM BlogEntry AS e ORDER BY e.date DESC");
        }
        return entries;
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
}
