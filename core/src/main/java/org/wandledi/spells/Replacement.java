package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;
import org.wandledi.Spell;

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

        if (ignoreBounds()) {
            startElement(name, attributes);
        } else {
            if (contentsOnly) {
                justStarted = true;
                super.startTransformedElement(name, attributes);
            }
            intent.replace(name, attributes, parent);
        }
    }

    public void endTransformedElement(String name) {

        if (ignoreBounds()) {
            endElement(name);
        } else {
            if (contentsOnly) {
                super.endTransformedElement(name);
            }
        }
    }

    @Override
    public void startElement(String name, Attributes attributes) {

        if (contentsOnly) {
            justStarted = false;
        }
    }

    @Override
    public void endElement(String name) {
    }

    @Override
    public void writeCharacters(char[] characters, int offset, int length, boolean safe) {

        if (contentsOnly && justStarted) {
            String string = new String(characters, offset, length);
            if (string.trim().length() == 0) { // keep whitespace, linebreaks etc.
                parent.writeCharacters(characters, offset, length, safe);
            } else {
                justStarted = false;
            }
        }
    }

    @Override
    public String toString() {
        return "Replacement(contentsOnly: " + contentsOnly + ", intent: " + intent + ")";
    }
}
