package wandledi.test;

import org.wandledi.*;
import org.wandledi.spells.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class HTML5 {

    public static String DIR = "core/src/test/java/wandledi/test/";

    private SelectableImpl page;
    private Wandler wandler;
    private Random random;

    public HTML5() {
        wandler = Wandler.forHTML();
        wandler.setResources(new Resources() {
            public Reader open(String resource) throws IOException {
                return new FileReader(DIR + resource);
            }
        });
        random = new Random();
    }

    public String wandle(String file) {
        StringWriter output = new StringWriter();
        FileReader input = null;
        try {
            input = new FileReader(DIR + file);
            wandler.useScroll(page.getScroll());
            wandler.wandle(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try { input.close(); } catch (IOException e) { }
            }
        }
        return output.toString();
    }

    public static Document parseXML(String xml) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @BeforeMethod
    public void setUp() {
        page = new SelectableImpl(new Scroll());
    }

    @Test
    public void simpleTest() {
        Element e = page.get("h1");
        e.replace(true, "buyakasha");

        String result = wandle("html5.html");
        Document doc = parseXML(result);
        NodeList hs = doc.getElementsByTagName("h1");

        int occurences = 0;
        for (int i = 0; i < hs.getLength(); ++i) {
            if ("buyakasha".equals(hs.item(i).getTextContent())) {
                ++occurences;
            }
        }
        assertEquals(occurences, 1, "number of occurences");
    }

    @Test
    public void replication() {
        // Element e = page.at("footer").get("section"); [old-way]
        Element e = page.get("footer section"); // [new-way]
        e.foreachIn(Arrays.asList(1, 2, 3)).apply(new Plan<Integer>() {
            public void execute(SelectableElement e, final Integer i) {
                e.setAttribute("id", "footer-" + i); // doesn't work the new way, but the old way
                e.get("img").setAttribute("title", new StringTransformation() {
                    public String transform(String input) {
                        return input + " (" + i + ")";
                    }
                });
            }
        });

        String result = wandle("html5.html"); System.out.println(result);
        Document doc = parseXML(result);
        NodeList sections = doc.getElementsByTagName("section");

        int occurences = 0;
        for (int i = 0; i < sections.getLength(); ++i) {
            Node id = sections.item(i).getAttributes().getNamedItem("id");
            if (id != null && id.getTextContent().startsWith("footer-")) {
                ++occurences;
            }
        }
        assertEquals(occurences, 3, "number of footer sections");
    }
}