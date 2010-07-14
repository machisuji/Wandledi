package wandledi.java.html;

import wandledi.core.Attribute;
import wandledi.core.Selector;

public interface Selectable {
    Element get(String selector);

    Element select(String label, String attrName, String attrValue);

    Element select(String label, Attribute... attributes);

    Element select(Attribute... attributes);

    Element get(Selector selector);
}
