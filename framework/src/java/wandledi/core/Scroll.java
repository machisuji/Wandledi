package wandledi.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.xml.sax.Attributes;
import wandledi.spells.*;

/**A Scroll contains a number of spells which can be apply using the scroll.
 *
 * @author Markus Kahl
 */
public class Scroll {

    private String name;
    private String view;
    private List<Passage> passages = new ArrayList<Passage>();

    public Scroll() {

    }

    public Scroll(String name) {

        this.name = name;
    }

    /**Changes the attributes of the target element.
     *
     * @param selector Selector matching the target element.
     * @param attributes Attributes to be added (existing ones will be overriden).
     */
    public void changeAttributes(String selector, Attribute... attributes) {

        addSpell(selector, new AttributeTransformation(attributes));
    }

    /**Duplicates the target element as often as indicated by the given number.
     *
     * @param selector Selector matching the target element.
     * @param number Number of duplications.
     */
    public void duplicate(String selector, int number) {

        addSpell(selector, new Duplication(number));
    }

    /**Includes the specified file in place of the target element.
     *
     * @param selector Selector matching the target element.
     * @param file The file to be included (from the view directory).
     */
    public void include(String selector, String file) {

        addSpell(selector, new Inclusion(file));
    }

    /**Inserts new elements into the target element according to the given intent.
     *
     * @param selector Selector matching the target element.
     * @param intent The intent specifying what to insert.
     */
    public void insert(String selector, boolean insertAtEnd, InsertionIntent intent) {

        addSpell(selector, new Insertion(insertAtEnd, intent));
    }

    /**Replaces the target element (or only its contents) according the given intent.
     *
     * @param selector Selector matching the target element.
     * @param contentsOnly If true, only the target elements content will be replaced.
     * @param intent The intent specifying the replacement.
     */
    public void replace(String selector, boolean contentsOnly, ReplacementIntent intent) {

        addSpell(selector, new Replacement(intent, contentsOnly));
    }

    public void addSpell(String selector, Spell spell) {

        addSpell(selector, spell, -1);
    }

    public void addSpell(String selector, Spell spell, int charges) {

        Selector s = Selector.valueOf(selector);
        boolean newPassage = true;
        for (Passage passage: passages) {
            if (s.equals(passage)) {
               passage.addSpell(spell, charges);
            }
            if (passage.toString().equals(selector)) {
                newPassage = false;
            }
        }
        if (newPassage) {
            Passage passage = new Passage(s);
            passage.addSpell(spell, charges);

            passages.add(passage);
            Collections.sort(passages);
        }
        if (true) return;
        Passage passage = findPassage(selector);
        if (passage == null) {
            passage = new Passage(Selector.valueOf(selector));
            passage.addSpell(spell, charges);

            passages.add(passage);
            Collections.sort(passages);
        } else {
            passage.addSpell(spell, charges);
        }
    }

    /**Finds the spells for the element with the given label
     * and the class and/or id read from the attributes.
     *
     * Found spells lose charges upon a call of this method!
     *
     * @param label
     * @param attributes
     * @return The list of found spells, which is empty, if none were found.
     */
    public List<Spell> readSpellsFor(String label, Attributes attributes) {

        Selector selector = new Selector(label, attributes);
        List<Spell> spells = new LinkedList<Spell>();
        for (Passage passage: passages) {
            if (passage.equals(selector)) {
                passage.transferSpellsInto(spells);
            }
        }
        return spells;
    }

    private Passage findPassage(String selector) {

        for (Passage passage: passages) {
            if (passage.toString().equals(selector)) {
                return passage;
            }
        }
        return null;
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
}

class Passage extends Selector {

    private List<Entry> entries = new LinkedList<Entry>();

    public Passage(Selector selector) {

        super(selector.getLabel(), selector.getElementClass(), selector.getId());
    }

    /**Adds the given spell with the specified amount of charges.
     *
     * @param spell
     * @param charges The spell has unlimited charges if below zero.
     */
    public void addSpell(Spell spell, int charges) {

        entries.add(new Entry(spell, charges));
    }

    public void addSpell(Spell spell) {

        addSpell(spell, -1);
    }

    /**With each read the charges of the containing spells decrease.
     * If the charges of a spell fall to 0 it can't be used anymore.
     *
     * @return
     */
    public List<Spell> readSpells() {

        List spells = new LinkedList();
        for (Entry entry: entries) {
            if (entry.charges != 0) {
                spells.add(entry.spell);
                --entry.charges;
            }
        }
        return spells;
    }

    /**Reads this scroll's spells and transfers them into the given collection.
     *
     * With each read the charges of the containing spells decrease.
     * If the charges of a spell fall to 0 it can't be used anymore.
     *
     * @param spells
     */
    public void transferSpellsInto(Collection<Spell> spells) {

        for (Entry entry: entries) {
            if (entry.charges != 0) {
                spells.add(entry.spell);
                --entry.charges;
            }
        }
    }

    private static class Entry {

        Spell spell;
        int charges; // how often a Spell is apply until its power is used up

        Entry(Spell spell, int charges) {

            this.spell = spell;
            this.charges = charges;
        }
    }
}