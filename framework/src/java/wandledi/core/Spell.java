package wandledi.core;

import org.xml.sax.Attributes;

/**This is how a spell look like.
 *
 * @author Markus Kahl
 */
public interface Spell {

    public void setParent(Spell spell);
    public void startElement(String name, Attributes attributes);
    public void endElement(String name);
    public void writeCharacters(char[] characters, int offset, int length);

    /**This shall only be a convenience method that makes a call to #writeCharacters.
     */
    public void writeString(String string);
    
    public void startTransformedElement(String name, Attributes attributes);
    public void endTransformedElement(String name);

    public Spell clone();

    /**Checks whether the given spell is contained within this spell's hierarchy.
     * For this each respective parent is compared.
     *
     * @param spell
     * @return
     */
    public boolean hierarchyContains(Spell spell);
}
