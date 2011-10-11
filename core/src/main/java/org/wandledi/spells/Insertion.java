package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;

/**
 *
 * @author Markus Kahl
 */
public class Insertion extends AbstractSpell {

    private boolean insertAtEnd;
    private InsertionIntent intent;

    /**Creates a new Insertion.
     *
     * @param insertAtEnd If true the insertion is made just before the end of
     */
    public Insertion(boolean insertAtEnd, InsertionIntent intent) {

        this.intent = intent;
        this.insertAtEnd = insertAtEnd;
    }

    /**Creates a new Insertion at the beginning of the enclosing element.
     */
    public Insertion(InsertionIntent intent) {

        this(false, intent);
    }

    @Override
    public void startTransformedElement(String name, Attributes attributes) {

        if (ignoreBounds()) {
            startElement(name, attributes);
        } else {
            super.startTransformedElement(name, attributes);
            if (!insertAtEnd) {
                intent.insert(parent);
            }
        }
    }

    @Override
    public void endTransformedElement(String name) {

        if (ignoreBounds()) {
            endElement(name);
        } else {
            if (insertAtEnd) {
                intent.insert(parent);
            }
            super.endTransformedElement(name);
        }
    }

    @Override
    public Insertion clone() {
        return new Insertion(insertAtEnd, intent);
    }

    @Override
    public String toString() {
        return "Insertion(atEnd: " + insertAtEnd + ", intent: " + intent + ")";
    }
}
