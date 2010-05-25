package wandledi.core;

import org.xml.sax.Attributes;

/**
 *
 * @author Markus Kahl
 */
public class Characters implements SpellLine {

    private char[] characters;
    private int offset;
    private int length;

    public Characters(char[] characters, int offset, int length) {

        this.characters = characters;
        this.offset = offset;
        this.length = length;
    }

    public void perform(Spell parent) {
        parent.writeCharacters(characters, offset, length);
    }

    public final boolean isStart() {
        return false;
    }

    public final boolean isEnd() {
        return false;
    }

    public final boolean isCharacters() {
        return true;
    }

    public final Attributes getAttributes() {
        return null;
    }

    public final String getElement() {
        return null;
    }

    public final char[] getCharacters() {
        return characters;
    }

    public final int getOffset() {
        return offset;
    }

    public final int getLength() {
        return length;
    }
}
