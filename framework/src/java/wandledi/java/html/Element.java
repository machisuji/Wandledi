package wandledi.java.html;

import java.util.Collection;
import wandledi.spells.InsertionIntent;
import wandledi.spells.ReplacementIntent;

/**An HTML element.
 *
 * @author Markus Kahl
 */
public interface Element {

    public void setAttribute(String name, String value);
    public void clone(int times);
    public void includeFile(String name);
    public void insert(boolean atEnd, InsertionIntent intent);
    public void insert(String content);
    public void insertLast(String content);
    public void insert(String content, boolean atEnd);
    public void replace(boolean contentsOnly, ReplacementIntent intent);
    public void replace(boolean contentsOnly, String content);
    public <T> ElementForeach<T> foreachIn(Collection<T> collection);
    public Element get(String selector);
    public void hide();
}
