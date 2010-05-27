package wandledi.java.html;

import java.util.Collection;
import org.xml.sax.Attributes;
import wandledi.core.Attribute;
import wandledi.core.Scroll;
import wandledi.core.Spell;
import wandledi.spells.InsertionIntent;
import wandledi.spells.Invisibility;
import wandledi.spells.ReplacementIntent;

/**
 *
 * @author Markus Kahl
 */
public class ElementImpl implements Element {

    private String selector;
    private Scroll scroll;

    public ElementImpl(String selector, Scroll scroll) {

        this.selector = selector;
        this.scroll = scroll;
    }

    public void setAttribute(String name, String value) {

        scroll.changeAttributes(selector, new Attribute(name, value));
    }

    public void clone(int times) {

        scroll.duplicate(selector, times);
    }

    public void includeFile(String name) {

        scroll.include(selector, name);
    }

    public void insert(boolean atEnd, InsertionIntent intent) {

        scroll.insert(selector, atEnd, intent);
    }

    public void insert(String content) {

        insert(content, false);
    }

    public void insertLast(String content) {

        insert(content, true);
    }

    public void insert(final String content, boolean atEnd) {

        scroll.insert(selector, atEnd, new InsertionIntent() {
            public void insert(Spell parent) {
                parent.writeString(content);
            }
        });
    }

    public void replace(boolean contentsOnly, ReplacementIntent intent) {

        scroll.replace(selector, contentsOnly, intent);
    }

    public void replace(boolean contentsOnly, final String content) {

        scroll.replace(selector, contentsOnly, new ReplacementIntent() {
            public void replace(String label, Attributes attributes, Spell parent) {
                parent.writeString(content);
            }
        });
    }

    public <T> ElementForeach<T> foreachIn(Collection<T> collection) {

        ElementForeach<T> foreach = new ElementForeach<T>() {
            public void cast(Spell1<T> spell) {
                // Spell Queue für Spells mit selbem Selector?
                spell.hex(ElementImpl.this, null);
            }
        };
        return foreach;
    }

    public Element get(String selector) {

        return new ElementImpl(selector, scroll);
    }

    public void hide() {

        scroll.addSpell(selector, new Invisibility());
    }

}
