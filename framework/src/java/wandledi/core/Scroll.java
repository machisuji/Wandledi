package wandledi.core;

import java.util.ArrayList;
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
     * @param selector CssSelector matching the target element.
     * @param attributes Attributes to be added (existing ones will be overriden).
     */
    public void changeAttributes(String selector, Attribute... attributes) {

        addSpell(selector, new AttributeTransformation(attributes));
    }

    /**Duplicates the target element as often as indicated by the given number.
     *
     * @param selector CssSelector matching the target element.
     * @param number Number of duplications.
     */
    public void duplicate(String selector, int number) {

        addSpell(selector, new Duplication(number));
    }

    /**Includes the specified file in place of the target element.
     *
     * @param selector CssSelector matching the target element.
     * @param file The file to be included (from the view directory).
     */
    public void include(String selector, String file) {

        addSpell(selector, new Inclusion(file));
    }

    /**Inserts new elements into the target element according to the given intent.
     *
     * @param selector CssSelector matching the target element.
     * @param intent The intent specifying what to insert.
     */
    public void insert(String selector, boolean insertAtEnd, InsertionIntent intent) {

        addSpell(selector, new Insertion(insertAtEnd, intent));
    }

    /**Replaces the target element (or only its contents) according the given intent.
     *
     * @param selector CssSelector matching the target element.
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

        Selector s = CssSelector.valueOf(selector);
        Passage passage = getPassage(s);
        if (charges > 0) {
            passage.addTransientSpell(spell, charges);
        } else {
            passage.addSpell(spell);
        }
    }

    public void addLateSpell(String selector, Spell spell, int offset) {

        Selector s = CssSelector.valueOf(selector);
        Passage passage = getPassage(s);
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
        Collections.sort(passages);
        
        return passage;
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

        List<Spell> spells = new LinkedList<Spell>();
        for (Passage passage: passages) {
            if (passage.matches(label, attributes)) {
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