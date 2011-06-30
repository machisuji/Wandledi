package org.wandledi.spells;

import org.wandledi.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

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
    private boolean inheritElementPath = false;

    public ArchSpell(Scroll scroll, boolean inheritElementPath) {
        this.scroll = scroll;
        this.inheritElementPath = inheritElementPath;
    }

    public ArchSpell(Scroll scroll) {
        this(scroll, false);
    }

    public void reset() {

        spellLevels.clear();
    }

    private void checkSpell(String label, Attributes attributes) {

        List<Spell> spells = getScroll().readSpellsFor(label, attributes, getElementPath());
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

    /**Sadly I have no idea what the heck this is good for.
     * But I know that there was a very good reason!
     * Just not what it was ...
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

    public void writeCharacters(char[] characters, int offset, int length) {
        if (spellLevels.size() == 0) {
            parent.writeCharacters(characters, offset, length);
        } else {
            SpellLevel level = spellLevels.getLast();
            level.spell.writeCharacters(characters, offset, length);
        }
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
    @Override
    public List<ElementStart> getElementPath() {
        if (inheritElementPath && parent != null) {
            List<ElementStart> path = new ArrayList<ElementStart>(parent.getElementPath());
            path.addAll(elementPath);
            System.out.println("Combined Element Path: " + path);
            return path;
        } else {
            return elementPathView;
        }
    }

    public Scroll getScroll() {
        return scroll;
    }

    public void setScroll(Scroll scroll) {
        this.scroll = scroll;
    }
}
