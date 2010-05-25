package wandledi.example.controllers;

import java.text.DateFormat;
import java.util.Date;
import wandledi.example.pages.HomePages;
import wandledi.java.PageController;
import wandledi.java.html.Pages;

/**
 *
 * @author Markus Kahl
 */
public class Home2 extends PageController {

    private HomePages pages = new HomePages();

    public void index() {

        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());
        pages.index(time);
    }

    @Override
    public Pages getPages() {

        return pages;
    }
}
