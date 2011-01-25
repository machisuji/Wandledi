package org.wandledi;

/**
 *
 * @author Markus Kahl
 */
public abstract class Plan<T> {

    private int index = -1;
    private boolean last;

    protected void setIndex(int index) {

        this.index = index;
    }

    /**Returns the index of the current item.
     *
     * @return
     */
    public int index() {

        return this.index;
    }

    /**Checks whether the current item is the first one.
     *
     * @return
     */
    public boolean first() {

        return index() == 0;
    }

    protected void setLast(boolean last) {

        this.last = last;
    }

    /**Checks whether the current item is the last item.
     *
     * @return
     */
    public boolean last() {

        return this.last;
    }

    /**Checks whether the current item's index is odd.
     *
     * @return
     */
    public boolean odd() {

        return index() % 2 == 1;
    }

    /**Checks whether the current item's index is even.
     *
     * @return
     */
    public boolean even() {

        return index() % 2 == 0;
    }

    public abstract void execute(SelectableElement e, T item);
}
