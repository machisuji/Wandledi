package wandledi.java.html;

import java.util.Collection;

import wandledi.core.Selector;
import wandledi.core.Spell;
import wandledi.spells.InsertionIntent;
import wandledi.spells.ReplacementIntent;

/**An HTML element.
 *
 * @author Markus Kahl
 */
public interface Element extends Selectable {

    /**The Selector used to address this Element.
     *
     * @return
     */
    public Selector getSelector();
    public void cast(Spell spell);
    public void castLater(Spell spell, int offset);
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
    public void hide();
}
