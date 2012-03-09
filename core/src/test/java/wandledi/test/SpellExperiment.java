package wandledi.test;

import org.wandledi.*;
import org.wandledi.spells.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class SpellExperiment {

    public static String DIR = "core/src/test/java/wandledi/test/";

    private SelectableImpl pages;
    private Wandler wandler;
    private Random random = new Random();

    public SpellExperiment() {
        this(Wandler.forXHTML());
    }

    public SpellExperiment(Wandler wandler) {
        this.wandler = wandler;
        wandler.setResources(new Resources() {
            public Reader open(String resource) throws IOException {
                return new FileReader(DIR + resource);
            }
        });
    }

    @BeforeMethod
    public void setUp() {
        pages = new SelectableImpl(new Scroll());
    }

    @Test
    public void testBasicCssSelectors() {
        Element label = pages.get("div");
        Element id = pages.get("#code");
        Element klass = pages.get(".time");
        Element labelClass = pages.get("p.content");

        label.setAttribute("selector", "label");
        id.setAttribute("selector", "id");
        klass.setAttribute("selector", "class");
        labelClass.setAttribute("selector", "label+class");

        String result = wandle("selectors.xhtml");
        Document doc = parseXML(result);

        // check label-selected
        NodeList divs = doc.getElementsByTagName("div");
        assertEquals(divs.getLength(), 3);
        for (int i = 0; i < divs.getLength(); ++i) {
            String attr = divs.item(i).getAttributes().getNamedItem("selector").getTextContent();
            assertEquals(attr, "label");
        }

        // check id-selected
        NodeList pres = doc.getElementsByTagName("pre");
        int codes = 0;
        for (int i = 0; i < pres.getLength(); ++i) {
            Node attr = pres.item(i).getAttributes().getNamedItem("selector");
            if (attr != null) {
                assertEquals(attr.getTextContent(), "id");
                ++codes;
            }
        }
        assertEquals(codes, 1);

        // check class-selected
        NodeList spans = doc.getElementsByTagName("span");
        int times = 0;
        for (int i = 0; i < spans.getLength(); ++i) {
            Node attr = spans.item(i).getAttributes().getNamedItem("selector");
            if (attr != null) {
                assertEquals(attr.getTextContent(), "class");
                ++times;
            }
        }
        assertEquals(times, 2);

        // check label+class-selected
        NodeList ps = doc.getElementsByTagName("p");
        int occurences = 0;
        for (int i = 0; i < ps.getLength(); ++i) {
            Node attr = ps.item(i).getAttributes().getNamedItem("selector");
            if (attr != null) {
                assertEquals(attr.getTextContent(), "label+class");
                ++occurences;
            }
        }
        assertEquals(occurences, 1);
    }

    @Test
    public void testAdvancedCssSelectors() {
        Element e = pages.get("div.left span.time");
        e.replace(true, "buyakasha");

        String result = wandle("selectors.xhtml");
        Document doc = parseXML(result);
        NodeList spans = doc.getElementsByTagName("span");

        int occurences = 0;
        for (int i = 0; i < spans.getLength(); ++i) {
            Node klass = spans.item(i).getAttributes().getNamedItem("class");
            if (klass != null) {
                if ("time".equals(klass.getTextContent()) && "buyakasha".equals(spans.item(i).getTextContent())) {
                    ++occurences;
                }
            }
        }
        assertEquals(occurences, 1, "number of occurences");
    }

    @Test
    public void testAncestorSelectors() {
        String text = "dust in the wind";
        Element e = pages.get("div > span");
        e.replace(true, text);

        String result = wandle("selectors.xhtml");
        Document doc = parseXML(result);

        NodeList spans = doc.getElementsByTagName("span");
        assertEquals(countNodesWithContent(spans, text), 1, "number of occurences");

        text = "above the winter moonlight";
        e = pages.get("div span");
        e.replace(true, text);

        result = wandle("selectors.xhtml");
        doc = parseXML(result);

        spans = doc.getElementsByTagName("span");
        assertEquals(countNodesWithContent(spans, text), 3, "number of occurences");
    }

    protected int countNodesWithContent(NodeList nodes, String content) {
        int occurences = 0;
        for (int i = 0; i < nodes.getLength(); ++i) {
            if (content.equals(nodes.item(i).getTextContent())) {
                ++occurences;
            }
        }
        return occurences;
    }

    @Test
    public void testAttributeTransformation() {

        String style = "background-color: black;";
        pages.get("body").setAttribute("style", style);
        pages.get("#time").changeAttribute("id", new StringTransformation() { // transform attr
            public String transform(String input) {
                StringBuilder sb = new StringBuilder(input);
                return sb.reverse().toString();
            }
        });
        pages.get("#time").changeAttribute("foobar", new StringTransformation() { // transform non-existing attr
            public String transform(String input) {
                return "NAAAH " + input;
            }
        });
        pages.get("#time").changeAttribute("baz", "iyuup $val"); // transform non-existing attr
        pages.get(".info").changeAttribute("class", "$val text"); // transform existing one

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList bodies = doc.getElementsByTagName("body");

        assertEquals(bodies.getLength(), 1);
        assertEquals(bodies.item(0).getAttributes().getNamedItem("style").getTextContent(), style);
        assertEquals(
            doc.getElementsByTagName("span").item(0).getAttributes().getNamedItem("id").getTextContent(),
            "emit", "transformed attribute");
        assertNull(
            doc.getElementsByTagName("span").item(0).getAttributes().getNamedItem("foobar"),
            "nothing");
        assertNull(
            doc.getElementsByTagName("span").item(0).getAttributes().getNamedItem("baz"),
            "nothing");
        assertEquals(
            doc.getElementsByTagName("div").item(1).getAttributes().getNamedItem("class").getTextContent(),
            "info i18n text", "transformed attribute");
    }

    @Test
    public void testDuplication() {

        int number = 2 + random.nextInt(10);
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
        pages.get("title").insert(start2);
        pages.get("title").insert(start); // gets inserted before start2, since insert always inserts at the start
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
                parent.writeString("Just follow ", true);
                parent.startElement("span", new SimpleAttributes(new Attribute("style", style)));
                parent.writeString("the", true);
                parent.endElement("span");
                parent.writeString(" day!", true);
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
        assertEquals(p.getChildNodes().item(1).getAttributes().getNamedItem("style").
                getTextContent(), style);
    }

    @Test(enabled=true)
    public void testInclusion() {

        pages.get(".info").cast(new Inclusion("inclusion.xhtml"));
        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        assertEquals(doc.getElementsByTagName("div").getLength(), 1);
        assertEquals(doc.getElementsByTagName("p").getLength(), 1);
        assertTrue(doc.getElementsByTagName("p").item(0).getTextContent().contains("inclusion"),
                "Text from inclusion.xhtml included.");
    }

    @Test(enabled=true)
    public void testVariantInclusion() {

        String style = "color: red;";
        Scroll scroll = new Scroll();
        scroll.addSpell("p", new AttributeTransformation(new Attribute("style", style)));
        pages.get(".info").cast(new Inclusion("inclusion.xhtml", scroll));
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
                parent.writeString(time, true);
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

        Element e = pages.get("div");
        int charges = 2;
        e.max(charges).cast(new AttributeTransformation(new Attribute("foo", "bar")));

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
    public void testLateSpellPerGet() {
        Element e = pages.get("div");
        int offset = 1;
        e.at(offset).setAttribute("foo", "bar");

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList divs = doc.getElementsByTagName("div");

        assertEquals(divs.getLength(), 3);
        assertNull(divs.item(0).getAttributes().getNamedItem("foo"));
        assertEquals("bar", divs.item(1).getAttributes().getNamedItem("foo").getTextContent());
        assertNull(divs.item(2).getAttributes().getNamedItem("foo"));
    }

    @Test
    public void testLateSpellPerAt() {
        Element e = pages.at("div");
        int offset = 2;
        e.at(offset).setAttribute("foo", "bar");

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList divs = doc.getElementsByTagName("div");

        assertEquals(divs.getLength(), 3);
        assertNull(divs.item(0).getAttributes().getNamedItem("foo"));
        assertNull(divs.item(1).getAttributes().getNamedItem("foo"));
        assertEquals("bar", divs.item(2).getAttributes().getNamedItem("foo").getTextContent());
    }

    @Test
    public void testSpellOfSpells() {

        String style = "color: red;";
        Scroll scroll = new Scroll();
        scroll.addSpell("#time", new AttributeTransformation(new Attribute("style", style)));
        scroll.addSpell("#time", new Insertion(true, new InsertionIntent() {
            public void insert(Spell parent) {
                parent.writeString(" !", true);
            }
        }));
        Spell sos = new ArchSpell(scroll);

        pages.get("div").max(1).cast(sos);

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
            public void execute(SelectableElement e, final String item) {
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
                assertTrue(headings.item(i).getAttributes().getNamedItem("style").
                        getTextContent().contains("red"),
                        "red background");
            } else {
                assertTrue(headings.item(i).getAttributes().getNamedItem("style").
                        getTextContent().contains("blue"),
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
                parent.writeString("HALLO", true);
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

    /**First real bug found during implementation of AnyGood.
     * Some problem with the replacement not propagating all events
     * properly, so that the attribute would not be set if an replacement
     * is applied afterwards.
     *
     * It worked, however, in case the replacement was applied first,
     * because the AttributeTranformations propagates events correctly.
     *
     * However, this should work indepently from the order of the spells.
     */
    @Test
    public void testReplacementCombination() {

        String value = String.valueOf(random.nextInt(1000));
        String replacement = String.valueOf(random.nextInt(1000));

        pages.get("#time").setAttribute("value", value);
        pages.get("#time").replace(true, replacement);

        pages.get("title").replace(true, replacement);
        pages.get("title").setAttribute("value", value);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList titles = doc.getElementsByTagName("title");
        NodeList spans = doc.getElementsByTagName("span");

        assertEquals(titles.getLength(), 1);
        assertEquals(spans.getLength(), 1);
        for (Node node: Arrays.asList(titles.item(0), spans.item(0))) {
            assertEquals(node.getTextContent(), replacement, "no replacement");
            assertNotNull(node.getAttributes().getNamedItem("value"), "attribute");
            assertEquals(node.getAttributes().getNamedItem("value").getTextContent(), value, "value");
        }
    }

    @Test
    public void testCombinedSpells() {

        String time = "22:43";
        pages.get("#time").replace(true, time);
        pages.get(".info").insertLast("Wandledi is great!");
        pages.get("div").at(2).insertLast("Wandledi is great!");
        pages.get("div").setAttribute("style", "font-weight: bold;");
        pages.get("#time").setAttribute("style", "color: red;");

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
        assertEquals(spans.item(0).getAttributes().getNamedItem("style").getTextContent(),
                "color: red;");
        assertEquals(spans.item(0).getTextContent(), time);
    }

    @Test
    public void testSelectionAfterAttributes() {

        pages.get("meta", "http-equiv", "Content-Type").setAttribute("content", "text/xml");

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList metas = doc.getElementsByTagName("meta");

        assertEquals(metas.getLength(), 1, "There should be exactly one meta element.");
        assertEquals(metas.item(0).getAttributes().getNamedItem("content").getTextContent(), "text/xml",
                "Its content should be 'text/xml'");
    }

    @Test
    public void testLocalTransformations() {

        Selectable info = pages.at(".info");
        info.get("div").setAttribute("attr", "value");

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList divs = doc.getElementsByTagName("div");

        assertEquals(divs.getLength(), 3, "number of divs");
        for (int i = 0; i < 2; ++i) {
            Node div = divs.item(i);
            assertNull(div.getAttributes().getNamedItem("attr"), "no added attribute");
        }
        Node div = divs.item(2);
        assertNotNull(div.getAttributes().getNamedItem("attr"), "added attribute");
    }

    @Test
    public void testTruncate() {
        Element div = pages.get("div");
        div.truncate(1);

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList divs = doc.getElementsByTagName("div");

        assertEquals(divs.getLength(), 0, "number of remaining divs");
        assertTrue(result.contains("Info: Repeat:"), "nested divs truncated");
    }

    @Test
    public void testTruncatedForeach() {
        List<String> words = Arrays.asList("Friede", "Freude", "Eierkuchen");
        Element li = pages.get("li.flatten");
        li.foreachIn(words).apply(new Plan<String>() {
            public void execute(SelectableElement e, String word) {
                Element li = e.get("ul li");
                li.at(0).replace(true, word);
                li.at(1).replace(true, word.toUpperCase());
            }
        });
        li.truncate(2);

        String result = wandle("foreach.xhtml");
        Document doc = parseXML(result);
        NodeList lis = doc.getElementsByTagName("li");

        assertEquals(lis.getLength(), 6, "number of found list items");
        for (int i = 0; i < words.size(); ++i) {
            String word = words.get(i);
            assertEquals(lis.item(i * 2).getTextContent(), word);
            assertEquals(lis.item(i * 2 + 1).getTextContent(), word.toUpperCase());
        }
    }

    @Test
    public void testRemoveAttributes() {
        Element timeSpan = pages.get("#time");
        timeSpan.removeAttribute("id"); // id should be removed
        timeSpan.removeAttribute("foobar"); // should have no effect

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList spans = doc.getElementsByTagName("span");

        assertEquals(spans.getLength(), 1);
        assertNull(spans.item(0).getAttributes().getNamedItem("id"), "id");
        assertNotNull(spans.item(0).getAttributes().getNamedItem("title"), "title");
    }

    @Test
    public void testSetAttributes() {
        Element time = pages.get("#time");
        Element title = pages.get("title");
        time.changeAttributes(new TransformedAttribute("title", new StringTransformation() {
            public String transform(String value) {
              return value.toUpperCase();
            }
          }));
        title.setAttributes(new Attribute("blah", "blah"), new Attribute("yada", "yada"));

        String result = wandle("test.xhtml");
        Document doc = parseXML(result);
        NodeList titles = doc.getElementsByTagName("title");
        NodeList spans = doc.getElementsByTagName("span");

        assertNotNull(titles.item(0).getAttributes().getNamedItem("blah"));
        assertNotNull(titles.item(0).getAttributes().getNamedItem("yada"));
        assertEquals(spans.item(0).getAttributes().getNamedItem("title").getTextContent(), "WHAT TIME IS IT?");
    }

    @Test
    /** Test that the Extraction works and in conjuction with other spells too.
     *
     */
    public void testExtraction() {
        Selector sel = CssSelector.valueOf("div.left");
        SelectableElement left = pages.at(sel);

        pages.get(new PathSelector()).extract(sel);
        left.get(".time").getText().replaceAll("baz", "TEST");
        left.get("p").foreachIn(Arrays.asList(1, 2, 3)).apply(new Plan<Integer>() {
            public void execute(SelectableElement e, Integer i) {
                e.insert(i + ": ");
            }
        });

        String result = wandle("selectors.xhtml");
        Document doc = parseXML(result);
        NodeList children = doc.getChildNodes();
        NodeList spans = doc.getElementsByTagName("span");

        assertEquals(children.getLength(), 1, "only one div");
        assertEquals(spans.getLength(), 3, "paragraphs including spans tripled");
        for (int i = 0; i < 2; ++i) {
            assertEquals(spans.item(i).getTextContent(), "TEST");
        }
    }

    @Test
    public void testEncoding() {
        Element doc = pages.get(new UniversalSelector());
        doc.getText().replaceAll("Ü", "(-.-)#");

        String result = wandle("umlaut.xhtml");
        assertTrue(result.contains("(-.-)#"));
        assertTrue(result.contains("ä"));
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
