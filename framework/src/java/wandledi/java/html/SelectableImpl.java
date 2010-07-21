package wandledi.java.html;

import wandledi.core.*;

public class SelectableImpl implements Selectable {

    protected Scroll scroll;

    public SelectableImpl(Scroll scroll) {

        this.scroll = scroll;
    }

    public Scroll getScroll() {

        return scroll;
    }

    public void setScroll(Scroll scroll) {

        this.scroll = scroll;
    }

    public Element get(String selector) {

        return get(CssSelector.valueOf(selector));
    }

    public Element get(String label, String attrName, String attrValue) {

        return get(label, new Attribute(attrName, attrValue));
    }

    public Element get(String attrName, String attrValue) {

        return get(new UniversalSelector(new Attribute(attrName, attrValue)));
    }

    public Element get(String label, Attribute... attributes) {

        return get(new UniversalSelector(label, attributes));
    }

    public Element get(Attribute... attributes) {

        return get(new UniversalSelector(attributes));
    }

    public Element get(Selector selector) {

        return new ElementImpl(selector, scroll);
    }

    /**Returns a new Selectable to select elements with.
     * The Selection is relative to the element matched by the
     * given selector and works only below it in the element tree.
     *
     * @param selector CSS selector
     * @return A new Selectable
     */
    public Selectable at(String selector) {

        return at(CssSelector.valueOf(selector));
    }

    /**Returns a new Selectable to select elements with.
     * The Selection is relative to the element matched by the
     * given selector and works only below it in the element tree.
     *
     * @param selector a selector
     * @return A new Selectable
     */
    public Selectable at(Selector selector) {

        Scroll nestedScroll = new Scroll();
        LocalSpells localSpell = new LocalSpells(scroll, nestedScroll);
        scroll.addSpell(selector, localSpell);

        return new SelectableImpl(nestedScroll);
    }
}
