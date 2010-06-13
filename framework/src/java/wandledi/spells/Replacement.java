package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;
import wandledi.core.Spell;

/**
 *
 * @author Markus Kahl
 */
public class Replacement extends AbstractSpell {

    private boolean contentsOnly;
    private boolean justStarted;
    private ReplacementIntent intent;

    public Replacement(ReplacementIntent intent, boolean contentsOnly) {

        this.intent = intent;
        this.contentsOnly = contentsOnly;
    }

    public Replacement(ReplacementIntent intent) {

        this(intent, false);
    }

    @Override
    public Spell clone() {

        return new Replacement(intent, contentsOnly);
    }

    public void startTransformedElement(String name, Attributes attributes) {

        if (contentsOnly) {
            justStarted = true;
            super.startTransformedElement(name, attributes);
        }
        intent.replace(name, attributes, parent);
    }

    public void endTransformedElement(String name) {
    
        if (contentsOnly) {
            super.endTransformedElement(name);
        }
    }

    @Override
    public void startElement(String name, Attributes attributes) {
        
        if (contentsOnly) {
            justStarted = false;
        }
    }
    
    @Override public void endElement(String name) { }

    @Override
    public void writeCharacters(char[] characters, int offset, int length) {

        if (contentsOnly && justStarted) {
            String string = new String(characters, offset, length);
            if (string.trim().length() == 0) { // keep whitespace, linebreaks etc.
                parent.writeCharacters(characters, offset, length);
            } else {
                justStarted = false;
            }
        }
    }
}
