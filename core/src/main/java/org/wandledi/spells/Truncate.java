package org.wandledi.spells;

import org.wandledi.AbstractSpell;
import org.wandledi.Spell;
import org.xml.sax.Attributes;

/**
 * Truncates a node, meaning that it drops outer nodes and only leaves the inner ones.
 * For instance if you truncate a list (ul) by 1 you will cut off the &lt;ul&gt; only
 * leaving the contained list items.
 *
 * @author Markus Kahl
 */
public class Truncate extends AbstractSpell{

    protected int depth;
    protected int level;

    public Truncate(int depth) {
        this.depth = depth;
        if (depth < 0) throw new IllegalArgumentException("Truncation depth must be at least 0");
    }

    protected Truncate() { }

    @Override
    public Spell clone() {
        return new Truncate(depth);
    }

    public void startTransformedElement(String name, Attributes attributes) {
        if (ignoreBounds()) {
            startElement(name, attributes);
        } else if (level++ >= depth) {
            super.startTransformedElement(name, attributes);
        }
    }

    public void endTransformedElement(String name) {
        if (ignoreBounds()) {
            super.endTransformedElement(name);
        } else if (level-- > depth) {
            super.endTransformedElement(name);
        }
    }

    @Override
    public void startElement(String name, Attributes attributes) {
        if (level++ >= depth) {
            super.startElement(name, attributes);
        }
    }

    @Override
    public void endElement(String name) {
        if (level-- > depth) {
            super.endElement(name);
        }
    }

    @Override
    public void writeCharacters(char[] characters, int offset, int length, boolean safe) {
        if (level >= depth) {
            super.writeCharacters(characters, offset, length, safe);
        }
    }

    @Override
    public String toString() {
        return "Truncate(depth: " + depth + ")";
    }
}
