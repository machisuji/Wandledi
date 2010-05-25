package wandledi.spells;

import org.xml.sax.Attributes;
import wandledi.core.Spell;

/**
 *
 * @author Markus Kahl
 */
public interface ReplacementIntent {

    void replace(String label, Attributes attributes, Spell parent);
}
