package org.wandledi.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Replaces all occurences of ampersands (&) with a magic character to hide them.
 * Later on that magic character has to be substituted again with it in the character data of the SAX events.
 *
 * This way SAX is oblivious to all used entities and character references in the document and hence leaves them alone.
 *
 * @author Markus Kahl
 * @version: 0.1
 *
 * 17.09.11
 */
public class MagicReader extends Reader {

    public static final char MAGIC_CHARACTER = (char) 5;

    private Reader in;

    public MagicReader(Reader in) {
        this.in = in;
    }

    protected void hideAllIn(char[] chars, int offset, int length) {
        for (int i = offset; i < offset + length; ++i) {
            if (chars[i] == '&') {
                chars[i] = MAGIC_CHARACTER;
            }
        }
    }

    public void showAllIn(char[] chars, int offset, int length) {
        for (int i = offset; i < offset + length; ++i) {
            if (chars[i] == MAGIC_CHARACTER) {
                chars[i] = '&';
            }
        }
    }

    @Override
    public int read(char[] chars, int offset, int length) throws IOException {
        int read = in.read(chars, offset, length);
        hideAllIn(chars, offset, read);
        return read;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
