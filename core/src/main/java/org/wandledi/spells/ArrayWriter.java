package org.wandledi.spells;

/**A StringTransformation that discards incoming values and
 * yields the values from a given array.
 *
 * If the transformation is used more often than it has values,
 * the last value is going to be reused.
 *
 * @author Markus Kahl
 */
class ArrayWriter implements StringTransformation {

    private int index = -1;
    private String[] values;

    public ArrayWriter(String... values) {
        this.values = values;
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("I need some values (-> values empty)!");
        }
    }

    public String transform(String input) {
        if (index + 1 < values.length) {
            ++index;
        }
        return values[index];
    }
}
