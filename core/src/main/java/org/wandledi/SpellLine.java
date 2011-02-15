package org.wandledi;

/**
 *
 * @author Markus Kahl
 */
public interface SpellLine {
    /**When this is called, the line is performed as part of the given spell.
     *
     * @param parent The spell in which this line is to be performed.
     */
    public void perform(Spell parent);
}
