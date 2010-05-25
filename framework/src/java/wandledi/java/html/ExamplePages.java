package wandledi.java.html;

import java.util.Collection;

/**
 *
 * @author Markus Kahl
 */
public class ExamplePages extends Pages {

    public void index() {

        setFile("/example/index.html");
        // <span id="time"></span> -> <span id="time">11/11/2011 11:11</span>
        get("#time").insert(new java.util.Date().toString());
        // <form action="#"> -> <form action="/user/login">
        get("form").setAttribute("action", "/user/login");
        // <h1 style="font-weight: bold;"> -> <h1 style="color: red; font-weight: bold;">
        get("h1").setAttribute("style", "color: red; %v");
        // <div id="post">...</div> -> <div id="post">...</div><div id="post">...</div>
        get("#post").clone(2);

        if (isUser()) {
            get(".welcome").hide();
        } else {
            get(".user").insert(getUser());
        }
    }
    String getUser() { return "Jim"; }
    boolean isUser() { return false; }

    public void guestbook(Collection<Message> messages) {

        get("input.author").setAttribute("name", "author");
        get("textarea.content").setAttribute("name", "content");
        get("form.message").setAttribute("action", "guestbookEntry");
        get(".messages").clone(messages.size());
        get(".message").foreachIn(messages).cast(new Spell1<Message>() {
            public void hex(Element e, Message msg) {
                e.get(".author").insert(msg.author + " (" + msg.createdAt + ")");
                e.get(".content").insert(msg.content);
            }
        });
    }
}
