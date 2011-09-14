package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;
import org.wandledi.Spell;

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

    public void startElement(String name, Attributes attributes) {

    }

    public void endElement(String name) {

    }

    public void writeCharacters(char[] chars, int offset, int length, boolean safe) {
        
    }
}
