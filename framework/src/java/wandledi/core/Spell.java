package wandledi.core;

import org.xml.sax.Attributes;

/**Derzeitige Limitierungen der Transformation API:
 *
 * Verschachtelung von Transformationen:
 *
 * Ein und dieselbe Transformation (Instanz) darf nicht geschachtelt werden.
 * D.h. wenn es z.B. eine Transformation gibt, die zu allen Elementen mit
 * der Klasse "info" einen Textknoten hinzufügt, dürfen zwar beliebig viele Elemente
 * einer Ebene diese Klassen besitzen, gibt es jedoch innerhalb eines Elementes mit
 * dieser Klasse mindestens ein weiteres Element mit derselben Klasse, so wird
 * die gesamte Transformation des Dokumentes fehlschlagen.
 * Dies kann umgangen werden, indem die geschachtelten Elemente gesondert selektriert
 * und transformiert werden.
 *
 * Mir ist durchaus bewusst, dass diese Situation unbefriedigend ist.
 * Ich arbeite an einer Lösung.
 *
 *
 * Transformationskomposition:
 *
 * Es ist derzeit nicht möglich, mehrere Transformationen auf ein und dasselbe Elemente
 * anzuwenden. Dies wird noch in Form einer CompositeTransformation folgen.
 *
 * Umbenannt von Transformation zu (Alteration) Spell. Transformation hat definitiv
 * zu viele Silben!
 *
 * @author Markus Kahl
 */
public interface Spell {

    public void setParent(Spell spell);
    public void startElement(String name, Attributes attributes);
    public void endElement(String name);
    public void writeCharacters(char[] characters, int offset, int length);

    /**This shall only be a convenience method that makes a call to #writeCharacters.
     */
    public void writeString(String string);
    
    public void startTransformedElement(String name, Attributes attributes);
    public void endTransformedElement(String name);
}
