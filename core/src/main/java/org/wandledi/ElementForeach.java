package org.wandledi;

import java.util.Collection;

/**
 *
 * @author Markus Kahl
 */
public interface ElementForeach<T> {

    /**Applies the given plan onto each item within the collection.
     *
     * @param plan
     */
    public abstract void apply(Plan<T> plan);
}
