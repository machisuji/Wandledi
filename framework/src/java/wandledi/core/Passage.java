package wandledi.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        transferSpellsInto(spells);
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
        int charges; // how often a Spell is applied until its power is used up

        Entry(Spell spell, int charges) {

            this.spell = spell;
            this.charges = charges;
        }
    }
}