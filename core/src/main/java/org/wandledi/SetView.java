package org.wandledi;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Markus Kahl
 * @param <F> From
 * @param <T> To
 */
public abstract class SetView<F, T> implements Set<T> {

    private Collection<F> collection;

    public SetView(Collection<F> collection) {

        this.collection = collection;
    }

    public abstract T map(F element);

    public Collection<F> getViewedCollection() {

        return this.collection;
    }

    public int size() {

        return collection.size();
    }

    public boolean isEmpty() {

        return collection.isEmpty();
    }

    public boolean contains(Object o) {

        for (F element: collection) {
            if (map(element).equals(o)) {
                return true;
            }
        }
        return false;
    }

    public Iterator<T> iterator() {

        return new ViewIterator();
    }

    public Object[] toArray() {

        Object[] names = new String[collection.size()];
        Iterator<F> i = collection.iterator();
        for (int j = 0; j < names.length && i.hasNext(); ++j) {
            names[j] = map(i.next());
        }
        return names;
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean add(T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {

        collection.clear();
    }

    public class ViewIterator implements Iterator<T> {

        private Iterator<F> i = collection.iterator();

        public boolean hasNext() {

            return i.hasNext();
        }

        public T next() {

            return map(i.next());
        }

        public void remove() {

            i.remove();
        }

    }
}
