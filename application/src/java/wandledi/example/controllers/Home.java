package wandledi.example.controllers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import wandledi.example.models.BlogEntry;
import wandledi.example.models.Comment;
import wandledi.example.pages.HomePage;
import wandledi.java.Database;
import wandledi.java.DefaultRoute;
import wandledi.java.PageController;
import wandledi.java.html.Page;

/**
 *
 * @author Markus Kahl
 */
public class Home extends PageController {

    protected HomePage page = new HomePage();

    @Override
    protected void beforeAction() {

        page.setLogin(getLogin());
        page.beforeAction((String) flash.get("msg"), DefaultRoute.getURI("home", "index"));
    }

    protected void setMessage(String msg) {

        page.get("#right").insert(msg);
    }

    public void index() {

        Collection<BlogEntry> entries = getEntries(param("test") != null);
        page.index(entries);
    }

    public void login() {

        if (isGetRequest()) {
            page.login();
        } else {
            String user = param("user");
            String password = param("password");

            if ("blogger".equals(user) && "reggolb".equals(password)) {
                setLogin(true);
                flash.put("msg", "Login successful");
            } else {
                flash.put("msg", "Login failed");
            }
            redirectTo("home");
        }
    }

    public void post() {

        if (isGetRequest()) {
            page.post();
        } else {
            BlogEntry entry = new BlogEntry(param("author"), param("title"), param("content"));
            if (entry.validate()) {
                getDatabase().persist(entry);
                flash.put("msg", "Entry saved");
                redirectTo("home");
            } else {
                page.post("Missing field", entry.getAuthor(), entry.getTitle(), entry.getContent());
            }
        }
    }

    public void comments() {

        BlogEntry entry = getDatabase().find(BlogEntry.class, Long.valueOf(param("id")));
        page.comments(entry);
    }

    public void comment() {

        Database db = getDatabase().withTransaction();
        BlogEntry entry = db.find(BlogEntry.class, Long.valueOf(param("id")));
        
        if (isGetRequest()) {
            page.comment(entry);
        } else {
            Comment comment = new Comment(param("author"), param("email"), param("content"));
            if (comment.validate()) {
                entry.getComments().add(comment);
                if (db.commitTransaction()) {
                    setMessage("Comment saved");
                } else {
                    setMessage("Could not save comment");
                }
                page.comments(entry);
            } else {
                page.comment("Missing field", entry, comment.getAuthor(),
                        comment.getEmail(), comment.getContent());
            }
        }
    }

    public void deleteComment() {

        Database db = getDatabase().withTransaction();
        BlogEntry entry = db.find(BlogEntry.class, Long.valueOf(param("bid")));
        Comment comment = db.find(Comment.class, Long.valueOf(param("id")));
        
        entry.getComments().remove(comment);
        if (db.commitTransaction()) {
            flash.put("msg", "Comment deleted");
        } else {
            flash.put("msg", "Could not delete comment");
        }
        redirectTo("home", "comments", Long.valueOf(param("bid")));
    }

    @Override
    public Page getPage() {

        return page;
    }

    protected void setLogin(boolean login) {

        session.put("login", login);
    }

    protected boolean getLogin() {

        Boolean login = (Boolean) session.get("login");
        return login != null ? login : false;
    }

    protected boolean isGetRequest() {

        return request.getMethod().equalsIgnoreCase("get");
    }

    protected Collection<BlogEntry> getEntries(boolean testData) {

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

    protected void addTestEntries(Collection<BlogEntry> entries) {

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
