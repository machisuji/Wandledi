package org.wandledi;

/**Used to record a call of ContentHandler#characters(char[],int,int).
 *
 * @author Markus Kahl
 */
public class Characters implements SpellLine {

    private char[] characters;
    private boolean safe;

    public Characters(char[] characters, int offset, int length, boolean safe) {

        this.characters = new char[length];
        this.safe = safe;
        System.arraycopy(characters, offset, this.characters, 0, length);
    }

    public void perform(Spell parent) {
        parent.writeCharacters(characters, 0, characters.length, safe);
    }
}
