package org.wandledi;

public class ChargedElement extends ElementImpl {

    private int charges = 1;

    public ChargedElement(Selector selector, Scroll scroll, int charges) {

        super(selector, scroll);
        this.charges = charges;
    }

    @Override
    public void cast(Spell spell) {

        scroll.addSpell(selector, spell, charges);
    }

    public int getCharges() {
        return charges;
    }

    public void setCharges(int charges) {
        this.charges = charges;
    }
}
