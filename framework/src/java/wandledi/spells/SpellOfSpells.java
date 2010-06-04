package wandledi.spells;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import wandledi.core.AbstractSpell;
import wandledi.core.Scroll;
import wandledi.core.Spell;
import wandledi.core.SpellLevel;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SpellOfSpells extends AbstractSpell {

    private Scroll scroll = new Scroll();
    private LinkedList<SpellLevel> spellLevels = new LinkedList<SpellLevel>();

    public SpellOfSpells(Scroll scroll) {

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

        return new SpellOfSpells(scroll);
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
