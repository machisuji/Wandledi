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

    public Element select(String label, String attrName, String attrValue) {

        return select(label, new Attribute(attrName, attrValue));
    }

    public Element select(String label, Attribute... attributes) {

        return get(new UniversalSelector(label, attributes));
    }

    public Element select(Attribute... attributes) {

        return get(new UniversalSelector(attributes));
    }

    public Element get(Selector selector) {

        return new ElementImpl(selector, scroll);
    }
}
