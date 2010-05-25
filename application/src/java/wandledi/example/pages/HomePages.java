package wandledi.example.pages;

import wandledi.java.html.Pages;

/**
 *
 * @author Markus Kahl
 */
public class HomePages extends Pages {

    public void index(String time) {

        get("#time").replace(true, time);
    }
}