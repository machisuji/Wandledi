package org.wandledi;

import java.util.Collection;

import org.wandledi.spells.InsertionIntent;
import org.wandledi.spells.ReplacementIntent;
import org.wandledi.spells.StringTransformation;
import org.wandledi.spells.TransformedAttribute;
import org.wandledi.wandlet.Response;

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
    public Scroll getScroll();
    public void cast(Spell spell);
    public void setAttribute(String name, String value);

    public void setAttributes(Attribute... attributes);

    /**Changes an existing attribute's value. You can reference the attribute's old
     * value in the new value using the string '$val'.
     *
     * Example: changeAttribute("class", "newClass $val"); // adds a new class to a possibly existing one
     *
     * @param name Attribute name.
     * @param value Attribute's new value
     */
    public void changeAttribute(String name, String value);
    public void changeAttribute(String name, StringTransformation transformation);
    public void changeAttributes(TransformedAttribute... attributes);

    public void removeAttribute(String name);

    public void clone(int times);

    public void includeFile(String name);
    public void includeFile(String name, Scroll scroll);
    public void includeFile(Response response);

    public void insert(boolean atEnd, InsertionIntent intent);
    public void insert(String content);
    public void insertLast(String content);
    public void insert(String content, boolean atEnd);

    public void replace(boolean contentsOnly, ReplacementIntent intent);
    public void replace(boolean contentsOnly, String content);

    public void truncate(int depth);
    public void reduce();
    public void extract(Selector target);

    public <T> ElementForeach<T> foreachIn(Collection<T> collection);
    public <T> ElementForeach<T> foreachIn(Collection<T> collection, boolean reduceBefore);

    public void hide();
    public TextContent getText();
}
