package org.wandledi.spells;

import org.wandledi.AbstractSpell;
import org.wandledi.Spell;

/**A complex spell combines the effects of several other spells.
 *
 * For instance 'new ComplexSpell(new AttributeTransformation(...), new Duplication(n))'
 * will first perform an attribute transformation on the target element and then
 * the transformed element will be duplicated with the new attributes.
 *
 * Note that it is undefined how following spells will behave if you
 * change more than the contents of the target element first, which is the case
 * with Replacements, Inclusions and Duplications.
 *
 * That is if you have a Duplication first and then an Insertion, "the Insertion does not know"
 * where to apply. Therefore in such cases if you want the Insertion to happen multiple times,
 * first insert and duplicate last.
 *
 */
public class ComplexSpell extends AbstractSpell {

    protected Spell[] spells;

    /**Creates a complex spell with the combined effects of the given spells.
     * The effects apply in order of the given array.
     *
     * @param spells
     */
    public ComplexSpell(Spell... spells) {
        this.spells = spells;
        weave(spells);
    }

    protected ComplexSpell() {

    }

    @Override
    public void setParent(Spell parent) {
        spells[spells.length - 1].setParent(parent);
    }

    /**Weaves this spell together with the given spells,
     * so that the effects of all the spells are combined.
     *
     * @param spells Spells to weave in.
     * @return This spell (no new spell).
     */
    protected void weave(Spell... spells) {
        super.setParent(spells[0]);
        for (int i = 0; i < spells.length - 1; ++i) {
            spells[i].setParent(spells[i + 1]);
        }
    }

    @Override
    public Spell clone() {
        Spell[] copies = new Spell[spells.length];
        for (int i = 0; i < spells.length; ++i) {
            copies[i] = spells[i].clone();
        }
        return new ComplexSpell(copies);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ComplexSpell(");
        if (spells.length == 0) {
            sb.append(")");
        } else {
            sb.append(spells[0]);
            for (int i = 1; i < spells.length; ++i) {
                sb.append(", "); sb.append(spells[i]);
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
