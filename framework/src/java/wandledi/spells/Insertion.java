package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.AbstractSpell;

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
     * the enclosing element. If false it is made just after the beginning of it.
     */
    public Insertion(InsertionIntent intent, boolean insertAtEnd) {

        this.intent = intent;
        this.insertAtEnd = insertAtEnd;
    }

    /**Creates a new Insertion at the beginning of the enclosing element.
     */
    public Insertion(InsertionIntent intent) {

        this(intent, false);
    }

    public void startTransformedElement(String name, Attributes attributes) {

        parent.startElement(name, attributes);
        if (!insertAtEnd) {
            intent.insert(parent);
        }
    }

    public void endTransformedElement(String name) {

        if (insertAtEnd) {
            intent.insert(parent);
        }
        parent.endElement(name);
    }
}
