package wandledi.java.html;

import java.util.Collection;

import org.xml.sax.Attributes;
import wandledi.core.Attribute;
import wandledi.core.Scroll;
import wandledi.core.Spell;
import wandledi.spells.*;

/**
 *
 * @author Markus Kahl
 */
public class ElementImpl implements Element {

    protected String selector;
    protected Scroll scroll;
    protected int charges = -1;

    public ElementImpl(String selector, Scroll scroll) {

        this.selector = selector;
        this.scroll = scroll;
    }

    protected ElementImpl(String selector, Scroll scroll, int charges) {

        this(selector, scroll);
        this.charges = charges;
    }

    public void cast(Spell spell) {

        scroll.addSpell(selector, spell, charges);
    }

    public void cast(Spell spell, int charges) {

        scroll.addSpell(selector, spell, charges);
    }

    public void setAttribute(String name, String value) {

        cast(new AttributeTransformation(new Attribute(name, value)));
    }

    public void clone(int times) {

        cast(new Duplication(times));
    }

    public void includeFile(String name) {

        cast(new Inclusion(name));
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

    public void insert(final String content, boolean atEnd) {

        InsertionIntent intent = new InsertionIntent() {
            public void insert(Spell parent) {
                parent.writeString(content);
            }
        };
        cast(new Insertion(atEnd, intent));
    }

    public void replace(boolean contentsOnly, ReplacementIntent intent) {

        cast(new Replacement(intent, contentsOnly));
    }

    public void replace(boolean contentsOnly, final String content) {

        ReplacementIntent intent = new ReplacementIntent() {
            public void replace(String label, Attributes attributes, Spell parent) {
                parent.writeString(content);
            }
        };
        cast(new Replacement(intent, contentsOnly));
    }

    public <T> ElementForeach<T> foreachIn(final Collection<T> collection) {

        ElementForeach<T> foreach = new ElementForeach<T>() {
            public void apply(Plan<T> plan) {
                Element chargedElement = new ElementImpl(selector, scroll, 1);
                for (T item: collection) {
                    plan.execute(chargedElement, item);
                }
            }
        };
        return foreach;
    }

    public Element get(String selector) {

        return new ElementImpl(selector, scroll);
    }

    public void hide() {

        cast(new Invisibility());
    }
}
