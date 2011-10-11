package org.wandledi;

import org.xml.sax.Attributes;

/**
 *
 * @author Markus Kahl
 */
public class LocalSpells extends AbstractSpell {

    private Scroll parentScroll;
    private Scroll localScroll;

    public LocalSpells(Scroll parentScroll, Scroll localScroll) {
        this.parentScroll = parentScroll;
        this.localScroll = localScroll;
    }

    public void startTransformedElement(String label, Attributes attributes) {
        super.startTransformedElement(label, attributes);
        parentScroll.addScroll(localScroll);
    }

    public void endTransformedElement(String label) {
        parentScroll.removeScroll(localScroll);
        super.endTransformedElement(label);
    }

    @Override
    public Spell clone() {
        return new LocalSpells(parentScroll, localScroll);
    }
}
