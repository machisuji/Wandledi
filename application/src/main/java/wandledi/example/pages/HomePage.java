package wandledi.example.pages;

import java.util.Collection;
import java.util.LinkedList;
import wandledi.core.Attribute;
import wandledi.core.Scroll;
import wandledi.example.models.BlogEntry;
import wandledi.example.models.Comment;
import wandledi.java.html.PageImpl;
import wandledi.java.html.Plan;
import wandledi.java.html.Selectable;
import wandledi.java.html.SelectableElement;

import static wandledi.java.Switchboard.linkToUri;
import static wandledi.java.Switchboard.linkToId;

/**
 *
 * @author Markus Kahl
 */
public class HomePage extends PageImpl {

    private boolean login = false;

    public void beforeAction(String msg, String homeLink) {

        get(new Attribute("rel", "stylesheet")).setAttribute("href",
                linkToUri("/css/main.css"));
        get(".homelink").setAttribute("href", homeLink);
        if (msg != null) {
            get("#right").insert(msg);
        }
        if (login) {
            get("a", "href", "login").setAttribute("href", "post");
            get("button").replace(true, "Post Entry");
        }
    }

    public void index(Collection<BlogEntry> entries) {

        if (entries.isEmpty()) {
            get(".entry").setAttribute("id", "last");
            get(".heading").replace(true, "No Entries yet");
            get(".text").replace(true, "There are no entries yet. Why don't you start writing one?");
            get(".footer").hide();
        } else {
            get(".entry").foreachIn(entries).apply(new Plan<BlogEntry>() {
                public void execute(SelectableElement element, BlogEntry entry) {
                    if (last()) {
                        element.setAttribute("id", "last");
                    }
                    produceEntry(element, entry);
                }
            });
        }
    }

    public void login() {

        setFile("/home/index.xhtml");
        includeInEntry("/home/login.xhtml", this);
    }

    public void post() {

        post(null, null, null, null);
    }

    public void post(String msg, String author, String title, String content) {

        Scroll scroll = new Scroll("Post");
        if (author != null) {
            scroll.get(new Attribute("name", "author")).setAttribute("value", author);
        }
        if (title != null) {
            scroll.get(new Attribute("name", "title")).setAttribute("value", title);
        }
        if (content != null) {
            scroll.get(new Attribute("name", "content")).replace(true, content);
        }
        setFile("/home/index.xhtml");
        includeInEntry("/home/post.xhtml", scroll, this);
        get(".entry").changeAttribute("class", "form $val");
    }

    public void comments(final BlogEntry entry) {

        Collection<Comment> comments = new LinkedList<Comment>();

        comments.add(new Comment()); // turn first one into commented blog entry
        comments.addAll(entry.getComments());
        comments.add(new Comment()); // include comment form here
        setFile("/home/index.xhtml");
        get(".entry").foreachIn(comments).apply(new Plan<Comment>() {
            public void execute(SelectableElement element, Comment comment) {
                if (first()) { // blog entry
                    produceEntry(element, entry);
                } else {
                    if (last()) { // comment form
                        element.setAttribute("id", "last");
                        element.changeAttribute("class", "form $val");
                        includeInEntry("/home/comment.xhtml", element);
                    } else { // comment
                        produceComment(element, comment);
                        if (login) {
                            includeDeleteButton(element, comment, entry.getId());
                        }
                    }
                    element.changeAttribute("class", "comment $val");
                }
            }
        });
    }

    private void produceComment(SelectableElement element, Comment comment) {

        element.get(".heading").replace(true, "Comment from " + comment.getAuthor());
        element.get(".subheading").replace(true, comment.getDate().toString());
        element.get(".text").replace(true, comment.getContent());
        element.get(".footer").hide();
    }

    public void comment(BlogEntry entry) {

        comment(null, entry, null, null, null);
    }

    public void comment(String msg, BlogEntry entry, String author, String email, String content) {

        Scroll scroll = new Scroll("Post");
        if (author != null) {
            scroll.get(new Attribute("name", "author")).setAttribute("value", author);
        }
        if (email != null) {
            scroll.get(new Attribute("name", "email")).setAttribute("value", email);
        }
        if (content != null) {
            scroll.get(new Attribute("name", "content")).replace(true, content);
        }
        setFile("/home/index.xhtml");
        includeInEntry("/home/comment.xhtml", scroll, this);
        get(".entry").changeAttribute("class", "form $val");
        if (msg != null) {
            get("#right").insert(msg);
        }
    }

    private void produceEntry(SelectableElement element, BlogEntry entry) {

        element.get(".heading").replace(true, entry.getTitle());
        element.get(".user").replace(true, entry.getAuthor());
        element.get(".date").replace(true, entry.getDate().toString());
        element.get(".text").replace(true, entry.getContent());
        element.get(".footer").insert(entry.getComments().size() + " ");
        element.get(new Attribute("href", "comments")).setAttribute("href",
                linkToId("home", "comments", entry.getId()));
    }

    private void includeDeleteButton(SelectableElement element, Comment comment, long bid) {

        Scroll scroll = new Scroll();

        scroll.get("a").setAttribute("href",
                linkToId("home", "deleteComment", comment.getId()) + "?bid=" + bid);
        element.get("br").includeFile("/home/delete.xhtml", scroll);
    }

    private void includeInEntry(String file, Scroll scroll, Selectable selectable) {

        if (scroll == null) {
            selectable.get("p").includeFile(file);
        } else {
            selectable.get("p").includeFile(file, scroll);
        }
        selectable.get("span").hide();
        selectable.get("br").hide();
    }

    private void includeInEntry(String file, Selectable selectable) {

        includeInEntry(file, null, selectable);
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
}
