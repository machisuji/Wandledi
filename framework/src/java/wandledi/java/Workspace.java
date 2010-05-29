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

        wandle();
    }

    public static void match(String s1, String s2) {

        Selector a = Selector.valueOf(s1);
        Selector b = Selector.valueOf(s2);
        System.out.println(s1 + " = " + s2 + ": " + a.equals(b));
    }

    private static void wandle() throws IOException {

        WorkPages pages = new WorkPages();
        pages.index("22:43");
        Scroll scroll = pages.getScroll();
        Wandler wandler = new Wandler();
        String template = "/home/markus/Entwicklung/java/Wandledi/application/web/WEB-INF/view/home2/index.xhtml";
        String output = "/home/markus/Desktop/index.html";
        wandler.useScroll(scroll);
        wandler.wandle(new FileReader(template), new FileWriter(output));
    }
}

class WorkPages extends Pages {

    public void index(String time) {

        get("#time").replace(true, time);
        get(".info").insertLast(" Wandledi is great!");
        get("#time").setAttribute("style", "color: red;");
        get("div").setAttribute("style", "font-weight: bold;");
    }
}
