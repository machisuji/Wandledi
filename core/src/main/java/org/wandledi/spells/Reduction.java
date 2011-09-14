package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;
import org.wandledi.Spell;

/**Reduces a set of selected elements to a single one.
 * Example:
 *
 * page.get("ul.users li").reduce();
 *
 * Given 'ul.users' contains any number of list items,
 * all but the first one will be removed.
 *
 * The affected elements don't have to be adjacent,
 * meaning that even if they are scattered throughout the
 * document all but the first one will disappear.
 *
 * @author Markus Kahl
 */
public class Reduction extends AbstractSpell {

    private boolean firstIteration = true;
    private int level = 0;

    @Override
    public Spell clone() {
        return new Reduction();
    }

    public void startTransformedElement(String name, Attributes attributes) {
        if (!firstIteration) return;
        super.startTransformedElement(name, attributes);
        ++level;
    }

    public void endTransformedElement(String name) {
        if (!firstIteration) return;
        super.endTransformedElement(name);
        --level;
        if (level <= 0) {
            firstIteration = false;
        }
    }

    public void startElement(String name, Attributes attributes) {
        if (!firstIteration) return;
        super.startElement(name, attributes);
        ++level;
    }

    public void endElement(String name) {
        if (!firstIteration) return;
        super.endElement(name);
        --level;
    }

    public void writeCharacters(char[] chars, int offset, int length, boolean safe) {
        if (!firstIteration) return;
        super.writeCharacters(chars, offset, length, safe);
    }
}
