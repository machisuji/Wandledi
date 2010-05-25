package wandledi.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xml.sax.Attributes;
import wandledi.spells.*;

/**Holds mappings from selectors to spells which are to be cast.
 *
 * @author Markus Kahl
 */
public class GrimoireSection {

    private String name;
    private String view;
    private List<Transtainer> spells = new ArrayList<Transtainer>();

    public GrimoireSection() {

    }

    public GrimoireSection(String name) {

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

        addSpell(selector, new Insertion(intent, insertAtEnd));
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

        Transtainer entry;
        if (selector.indexOf("#") != -1) { // ids
            String id = selector.substring(selector.indexOf('#') + 1);
            entry = new Transtainer(new Selector(id), spell);
        } else if (selector.indexOf(".") != -1) { // classes
            String klass = selector.substring(selector.indexOf('.') + 1);
            String label = selector.substring(0, selector.indexOf('.'));
            entry = new Transtainer(new Selector(label.length() > 0 ? label : null, klass, null),
                    spell);
        } else { // labels
            String label = selector;
            entry = new Transtainer(new Selector(label, null, null), spell);
        }
        spells.add(entry);
        Collections.sort(spells);
    }

    public Spell findSpellFor(String label, Attributes attributes) {

        int index = spells.indexOf(new Selector(label, attributes));
        if (index != -1) {
            return spells.get(index).getTransformation();
        } else {
            return null;
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
}

class Transtainer extends Selector {

    private Spell transformation;

    public Transtainer(Selector selector, Spell transformation) {

        super(selector.getLabel(), selector.getElementClass(), selector.getId());
        this.transformation = transformation;
    }

    public Spell getTransformation() {

        return transformation;
    }
}