package wandledi.test;

import org.wandledi.Attribute;
import org.wandledi.CssSelector;
import org.wandledi.Selector;
import org.wandledi.UniversalSelector;
import org.testng.annotations.Test;
import org.xml.sax.helpers.AttributesImpl;

import static org.testng.Assert.*;

public class SelectorExperiment {

    @Test
    public void testBasicSelectors() {
        CssSelector label = CssSelector.valueOf("div");
        CssSelector klass = CssSelector.valueOf(".info");
        CssSelector labelAndClass = CssSelector.valueOf("div.info");
        CssSelector id = CssSelector.valueOf("#foobar");

        assertTrue(label.equals(new CssSelector("div", new Attribute("foo", "bar"))),
                "A bare label should be matched.");
        assertTrue(klass.equals(new CssSelector("div", new Attribute("class", "info"))),
                "A label combined with a class should be matched by just a class.");
        assertTrue(labelAndClass.equals(new CssSelector("div", new Attribute("class", "info"))),
                "A label combined with a class should be matched by a class-and-label selector.");
        assertTrue(id.equals(new CssSelector("div", new Attribute("id", "foobar"))),
                "Any element with an id should be matched by a corresponding id selector.");
    }

    @Test
    public void testUniversalSelectorsHashContract() {
        UniversalSelector u1 = new UniversalSelector("meta",
                new Attribute("foo", "bar"),
                new Attribute("http-equiv", "content-language"),
                new Attribute("content", "de"));
        UniversalSelector u2 = new UniversalSelector("meta",
                new Attribute("content", "de"),
                new Attribute("foo", "bar"),
                new Attribute("http-equiv", "content-language"));
        UniversalSelector u3 = new UniversalSelector("meta",
                new Attribute("http-equiv", "content-language"),
                new Attribute("content", "de"),
                new Attribute("foo", "bar"));
        
        assertFalse(u1.toString().equals(u2.toString()), "The String representations should not match.");
        assertTrue(u1.equals(u2), "They should be equal, though.");
        assertTrue(u2.equals(u1), "And symmetrical at that.");
        assertTrue(u1.equals(u2) && u2.equals(u3) && u1.equals(u3), "Transitivity, ye know?");
        assertTrue(u1.hashCode() == u2.hashCode() && u2.hashCode() == u3.hashCode(),
                "Their hashCodes should be, too.");
    }

    @Test
    public void testUniversalMatches() {
        Selector selector = new UniversalSelector();
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(null, "style", "style", null, "color: black;");

        assertTrue(selector.matches("span", attributes),
                "The empty UniversalSelector should match anything.");
        assertTrue(selector.matches("div", new AttributesImpl()),
                "The empty UniversalSelector should match anything.");
    }
}
