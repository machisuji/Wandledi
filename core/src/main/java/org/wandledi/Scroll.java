package org.wandledi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

/**A Scroll contains a number of spells which can be applied using the scroll.
 *
 * @author Markus Kahl
 */
public class Scroll implements Selectable {

    private String name;
    private String view;
    private List<Passage> passages = new ArrayList<Passage>();
    private SelectableImpl selectable = new SelectableImpl(this);
    private List<Scroll> scrolls = new ArrayList<Scroll>();

    public Scroll() {

    }

    public Scroll(String name) {

        this.name = name;
    }

    public void addScroll(Scroll scroll) {

        scrolls.add(scroll);
    }

    public void removeScroll(Scroll scroll) {

        scrolls.remove(scroll);
    }

    public void addSpell(String selector, Spell spell) {

        addSpell(selector, spell, -1);
    }

    public void addSpell(String selector, Spell spell, int charges) {

        addSpell(CssSelector.valueOf(selector), spell, charges);
    }

    public void addSpell(Selector selector, Spell spell) {

        addSpell(selector, spell, -1);
    }

    public void addSpell(Selector selector, Spell spell, int charges) {

        Passage passage = getPassage(selector);
        if (charges > 0) {
            passage.addTransientSpell(spell, charges);
        } else {
            passage.addSpell(spell);
        }
    }

    public void addLateSpell(String selector, Spell spell, int offset) {

        addLateSpell(CssSelector.valueOf(selector), spell, offset);
    }

    public void addLateSpell(Selector selector, Spell spell, int offset) {

        Passage passage = getPassage(selector);
        passage.addSpell(spell, offset);
    }

    /**Gets this Scroll's passage for the given selector.
     *
     * @param selector
     * @return An existing Passage or a new one if there wasn't already a Passage for that selector.
     */
    protected Passage getPassage(Selector selector) {

        for (Passage passage: passages) {
            if (selector.equals(passage)) {
                return passage;
            }
        }
        Passage passage = new Passage(selector);
        passages.add(passage);

        return passage;
    }

    /**
     * Finds the spells for the element with the given label
     * and the class and/or id read from the attributes.
     *
     * Found spells lose charges upon a call of this method!
     *
     *
     * @param label Element label
     * @param attributes Element's attributes
     * @param elementPath Element's path
     * @param reverse Yield the found spells in reverse order
     *
     * @return The list of found spells, which is empty, if none were found.
     */
    public List<Spell> readSpellsFor(String label, Attributes attributes, List<ElementStart> elementPath, boolean reverse) {

        List<Spell> spells = new LinkedList<Spell>();
        for (Passage passage: passages) {
            if (passage.matches(label, attributes, elementPath)) {
                passage.transferSpellsInto(spells);
            }
        }
        for (Scroll scroll: scrolls) {
            spells.addAll(scroll.readSpellsFor(label, attributes, elementPath, false));
        }
        if (reverse) {
            Collections.reverse(spells);
        }
        return spells;
    }

    /**
     * Finds the spells for the element with the given label
     * and the class and/or id read from the attributes.
     *
     * Found spells lose charges upon a call of this method!
     *
     *
     * @param label Element label
     * @param attributes Element's attributes
     * @param elementPath Element's path
     *
     * @return The list of found spells, which is empty, if none were found.
     */
    public List<Spell> readSpellsFor(String label, Attributes attributes, List<ElementStart> elementPath) {
        return readSpellsFor(label, attributes, elementPath, true);
    }

    /**
     * Checks whether all contained Spells have been used
     * and outputs warning for unused Spells.
     *
     * @param reset Reset the Spells' status back to unused.
     */
    public void checkUsed(boolean reset) {
        for (Passage passage: passages) {
            if (!passage.isUsed()) {
                Logger.getLogger(getClass().getName()).warning("Unused Spells under " + passage.toString());
                if (reset) {
                    passage.setUsed(false);
                }
            }
        }
        for (Scroll scroll: scrolls) {
            scroll.checkUsed(reset);
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the view
     */
    public String getView() {
        return view;
    }

    /**
     * @param view the view to set
     */
    public void setView(String view) {
        this.view = view;
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
        return selectable.at(selector);
    }

    public Scroll getScroll() {
        return this;
    }

    class ScrollEntry {

        Selector selector;
        Scroll scroll;
        boolean active;

        public ScrollEntry(Selector selector, Scroll scroll) {

            this.selector = selector;
            this.scroll = scroll;
        }
    }
}
