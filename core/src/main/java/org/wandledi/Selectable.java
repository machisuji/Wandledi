package org.wandledi;

public interface Selectable {

    Element get(String selector);

    Element get(String label, String attrName, String attrValue);

    Element get(String attrName, String attrValue);

    Element get(String label, Attribute attrHead, Attribute... attrTail);

    Element get(Attribute... attributes);

    /**Retrieves an Element object for the elements matching the given selector.
     *
     * @param selector
     * @return
     */
    Element get(Selector selector);

    Selectable at(Selector selector);

    Selectable at(String selector);

    Scroll getScroll();
}
