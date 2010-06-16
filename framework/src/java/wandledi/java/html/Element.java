package wandledi.java.html;

import java.util.Collection;

import wandledi.core.Scroll;
import wandledi.core.Selector;
import wandledi.core.Spell;
import wandledi.spells.InsertionIntent;
import wandledi.spells.ReplacementIntent;

/**An HTML element.
 *
 * @author Markus Kahl
 */
public interface Element {

    /**Creates a charged version of this element whose spells have the
     * given number of charges by default.
     *
     * @param charges The number of charges which the spells on this element shall have.
     * @return The ChargedElement
     */
    public ChargedElement max(int charges);

    /**Creates a late version of this element whose spells have
     * the given offset by default.
     *
     * @param offset The offset all spells on this element shall have.
     * @return The LateElement
     */
    public LateElement at(int offset);
    /**The Selector used to address this Element.
     *
     * @return
     */
    public Selector getSelector();
    public void cast(Spell spell);
    public void setAttribute(String name, String value);
    public void clone(int times);
    public void includeFile(String name);
    public void includeFile(String name, Scroll scroll);
    public void insert(boolean atEnd, InsertionIntent intent);
    public void insert(String content);
    public void insertLast(String content);
    public void insert(String content, boolean atEnd);
    public void replace(boolean contentsOnly, ReplacementIntent intent);
    public void replace(boolean contentsOnly, String content);
    public <T> ElementForeach<T> foreachIn(Collection<T> collection);
    public void hide();
}
