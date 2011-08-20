package org.wandledi;

import org.wandledi.spells.Changeling;
import org.wandledi.spells.Duplication;
import org.wandledi.spells.ArchSpell;

import java.util.Collection;
import java.util.LinkedList;

public class ElementForeachImpl<T> implements ElementForeach<T> {

    private Element element;
    private Collection<T> collection;
    private boolean reduce = false;

    public ElementForeachImpl(Element element, Collection<T> collection) {
        this(element, collection, false);
    }

    public ElementForeachImpl(Element element, Collection<T> collection, boolean reduceBefore) {
        this.element = element;
        this.collection = collection;
        this.reduce = reduceBefore;
    }

    public void apply(Plan<T> plan) {
        Collection<Scroll> scrolls = new LinkedList<Scroll>();
        int index = 0;
        int size = collection.size();
        plan.setLast(false);
        for (T item: collection) {
            Scroll scroll = new Scroll();
            SelectableElement e = new SelectableElementImpl(
                new SelectableImpl(scroll),
                new ElementImpl(new PathSelector(), scroll));
            plan.setIndex(index++);
            if (index == size) {
                plan.setLast(true);
            }
            plan.execute(e, item);
            scrolls.add(scroll);
        }
        Spell[] modifications = new Spell[scrolls.size()];
        int mi = 0;
        for (Scroll scroll: scrolls) {
            modifications[mi++] = new ArchSpell(scroll);
        }
        Spell duplication = new Duplication(size, new Changeling(modifications));
        if (reduce) {
            element.reduce();
        }
        element.cast(duplication);
    }
}
