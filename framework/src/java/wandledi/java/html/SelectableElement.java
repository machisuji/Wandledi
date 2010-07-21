package wandledi.java.html;

import java.util.Collection;
import wandledi.core.Attribute;
import wandledi.core.CssSelector;
import wandledi.core.Scroll;
import wandledi.core.Selector;
import wandledi.core.Spell;
import wandledi.spells.InsertionIntent;
import wandledi.spells.ReplacementIntent;
import wandledi.spells.StringTransformation;

/**
 *
 * @author Markus Kahl
 */
public class SelectableElement implements Selectable, Element {

    private SelectableImpl selectable;
    private ElementImpl element;

    public SelectableElement(Selector selector, Scroll scroll) {

        selectable = new SelectableImpl(scroll);
        element = new ElementImpl(selector, scroll);
    }

    public Element get(String selector) {
        return selectable.get(selector);
    }

    public Element get(String label, String attrName, String attrValue) {
        return selectable.get(label, attrName, attrValue);
    }

    public Element get(String attrName, String attrValue) {
        return selectable.get(attrName, attrValue);
    }

    public Element get(String label, Attribute... attributes) {
        return selectable.get(label, attributes);
    }

    public Element get(Attribute... attributes) {
        return selectable.get(attributes);
    }

    public Element get(Selector selector) {
        return selectable.get(selector);
    }

    public Selectable at(Selector selector) {
        return selectable.at(selector);
    }

    public Selectable at(String selector) {
        return at(CssSelector.valueOf(selector));
    }

    public ChargedElement max(int charges) {
        return element.max(charges);
    }

    public LateElement at(int offset) {
        return element.at(offset);
    }

    public Selector getSelector() {
        return element.getSelector();
    }

    public void cast(Spell spell) {
        element.cast(spell);
    }

    public void setAttribute(String name, String value) {
        element.setAttribute(name, value);
    }

    public void setAttribute(String name, StringTransformation transformation) {
        element.setAttribute(name, transformation);
    }

    public void clone(int times) {
        element.clone(times);
    }

    public void includeFile(String name) {
        element.includeFile(name);
    }

    public void includeFile(String name, Scroll scroll) {
        element.includeFile(name, scroll);
    }

    public void insert(boolean atEnd, InsertionIntent intent) {
        element.insert(atEnd, intent);
    }

    public void insert(String content) {
        element.insert(content);
    }

    public void insertLast(String content) {
        element.insertLast(content);
    }

    public void insert(String content, boolean atEnd) {
        element.insert(content, atEnd);
    }

    public void replace(boolean contentsOnly, ReplacementIntent intent) {
        element.replace(contentsOnly, intent);
    }

    public void replace(boolean contentsOnly, String content) {
        element.replace(contentsOnly, content);
    }

    public <T> ElementForeach<T> foreachIn(Collection<T> collection) {
        return element.foreachIn(collection);
    }

    public void hide() {
        element.hide();
    }

    public void changeAttribute(String name, String value) {
        element.changeAttribute(name, value);
    }
}
