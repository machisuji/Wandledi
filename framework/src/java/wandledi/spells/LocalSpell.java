package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;
import wandledi.core.Scroll;
import wandledi.core.Selector;
import wandledi.core.Spell;

/**
 *
 * @author Markus Kahl
 */
public class LocalSpell extends AbstractSpell {

    private Scroll parentScroll;
    private Scroll localScroll;

    public LocalSpell(Scroll parentScroll, Scroll localScroll) {

        this.parentScroll = parentScroll;
        this.localScroll = localScroll;
    }

    public void startTransformedElement(String label, Attributes attributes) {

        parentScroll.addScroll(localScroll);
        super.startTransformedElement(label, attributes);
    }

    public void endTransformedElement(String label) {

        parentScroll.removeScroll(localScroll);
        super.endTransformedElement(label);
    }

    @Override
    public Spell clone() {

        return new LocalSpell(parentScroll, localScroll);
    }
}
