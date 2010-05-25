package wandledi.java.html;

import java.util.Collection;
import org.xml.sax.Attributes;
import wandledi.core.Attribute;
import wandledi.core.GrimoireSection;
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
    private GrimoireSection gs;

    public ElementImpl(String selector, GrimoireSection gs) {

        this.selector = selector;
        this.gs = gs;
    }

    public void setAttribute(String name, String value) {

        gs.changeAttributes(selector, new Attribute(name, value));
    }

    public void clone(int times) {

        gs.duplicate(selector, times);
    }

    public void includeFile(String name) {

        gs.include(selector, name);
    }

    public void insert(boolean atEnd, InsertionIntent intent) {

        gs.insert(selector, atEnd, intent);
    }

    public void insert(String content) {

        insert(content, false);
    }

    public void insertLast(String content) {

        insert(content, true);
    }

    public void insert(final String content, boolean atEnd) {

        gs.insert(selector, atEnd, new InsertionIntent() {
            public void insert(Spell parent) {
                parent.writeString(content);
            }
        });
    }

    public void replace(boolean contentsOnly, ReplacementIntent intent) {

        gs.replace(selector, contentsOnly, intent);
    }

    public void replace(boolean contentsOnly, final String content) {

        gs.replace(selector, contentsOnly, new ReplacementIntent() {
            public void replace(String label, Attributes attributes, Spell parent) {
                parent.writeString(content);
            }
        });
    }

    public <T> ElementForeach<T> foreachIn(Collection<T> collection) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Element get(String selector) {

        return new ElementImpl(selector, gs);
    }

    public void hide() {

        gs.addSpell(selector, new Invisibility());
    }

}
