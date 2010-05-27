package wandledi.java;

import wandledi.core.Selector;

public class Workspace {

    public static void main(String[] args) {

        match(".info", "p.info");
        match("p.info", ".info");
        match("p", "p.info");
        match("p.info", "p");
    }

    public static void match(String s1, String s2) {

        Selector a = Selector.valueOf(s1);
        Selector b = Selector.valueOf(s2);
        System.out.println(s1 + " = " + s2 + ": " + a.equals(b));
    }
}
