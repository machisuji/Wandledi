package org.wandledi;

import java.util.Collection;
import org.wandledi.spells.*;
import org.wandledi.wandlet.Response;
import org.xml.sax.Attributes;


/**
 *
 * @author Markus Kahl
 */
public class ElementImpl implements Element {

    protected Scroll scroll;
    protected Selector selector;

    public ElementImpl(Selector selector, Scroll scroll) {
        this.scroll = scroll;
        this.selector = selector;
    }

    public Selector getSelector() {
        return selector;
    }

    public Scroll getScroll() {
        return scroll;
    }

    public TextContent getText() {
        return new TextContentImpl(this);
    }

    public ChargedElement max(int charges) {
        return new ChargedElement(selector, scroll, charges);
    }

    public LateElement at(int offset) {
        return new LateElement(selector, scroll, offset);
    }

    public void cast(Spell spell) {
        scroll.addSpell(selector, spell);
    }

    public void setAttribute(String name, String value) {
        cast(new AttributeTransformation(new Attribute(name, value)));
    }

    public void setAttributes(Attribute... attributes) {
        cast(new AttributeTransformation(attributes));
    }

    public void changeAttribute(final String name, final String value) {
        cast(new AttributeTransformation(new TransformedAttribute(name, new StringTransformation() {
            public String transform(String input) {
                return value.replace("$val", input);
            }

            public String toString() {
                return String.format(
                    "StringTransformation(change: '%s' -> '%s')",
                    name, value);
            }
        })));
    }

    public void changeAttribute(String name, StringTransformation transformation) {
        cast(new AttributeTransformation(new TransformedAttribute(name, transformation)));
    }

    public void changeAttributes(TransformedAttribute... attributes) {
        cast(new AttributeTransformation(attributes));
    }

    public void removeAttribute(String name) {
        cast(new AttributeTransformation(name));
    }

    public void clone(int times) {
        cast(new Duplication(times));
    }

    public void includeFile(String name) {
        cast(new Inclusion(name));
    }

    public void includeFile(String name, Scroll scroll) {
        cast(new Inclusion(name, scroll));
    }

    public void includeFile(Response response) {
        cast(new Inclusion(response.getFile(), response.getScroll()));
    }

    public void insert(boolean atEnd, InsertionIntent intent) {
        cast(new Insertion(atEnd, intent));
    }

    public void insert(String content) {
        insert(content, false);
    }

    public void insertLast(String content) {
        insert(content, true);
    }

    public void insert(final String content, final boolean atEnd) {
        InsertionIntent intent = new InsertionIntent() {
            public void insert(Spell parent) {
                parent.writeString(content, true);
            }

            public String toString() {
                return String.format("InsertionIntent(content: %s)", content);
            }
        };
        cast(new Insertion(atEnd, intent));
    }

    public void replace(boolean contentsOnly, ReplacementIntent intent) {
        cast(new Replacement(intent, contentsOnly));
    }

    public void replace(final boolean contentsOnly, final String content) {
        ReplacementIntent intent = new ReplacementIntent() {
            public void replace(String label, Attributes attributes, Spell parent) {
                parent.writeString(content, true);
            }

            public String toString() {
                return String.format("ReplacementIntent(content: %s)", content);
            }
        };
        cast(new Replacement(intent, contentsOnly));
    }

    public void truncate(int depth) {
        cast(new Truncate(depth));
    }

    public void reduce() {
        cast(new Reduction());
    }

    public void extract(Selector target) {
        cast(new Extraction(target));
    }

    public <T> ElementForeach<T> foreachIn(Collection<T> collection) {
        return new ElementForeachImpl<T>(this, collection);
    }

    public <T> ElementForeach<T> foreachIn(Collection<T> collection, boolean reduceBefore) {
        return new ElementForeachImpl<T>(this, collection, reduceBefore);
    }

    public void hide() {
        cast(new Invisibility());
    }
}
