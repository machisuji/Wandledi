package wandledi.test;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import wandledi.core.*;
import wandledi.java.html.Element;
import wandledi.java.html.ElementImpl;
import wandledi.java.html.Pages;
import wandledi.java.html.Plan;
import wandledi.spells.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;

public class SpellExperiment {

    public static String DIR = "src/test/wandledi/test/";

    Pages pages;
    Wandler wandler = new Wandler();
    Random random = new Random();

    @BeforeMethod
    public void setUp() {
        
        pages = new Pages();
    }

    @Test
    public void testAttributeTransformation() {

        String style = "background-color: black;";
        pages.get("body").setAttribute("style", style);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList bodies = doc.getElementsByTagName("body");

        assertEquals(bodies.getLength(), 1);
        assertEquals(bodies.item(0).getAttributes().getNamedItem("style").getTextContent(), style);
    }

    @Test
    public void testDuplication() {

        int number = 1 + random.nextInt(10);
        pages.get("#time").clone(number);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList spans = doc.getElementsByTagName("span");

        assertEquals(spans.getLength(), number);
        for (int i = 0; i < spans.getLength(); ++i) {
            for (int j = 0; j < spans.getLength(); ++j) {
                if (i != j) {
                    assertTrue(spans.item(i).isEqualNode(spans.item(j)));
                }
            }
        }
    }

    @Test
    public void testTripleStringInsertion() {

        String start = String.valueOf(random.nextInt(1000));
        String start2 = String.valueOf(random.nextInt(1000));
        String end = String.valueOf(random.nextInt(1000));
        pages.get("title").insert(start);
        pages.get("title").insert(start2);
        pages.get("title").insertLast(end);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList titles = doc.getElementsByTagName("title");

        assertEquals(titles.getLength(), 1);
        assertTrue(titles.item(0).getTextContent().startsWith(start), "Title starts with " + start);
        assertTrue(titles.item(0).getTextContent().startsWith(start + start2),
                "Title starts with " + start + start2);
        assertTrue(titles.item(0).getTextContent().endsWith(end), "Title ends with " + end);
    }

    @Test
    public void testXmlInsertion() {

        final String style = "font-weight: bold;";
        final String id = String.valueOf(random.nextInt(1000));
        pages.get("body").insert(true, new InsertionIntent() {
            public void insert(Spell parent) {
                parent.startElement("p", new SimpleAttributes(new Attribute("id", id)));
                parent.writeString("Just follow ");
                parent.startElement("span", new SimpleAttributes(new Attribute("style", style)));
                parent.writeString("the");
                parent.endElement("span");
                parent.writeString(" day!");
                parent.endElement("p");
            }
        });

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        Node body = doc.getElementsByTagName("body").item(0);
        Node p = body.getLastChild();

        assertEquals(p.getNodeName(), "p");
        assertEquals(p.getAttributes().getNamedItem("id").getTextContent(), id);
        assertEquals(p.getChildNodes().getLength(), 3);
        assertEquals(p.getTextContent(), "Just follow the day!");
        assertEquals(p.getChildNodes().item(1).getNodeName(), "span");
        assertEquals(p.getChildNodes().item(1).getAttributes().getNamedItem("style").getTextContent(), style);
    }

    @Test
    public void testInclusion() {

        pages.get(".info").cast(new Inclusion("inclusion.xhtml") {
            public String getPath(String file) {
                return DIR + file;
            }
        });
        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        assertEquals(doc.getElementsByTagName("div").getLength(), 1);
        assertEquals(doc.getElementsByTagName("p").getLength(), 1);
        assertTrue(doc.getElementsByTagName("p").item(0).getTextContent().contains("inclusion"),
                "Text from inclusion.xhtml included.");
    }

    @Test
    public void testVariantInclusion() {

        String style = "color: red;";
        Scroll scroll = new Scroll();
        scroll.changeAttributes("p", new Attribute("style", style));
        pages.get(".info").cast(new Inclusion("inclusion.xhtml", scroll) {
            public String getPath(String file) {
                return DIR + file;
            }
        });
        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList divs = doc.getElementsByTagName("div");
        NodeList paragraphs = doc.getElementsByTagName("p");
        assertEquals(divs.getLength(), 1);
        assertEquals(paragraphs.getLength(), 1);
        assertTrue(paragraphs.item(0).getTextContent().contains("inclusion"),
                "Text from inclusion.xhtml included.");
        assertEquals(paragraphs.item(0).getAttributes().getNamedItem("style").getTextContent(),
                style, "The paragraph's style should be: " + style);
    }

    @Test
    public void testInvisibility() {

        pages.get("h1").hide();

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        assertEquals(doc.getElementsByTagName("h1").getLength(), 0);
    }

    @Test
    public void testReplacement() {

        String title = String.valueOf(random.nextInt(1000));
        final String time = "11:05";
        pages.get("title").replace(true, title);
        pages.get("#time").replace(false, new ReplacementIntent() {
            public void replace(String label, Attributes attributes, Spell parent) {
                assertEquals(label, "span");
                assertEquals(attributes.getValue("id"), "time");
                parent.startElement("b", new SimpleAttributes());
                parent.writeString(time);
                parent.endElement("b");
            }
        });
        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList titles = doc.getElementsByTagName("title");
        NodeList spans = doc.getElementsByTagName("span");
        NodeList bolds = doc.getElementsByTagName("b");

        assertEquals(titles.getLength(), 1);
        assertEquals(spans.getLength(), 0);
        assertEquals(bolds.getLength(), 1);
        assertEquals(titles.item(0).getTextContent(), title);
        assertEquals(bolds.item(0).getTextContent(), time);
    }

    @Test
    public void testChargedSpell() {

        ElementImpl e = (ElementImpl) pages.get("div");
        int charges = 2;
        e.cast(new AttributeTransformation(new Attribute("foo", "bar")), charges);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList divs = doc.getElementsByTagName("div");

        assertEquals(divs.getLength(), 3);
        assertNotNull(divs.item(0).getAttributes().getNamedItem("foo"));
        assertEquals("bar", divs.item(0).getAttributes().getNamedItem("foo").getTextContent());
        assertEquals("bar", divs.item(1).getAttributes().getNamedItem("foo").getTextContent());
        assertNull(divs.item(2).getAttributes().getNamedItem("foo"));
    }

    @Test
    public void testSpellOfSpells() {

        String style = "color: red;";
        Scroll scroll = new Scroll();
        scroll.changeAttributes("#time", new Attribute("style", style));
        scroll.insert("#time", true, new InsertionIntent() {
            public void insert(Spell parent) {
                parent.writeString(" !");
            }
        });
        Spell sos = new SpellOfSpells(scroll);

        ((ElementImpl) pages.get("div")).cast(sos, 1);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList spans = doc.getElementsByTagName("span");
        assertEquals(spans.getLength(), 1, "There should be exactly one span element.");
        Node span = spans.item(0);
        assertNotNull(span, "There should be a span with the id 'time'.");
        assertEquals(span.getAttributes().getNamedItem("style").getTextContent(), style,
                "It should have the following style: " + style);
    }

    @Test
    public void testVariantDuplication() {

        Spell modification = new Changeling(
                new AttributeTransformation(new Attribute("class", "1")),
                new AttributeTransformation(new Attribute("class", "2")),
                new AttributeTransformation(new Attribute("class", "3"))
        );
        pages.get("title").cast(new Duplication(3, modification));

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList titles = doc.getElementsByTagName("title");

        assertEquals(titles.getLength(), 3, "There should be as many as three titles now.");
        for (int i = 0; i < 3; ++i) {
            assertEquals(titles.item(i).getAttributes().getNamedItem("class").getTextContent(),
                    String.valueOf(i + 1), (i + 1) + ". class should be " + (i + 1));
        }
    }

    @Test
    public void testForEach() {

        List<String> titles = Arrays.asList("It's", "something", "only", "you", "can", "take.");
        pages.get("h1").foreachIn(titles).apply(new Plan<String>() {
            public void execute(Element e, final String item) {
                e.replace(true, item);
                if (odd()) { // alternating background colors
                    e.setAttribute("style", "background-color: red;");
                } else {
                    e.setAttribute("style", "background-color: blue;");
                }
            }
        });
        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList headings = doc.getElementsByTagName("h1");

        assertEquals(headings.getLength(), titles.size());
        for (int i = 0; i < titles.size(); ++i) {
            assertEquals(headings.item(i).getTextContent(), titles.get(i));
            if (i % 2 == 1) {
                assertTrue(headings.item(i).getAttributes().getNamedItem("style").getTextContent().contains("red"),
                        "red background");
            } else {
                assertTrue(headings.item(i).getAttributes().getNamedItem("style").getTextContent().contains("blue"),
                        "blue background");
            }
        }
    }

    @Test
    public void testComplexSpell() {

        String style = "color: red;";
        Spell attr = new AttributeTransformation(new Attribute("style", style));
        Spell insrt = new Insertion(false, new InsertionIntent() {
            public void insert(Spell parent) {
                parent.writeString("HALLO");
            }
        });
        Spell cmplx = new ComplexSpell(attr, insrt, new Duplication(2));
        pages.get("h1").cast(cmplx);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList headings = doc.getElementsByTagName("h1");

        assertEquals(headings.getLength(), 2);
        assertTrue(headings.item(0).isEqualNode(headings.item(1)), "Nodes duplicated.");
        assertTrue(headings.item(0).getTextContent().startsWith("HALLO"), "Starts with 'HALLO'");
        assertNotNull(headings.item(0).getAttributes().getNamedItem("style"), "Style not null");
        assertEquals(headings.item(0).getAttributes().getNamedItem("style").getTextContent(), style);
    }

    @Test
    public void testCombinedSpells() {

        String time = "22:43";
        pages.get("#time").replace(true, time);
        pages.get(".info").insertLast(" Wandledi is great!");
        pages.get("#time").setAttribute("style", "color: red;");
        pages.get("div").setAttribute("style", "font-weight: bold;");

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList divs = doc.getElementsByTagName("div");

        assertEquals(divs.getLength(), 3);
        for (int i = 0; i < divs.getLength(); ++i) {
            Node node = divs.item(i);
            assertEquals(node.getAttributes().getNamedItem("style").getTextContent(),
                    "font-weight: bold;");
            if (i == 1) {
                String txt = node.getTextContent();
                // txt should look like: "Info: Repeat: Wandledi is great! Wandledi is great!"
                assertTrue(txt.startsWith("Info:") && txt.contains("Repeat:") &&
                            txt.lastIndexOf("Wandledi") > txt.indexOf("Wandledi"));
            }
        }
        NodeList spans = doc.getElementsByTagName("span");
        assertEquals(spans.getLength(), 1);
        assertEquals(spans.item(0).getAttributes().getNamedItem("style").getTextContent(), "color: red;");
        assertEquals(spans.item(0).getTextContent(), time);
    }

    public String wandle(String file) {

        StringWriter output = new StringWriter();
        FileReader input = null;
        try {
            input = new FileReader(DIR + file);
            wandler.useScroll(pages.getScroll());
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
}
