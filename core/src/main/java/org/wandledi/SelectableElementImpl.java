package org.wandledi;

import java.util.Collection;
import org.wandledi.spells.InsertionIntent;
import org.wandledi.spells.ReplacementIntent;
import org.wandledi.spells.StringTransformation;
import org.wandledi.spells.TransformedAttribute;
import org.wandledi.wandlet.Response;

/**
 *
 * @author Markus Kahl
 */
public class SelectableElementImpl implements SelectableElement {
    private Selectable selectable;
    private Element element;

    public SelectableElementImpl(Selectable selectable, Element element) {
        this.selectable = selectable;
        this.element = element;
    }

    public SelectableElementImpl(Selector selector, Scroll parentScroll, Scroll localScroll) {
        this(new SelectableImpl(localScroll), new ElementImpl(selector, parentScroll));
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

    public Element get(String label, Attribute attrHead, Attribute... attrTail) {
        return selectable.get(label, attrHead, attrTail);
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

    public Scroll getScroll() {
        return element.getScroll();
    }

    public TextContent getText() {
        return element.getText();
    }

    public void cast(Spell spell) {
        element.cast(spell);
    }

    public void setAttribute(String name, String value) {
        element.setAttribute(name, value);
    }

    public void changeAttribute(String name, StringTransformation transformation) {
        element.changeAttribute(name, transformation);
    }

    public void setAttributes(Attribute... attributes) {
        element.setAttributes(attributes);
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

    public void includeFile(Response response) {
        element.includeFile(response);
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

    public void truncate(int depth) {
        element.truncate(depth);
    }

    public void reduce() {
        element.reduce();
    }

    public void extract(Selector target) {
        element.extract(target);
    }

    public <T> ElementForeach<T> foreachIn(Collection<T> collection) {
        return element.foreachIn(collection);
    }

    public <T> ElementForeach<T> foreachIn(Collection<T> collection, boolean reduceBefore) {
        return element.foreachIn(collection, reduceBefore);
    }

    public void hide() {
        element.hide();
    }

    public void changeAttribute(String name, String value) {
        element.changeAttribute(name, value);
    }

    public void changeAttributes(TransformedAttribute... attributes) {
        element.changeAttributes(attributes);
    }

    public void removeAttribute(String name) {
        element.removeAttribute(name);
    }
}
