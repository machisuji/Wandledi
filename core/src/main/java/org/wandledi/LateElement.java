package org.wandledi;

public class LateElement extends ElementImpl {

    private int offset = 0;

    public LateElement(Selector selector, Scroll scroll, int offset) {

        super(selector, scroll);
        this.offset = offset;
    }

    public void cast(Spell spell) {

        scroll.addLateSpell(selector, spell, offset);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
