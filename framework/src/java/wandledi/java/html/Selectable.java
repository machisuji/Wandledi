package wandledi.java.html;

import wandledi.core.Attribute;
import wandledi.core.Selector;

/**
 * Created by IntelliJ IDEA.
 * User: markus
 * Date: 06.06.2010
 * Time: 15:00:56
 * To change this template use File | Settings | File Templates.
 */
public interface Selectable {
    Element get(String selector);

    Element select(String label, String attrName, String attrValue);

    Element select(String label, Attribute... attributes);

    Element select(Attribute... attributes);

    Element get(Selector selector);
}
