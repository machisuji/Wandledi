package wandledi.example.pages;

import wandledi.java.html.Pages;

/**
 *
 * @author Markus Kahl
 */
public class HomePages extends Pages {

    public void index(String time) {

        get("#time").replace(true, time);
        get(".info").insertLast(" Wandledi is great!");
        get("#time").setAttribute("style", "color: red;");
        get("p").setAttribute("style", "font-weight: bold;");
    }
}