package wandledi.core;

import org.xml.sax.Attributes;

/**
 *
 * @author Markus Kahl
 */
public interface SpellLine {

    public boolean isStart();
    public boolean isEnd();
    public boolean isCharacters();

    public Attributes getAttributes();
    public String getElement();
    public char[] getCharacters();
    public int getOffset();
    public int getLength();

    /**When this is called, the line is performed as part of the given spell.
     *
     * @param parent The spell in which this line is to be performed.
     */
    public void perform(Spell parent);
}
