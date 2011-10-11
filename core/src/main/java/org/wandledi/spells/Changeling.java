package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.AbstractSpell;
import org.wandledi.Spell;


/**A changeling acts as another spell each time it is cast.
 */
public class Changeling extends AbstractSpell {

    private Spell[] identities;
    private int index = 0;

    /**Creates a new changeling with the given identities.
     *
     * @param identities Spells to mimic. Must be at least one.
     */
    public Changeling(Spell... identities) {

        this.identities = identities;
    }

    public Spell[] getIdentities() {

        return this.identities;
    }

    /**Makes this changeling change its identity.
     */
    public void change() {

        ++index;
        if (index >= identities.length) {
            index = 0;
        }
    }

    public void startTransformedElement(String name, Attributes attributes) {

        if (ignoreBounds()) {
            startElement(name, attributes);
        } else {
            identities[index].startTransformedElement(name, attributes);
        }
    }

    /**The changeling changes its identity with each element it is cast upon.
     *
     * @param name
     */
    public void endTransformedElement(String name) {

        if (ignoreBounds()) {
            endElement(name);
        } else {
            identities[index].endTransformedElement(name);
            change();
        }
    }

    public void setParent(Spell spell) {

        for (Spell id: identities) {
            id.setParent(spell);
        }
    }

    public void startElement(String name, Attributes attributes) {

        identities[index].startElement(name, attributes);
    }

    public void endElement(String name) {

        identities[index].endElement(name);
    }

    public void writeCharacters(char[] characters, int offset, int length, boolean safe) {

        identities[index].writeCharacters(characters, offset, length, safe);
    }

    public void writeString(String string, boolean safe) {

        identities[index].writeString(string, safe);
    }

    @Override
    public Spell clone() {
        return identities[index].clone();
    }

    public boolean hierarchyContains(Spell spell) {

        return identities[index].hierarchyContains(spell);
    }

    /**Clones the changeling itself.
     *
     * @return A changeling with the same identities as this one.
     */
    public Spell cloneChangeling() {

        Spell[] copies = new Spell[identities.length];
        for (int i = 0; i < copies.length; ++i) {
            copies[i] = identities[i].clone();
        }
        return new Changeling(copies);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Changeling(identities: ");
        if (identities.length == 0) {
            sb.append(")");
        } else {
            sb.append(identities[0]);
            for (int i = 1; i < identities.length; ++i) {
                sb.append(", "); sb.append(identities[i]);
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
