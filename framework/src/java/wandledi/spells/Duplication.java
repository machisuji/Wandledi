package wandledi.spells;

import java.util.Iterator;
import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;
import wandledi.core.Characters;
import wandledi.core.ElementEnd;
import wandledi.core.ElementStart;
import wandledi.core.SimpleAttributes;
import wandledi.core.Spell;
import wandledi.core.SpellLine;

/**This transformations needs to store the whole element tree of the targetted
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
        startElement(name, attributes);
    }

    public void endTransformedElement(String name) {

        ElementStart start = (ElementStart) pullLine();

        start.perform(parent);
        for (int i = 0; i < intent.duplications(); ++i) {
            Iterator<SpellLine> e = lines.iterator();
            while (e.hasNext()) {
                SpellLine line = e.next();
                line.perform(parent);
            }
        }
        parent.endElement(name);
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
