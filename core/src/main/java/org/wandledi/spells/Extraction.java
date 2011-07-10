package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;
import org.wandledi.Spell;
import org.wandledi.Selector;
import org.wandledi.SelectableElement;
import java.util.List;
import java.util.ArrayList;

/**Extracts a given Node from the document.
 *
 * @author Markus Kahl
 */
public class Extraction extends ComplexSpell {

    private ArchSpell main;
    private Consume consume;
    private Selector selector;
    private boolean extract = false;

    public Extraction(Selector selector) {
        this.main = new ArchSpell();
        this.consume = new Consume();
        this.selector = selector;
        this.spells = new Spell[] { main, consume };
        weave(spells);
        main.getScroll().addSpell(selector, new Pass());
    }

    class Consume extends AbstractSpell {
        @Override
        public Spell clone() {
            return new Consume();
        }

        public void startTransformedElement(String name, Attributes attributes) {
            if (extract) {
                super.startTransformedElement(name, attributes);
            }
        }

        public void endTransformedElement(String name) {
            if (extract) {
                super.endTransformedElement(name);
            }
        }

        public void startElement(String name, Attributes attributes) {
            if (extract) {
                super.startElement(name, attributes);
            }
        }

        public void endElement(String name) {
            if (extract) {
                super.endElement(name);
            }
        }

        public void writeCharacters(char[] chars, int offset, int length) {
            if (extract) {
                super.writeCharacters(chars, offset, length);
            }
        }
    }

    class Pass extends AbstractSpell {
        private int level = 0;

        @Override
        public Spell clone() {
            return new Pass();
        }

        public void startTransformedElement(String name, Attributes attributes) {
            if (ignoreBounds()) {
                startElement(name, attributes);
            } else {
                if (!extract) {
                    extract = true;
                    level = 0;
                }
                ++level;
                parent.startTransformedElement(name, attributes);
            }
        }

        public void endTransformedElement(String name) {
            if (ignoreBounds()) {
                endElement(name);
            } else {
                --level;
                parent.endTransformedElement(name);
                if (level <= 0) {
                    extract = false;
                }
            }
        }
    }
}
