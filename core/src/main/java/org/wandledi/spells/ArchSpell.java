package org.wandledi.spells;

import org.wandledi.*;
import org.xml.sax.Attributes;

import java.util.*;

public class ArchSpell extends AbstractSpell {

    private Scroll scroll;
    private LinkedList<SpellLevel> spellLevels = new LinkedList<SpellLevel>();

    /**A list of elements from the root of the document (<html>) down to
     * the leaf (element) that is currently processed.
     *
     * For instance given the following document:
     *
     * <pre>
     * &lt;html&gt;
     *     &lt;head&gt;...&lt;/head&gt;
     *     &lt;body&gt;
     *         &lt;h1&gt;Title&lt;/h1&gt;
     *         &lt;div&gt;...&lt;/div&gt;
     *     &lt;/body&gt;
     * &lt;html&gt;
     * </pre>
     *
     * The elementPath for the 'Title' will be the following list:
     * [html, body, h1]
     */
    private ArrayList<ElementStart> elementPath = new ArrayList<ElementStart>(12);
    private List<ElementStart> elementPathView = Collections.unmodifiableList(elementPath);

    public ArchSpell(Scroll scroll) {
        this.scroll = scroll;
    }

    public ArchSpell() {
        this(new Scroll());
    }

    public void reset() {

        spellLevels.clear();
    }

    private void checkSpell(String label, Attributes attributes) {

        List<Spell> spells = getScroll().readSpellsFor(label, attributes, elementPathView);
        Spell parent = this.parent;
        if (spellLevels.size() > 0) {
            parent = spellLevels.getLast().spell;
        }
        Iterator<Spell> i = spells.iterator();
        while (i.hasNext()) {
            Spell spell = copyIfNested(i.next());
            spell.setParent(parent);
            parent = spell;
            if (!i.hasNext()) {
                spellLevels.add(new SpellLevel(spell));
            }
        }
    }

    /**
     * Makes the Spell after the first spell to be called in the chain ignore transformation bounds.
     * That is if it receives a #startTransformedElement this was actually directed at the first spell,
     * so it must treat it as a mere #startElement.
     *
     * @param ignore
     */
    private void setIgnoreTransformationBounds(boolean ignore) {
        if (spellLevels.size() > 1) {
            SpellLevel wall = spellLevels.get(spellLevels.size() - 2);
            Spell spell = wall.spell;
            spell.ignoreBounds(ignore);
        }
    }

    /**If the very same spell is applied to nested elements we
     * need to clone the spell for any further appliance to prevent
     * an infinite loop.
     *
     * @param spell
     * @return
     */
    private Spell copyIfNested(Spell spell) {
        if (spellLevels.size() > 0 && spellLevels.getLast().spell.hierarchyContains(spell)) {
            return spell.clone();
        }
        return spell;
    }

    public void startTransformedElement(String name, Attributes atts) {
        startElement(name, atts);
    }

    public void endTransformedElement(String name) {
        endElement(name);
    }

    public void startElement(String name, Attributes atts) {
        checkSpell(name, atts);
        setIgnoreTransformationBounds(true);
        elementPath.add(new ElementStart(name, copy(atts)));

        if (Wandler.dlogLevel >= Wandler.DLOG_LEVEL_3) {
            System.out.println("[DEBUG]: startElement(" + name + ", " + atts + "): ");
            System.out.println("[ ARCH]      elementPath: " + elementPath);
            System.out.println("[SPELL]      spell chain: " + getCurrentSpellChain());
            System.out.println("[-----]");
        }

        if (spellLevels.size() == 0) {
            parent.startElement(name, atts);
        } else {
            SpellLevel level = spellLevels.getLast();
            ++level.tagLevel;
            if (level.tagLevel > 1) {
                level.spell.startElement(name, atts);
            } else {
                level.spell.startTransformedElement(name, atts);
            }
        }
    }

    protected Attributes copy(Attributes attr) {
        return new SimpleAttributes(attr);
    }

    public void endElement(String name) {
        if (elementPath.size() > 0) {
            elementPath.remove(elementPath.size() - 1);
        }
        if (spellLevels.size() == 0) {
            parent.endElement(name);
        } else {
            SpellLevel level = spellLevels.getLast();
            --level.tagLevel;
            if (level.tagLevel > 0) {
                level.spell.endElement(name);
            } else {
                level.spell.endTransformedElement(name);
                setIgnoreTransformationBounds(false);
                spellLevels.removeLast();
            }
        }
    }

    public void writeCharacters(char[] characters, int offset, int length, boolean safe) {
        if (spellLevels.size() == 0) {
            parent.writeCharacters(characters, offset, length, safe);
        } else {
            SpellLevel level = spellLevels.getLast();
            level.spell.writeCharacters(characters, offset, length, safe);
        }
    }

    /**
     * Returns the currently active chain of spells.
     * The first item is the first Spell to receive incoming events,
     * which are then passed through to the next spell and so on.
     */
    protected List<Spell> getCurrentSpellChain() {
        List<Spell> spellChain = new LinkedList<Spell>();

        if (!spellLevels.isEmpty()) {
            Spell first = spellLevels.getLast().spell;
            Spell parent = first.getParent();

            spellChain.add(first);

            while (parent != null) {
                spellChain.add(parent);
                parent = parent.getParent();
            }
        }
        return spellChain;
    }

    @Override
    public Spell clone() {
        return new ArchSpell(scroll);
    }

    public LinkedList<SpellLevel> getSpellLevels() {
        return spellLevels;
    }

    /**A list of elements from the root of the document (<html>) down to
     * the leaf (element) that is currently processed.
     *
     * For instance given the following document:
     *
     * <pre>
     * &lt;html&gt;
     *     &lt;head&gt;...&lt;/head&gt;
     *     &lt;body&gt;
     *         &lt;h1&gt;Title&lt;/h1&gt;
     *         &lt;div&gt;...&lt;/div&gt;
     *     &lt;/body&gt;
     * &lt;html&gt;
     * </pre>
     *
     * The elementPath for the 'Title' will be the following list:
     * [html, body, h1]
     *
     * @return An immutable list of path elements.
     */
    public List<ElementStart> getElementPath() {
        return elementPathView;
    }

    public Scroll getScroll() {
        return scroll;
    }

    public void setScroll(Scroll scroll) {
        this.scroll = scroll;
    }
}
