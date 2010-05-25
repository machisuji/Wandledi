package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;

/**Hides the target element.
 *
 * @author Markus Kahl
 */
public class Invisibility extends AbstractSpell {

    public void startTransformedElement(String name, Attributes attributes) {
        // nothing
    }

    public void endTransformedElement(String name) {
        // nothing
    }
}
