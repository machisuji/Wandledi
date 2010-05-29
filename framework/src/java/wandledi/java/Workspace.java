package wandledi.java;

import wandledi.core.Scroll;
import wandledi.core.Selector;
import wandledi.core.Wandler;
import wandledi.java.html.Pages;

import javax.servlet.http.HttpServletResponse;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Workspace {

    public static void main(String[] args) throws Exception {

        match(".info", "p.info");
    }

    public static void match(String s1, String s2) {

        Selector a = Selector.valueOf(s1);
        Selector b = Selector.valueOf(s2);
        System.out.println(s1 + " = " + s2 + ": " + a.equals(b));
    }
}
