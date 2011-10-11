package org.wandledi.spells;

import java.util.Iterator;
import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;
import org.wandledi.Spell;
import org.wandledi.ElementStart;
import org.wandledi.ElementEnd;
import org.wandledi.TransformedElementStart;
import org.wandledi.SimpleAttributes;
import org.wandledi.SpellLine;
import org.wandledi.Characters;

/**This transformations needs to store the whole element tree of the targeted
 * element in memory in order to be able to duplicate it.
 * I'm just saying, keep this in mind.
 *
 * @author Markus Kahl
 */
public class Duplication extends AbstractSpell {

    private DuplicationIntent intent;

    public Duplication(DuplicationIntent intent) {

        this.intent = intent;
    }

    public Duplication(final int number) {

        this.intent = new DuplicationIntent() {

            public int duplications() {
                return number;
            }

            public Spell modification() {
                return null;
            }
        };
    }

    /**This duplication applies a given modification during each duplication.
     *
     * @param number
     * @param modification
     */
    public Duplication(final int number, final Spell modification) {

        this.intent = new DuplicationIntent() {

            public int duplications() {
                return number;
            }

            public Spell modification() {
                return modification;
            }
        };
    }

    @Override
    public Spell clone() {

        return new Duplication(intent);
    }

    public void startTransformedElement(String name, Attributes attributes) {

        if (ignoreBounds()) {
            startElement(name, attributes);
        } else {
            reset();
            pushLine(new TransformedElementStart(name, new SimpleAttributes(attributes)));
        }
    }

    public void endTransformedElement(String name) {

        if (ignoreBounds()) {
            endElement(name);
        } else {
            SpellLine start = pullLine();
            Spell parent = this.parent;
            if (intent.modification() != null) {
                parent = new ComplexSpell(intent.modification(), this.parent);
            }
            for (int i = 0; i < intent.duplications(); ++i) {
                start.perform(parent);
                Iterator<SpellLine> e = lines.iterator();
                while (e.hasNext()) {
                    SpellLine line = e.next();
                    line.perform(parent);
                }
                parent.endTransformedElement(name);
            }
            clearLines();
        }
    }

    @Override
    public void startElement(String name, Attributes attributes) {
        pushLine(new ElementStart(name, new SimpleAttributes(attributes)));
    }

    @Override
    public void endElement(String name) {
        pushLine(new ElementEnd(name));
    }

    @Override
    public void writeCharacters(char[] characters, int offset, int length, boolean safe) {
        pushLine(new Characters(characters, offset, length, safe));
    }

    @Override
    public String toString() {
        return "Duplication(number: " + intent.duplications() + ", modification: " + intent.modification() + ")";
    }
}
