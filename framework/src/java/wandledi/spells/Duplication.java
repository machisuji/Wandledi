package wandledi.spells;

import java.util.Iterator;
import org.xml.sax.Attributes;
import wandledi.core.*;

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
        };
    }

    @Override
    public Spell clone() {

        return new Duplication(intent);
    }

    public void startTransformedElement(String name, Attributes attributes) {

        reset();
        pushLine(new TransformedElementStart(name, new SimpleAttributes(attributes)));
    }

    public void endTransformedElement(String name) {

        SpellLine start = pullLine();
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

    @Override
    public void startElement(String name, Attributes attributes) {

        pushLine(new ElementStart(name, new SimpleAttributes(attributes)));
    }

    @Override
    public void endElement(String name) {

        pushLine(new ElementEnd(name));
    }

    @Override
    public void writeCharacters(char[] characters, int offset, int length) {

        pushLine(new Characters(characters, offset, length));
    }
}
