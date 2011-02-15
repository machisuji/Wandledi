package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;
import org.wandledi.Scroll;
import org.wandledi.Spell;
import org.wandledi.SpellLevel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ArchSpell extends AbstractSpell {

    private Scroll scroll = new Scroll();
    private LinkedList<SpellLevel> spellLevels = new LinkedList<SpellLevel>();

    public ArchSpell(Scroll scroll) {

        this.scroll = scroll;
    }

    public void reset() {

        spellLevels.clear();
    }

    private void checkSpell(String label, Attributes attributes) {

        List<Spell> spells = getScroll().readSpellsFor(label, attributes);
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

    public void endElement(String name) {
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

    public Scroll getScroll() {
        return scroll;
    }

    public void setScroll(Scroll scroll) {
        this.scroll = scroll;
    }
}
