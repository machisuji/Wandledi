package wandledi.java.html;

import wandledi.core.Attribute;
import wandledi.core.Selector;

public interface Selectable {

    Element get(String selector);

    Element get(String label, String attrName, String attrValue);

    Element get(String attrName, String attrValue);

    Element get(String label, Attribute... attributes);

    Element get(Attribute... attributes);

    /**Retrieves an Element object for the elements matching the given selector.
     *
     * @param selector
     * @return
     */
    Element get(Selector selector);

    Selectable at(Selector selector);

    Selectable at(String selector);
}
