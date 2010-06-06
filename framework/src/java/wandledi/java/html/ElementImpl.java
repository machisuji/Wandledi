package wandledi.java.html;

import java.util.Collection;

import org.xml.sax.Attributes;
import wandledi.core.*;
import wandledi.spells.*;

/**
 *
 * @author Markus Kahl
 */
public class ElementImpl extends SelectableImpl implements Element {

    protected Selector selector;
    protected int charges = -1;

    public ElementImpl(Selector selector, Scroll scroll) {

        super(scroll);
        this.selector = selector;
    }

    protected ElementImpl(Selector selector, Scroll scroll, int charges) {

        this(selector, scroll);
        this.charges = charges;
    }

    public Selector getSelector() {

        return selector;
    }

    public void cast(Spell spell) {

        scroll.addSpell(selector, spell, charges);
    }

    public void cast(Spell spell, int charges) {

        scroll.addSpell(selector, spell, charges);
    }

    public void castLater(Spell spell, int offset) {

        scroll.addLateSpell(selector, spell, offset);
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

    public <T> ElementForeach<T> foreachIn(Collection<T> collection) {

        return new ElementForeachImpl(this, collection);
    }

    public void hide() {

        cast(new Invisibility());
    }
}
