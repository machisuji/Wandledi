package wandledi.java;

import wandledi.core.CssSelector;

public class Workspace {

    public static void main(String[] args) throws Exception {

        match(".info", "p.info");
    }

    public static void match(String s1, String s2) {

        CssSelector a = CssSelector.valueOf(s1);
        CssSelector b = CssSelector.valueOf(s2);
        System.out.println(s1 + " = " + s2 + ": " + a.equals(b));
    }
}
