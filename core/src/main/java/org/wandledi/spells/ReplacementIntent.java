package org.wandledi.spells;

import org.xml.sax.Attributes;
import org.wandledi.Spell;

/**
 *
 * @author Markus Kahl
 */
public interface ReplacementIntent {

    void replace(String label, Attributes attributes, Spell parent);
}
