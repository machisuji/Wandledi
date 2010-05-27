package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;
import wandledi.core.Spell;

/**Hides the target element.
 *
 * @author Markus Kahl
 */
public class Invisibility extends AbstractSpell {

    @Override
    public Spell clone() {

        return new Invisibility();
    }

    public void startTransformedElement(String name, Attributes attributes) {
        // nothing
    }

    public void endTransformedElement(String name) {
        // nothing
    }
}
