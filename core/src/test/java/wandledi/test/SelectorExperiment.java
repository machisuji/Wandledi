package wandledi.test;

import org.wandledi.*;
import org.testng.annotations.Test;
import org.xml.sax.helpers.AttributesImpl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.*;

public class SelectorExperiment {

    @Test
    public void testBasicSelectors() {
        CssSelector label = CssSelector.valueOf("div");
        CssSelector klass = CssSelector.valueOf(".info");
        CssSelector labelAndClass = CssSelector.valueOf("div.info");
        CssSelector id = CssSelector.valueOf("#foobar");

        assertTrue(label.matches(new CssSelector("div", new Attribute("foo", "bar"))),
                "A bare label should be matched.");
        assertTrue(klass.matches(new CssSelector("div", new Attribute("class", "info"))),
                "A label combined with a class should be matched by just a class.");
        assertTrue(labelAndClass.matches(new CssSelector("div", new Attribute("class", "info"))),
                "A label combined with a class should be matched by a class-and-label selector.");
        assertTrue(id.matches(new CssSelector("div", new Attribute("id", "foobar"))),
                "Any element with an id should be matched by a corresponding id selector.");

        CssSelector alike1 = CssSelector.valueOf("span.text.i18n");
        CssSelector alike2 = CssSelector.valueOf("span.i18n.text");
        CssSelector text = CssSelector.valueOf(".text");

        assertEquals(alike1, alike2, "selectors should be equal");
        assertTrue(text.matches(alike1), ".text should match span.text.i18n");
        assertFalse(text.equals(alike1), ".text should not equal span.text.i18n though");

        CssSelector hello = CssSelector.valueOf("div.content[title=hello, data-active=\"true\"]");
        CssSelector bye = CssSelector.valueOf("div.content[title=bye]");
        CssSelector hello2 = CssSelector.valueOf("div.content[title = 'hello', data-active=true]");

        assertFalse(hello.equals(bye), "selector");
        assertTrue(hello.equals(hello2), "selector");
    }

    @Test
    public void testNestedCssSelectors() {
        CssSelector selector = CssSelector.valueOf(".content lu span");
        List<ElementStart> validPath = Arrays.asList(
                new ElementStart("html"),
                new ElementStart("div", new Attribute("class", "content")),
                new ElementStart("p"),
                new ElementStart("lu"),
                new ElementStart("li"),
                new ElementStart("span", new Attribute("id", "foobar"))
        );
        List<ElementStart> invalidPath = Arrays.asList(
                new ElementStart("html"),
                new ElementStart("div"),
                new ElementStart("p"),
                new ElementStart("lu"),
                new ElementStart("li"),
                new ElementStart("span", new Attribute("id", "foobar"))
        );
        assertTrue(selector.matches("span", new SimpleAttributes(new Attribute("id", "foobar")), validPath));
        assertFalse(selector.matches("span", new SimpleAttributes(new Attribute("id", "foobar")), invalidPath));
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

        assertTrue(selector.matches("span", attributes, new LinkedList<ElementStart>()),
                "The empty UniversalSelector should match anything.");
        assertTrue(selector.matches("div", new AttributesImpl(), new LinkedList<ElementStart>()),
                "The empty UniversalSelector should match anything.");
    }
}
