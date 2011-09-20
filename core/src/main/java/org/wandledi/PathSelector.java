package org.wandledi;

import org.xml.sax.Attributes;
import java.util.List;
import java.util.Arrays;

/**A PathSelector matches only against the element path ignoring everything else.
 * Note that the element path does not include the currently processed element,
 * meaning that the element path is empty for the root element.
 */
public class PathSelector implements Selector {

    private List<ElementStart> elementPath;

    public PathSelector(List<ElementStart> elementPath) {
        this.elementPath = elementPath;
    }

    public PathSelector(ElementStart... elements) {
        this(Arrays.asList(elements));
    }

    /**Tries to match this selector against the element with the given
     * label and attributes.
     *
     * @param label
     * @param attributes
     * @param elementPath
     * @return
     */
    public boolean matches(String label, Attributes attributes, List<ElementStart> elementPath) {
        return this.elementPath.equals(elementPath);
    }

    public int compareTo(Selector o) {
        if (o instanceof CssSelector) {
            return 10;
        } else if (o instanceof UniversalSelector) {
            return 5;
        } else if (o instanceof PathSelector) {
            return 0;
        } else {
            return -10;
        }
    }

    @Override
    public String toString() {
        return "PathSelector" + elementPath.toString();
    }

    @Override
    public int hashCode() {
        return elementPath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PathSelector) {
            PathSelector that = (PathSelector) o;
            return this.elementPath.equals(that.elementPath);
        } else {
            return false;
        }
    }
}
