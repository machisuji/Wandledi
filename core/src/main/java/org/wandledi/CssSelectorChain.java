package org.wandledi;

import java.util.*;

public class CssSelectorChain {

    private List<Entry> elements;

    public CssSelectorChain(List<Entry> elements) {
        this.elements = new ArrayList<Entry>(elements);
    }

    public CssSelectorChain() {
        this(new LinkedList<Entry>());
    }

    public int size() {
        return elements.size();
    }

    protected <T> List<T> reverse(List<T> list) {
        List<T> reversed = new ArrayList<T>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    public boolean matches(List<ElementStart> elementPath) {
        Iterator<ElementStart> path = reverse(elementPath).iterator();
        Iterator<Entry> entries = reverse(elements).iterator();
        while (entries.hasNext()) {
            Entry entry = entries.next();
            boolean match = false;
            while (path.hasNext()) {
                ElementStart pe = path.next();
                match = entry.getSelector().matches(pe.getName(), pe.getAttributes());
                if (match) break;
                else if (entry.isAnscestor()) return false;
            }
            if (!match) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CssSelectorChain.Entry e: elements) {
            sb.append(e.toString());
        }
        return sb.toString();
    }

    public static class Entry {
        private CssSelector selector;
        private boolean anscestor;

        public Entry(CssSelector selector, boolean anscestor) {
            this.selector = selector;
            this.anscestor = anscestor;
        }

        public CssSelector getSelector() {
            return selector;
        }

        /**
         * Indicates whether or not the corresponding element must be a direct child
         * of its parent or not.
         */
        public boolean isAnscestor() {
            return anscestor;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(selector.toString().replaceAll("^.*\\(", ""));
            sb.setLength(sb.length() - 1);
            if (isAnscestor()) {
                sb.append(" >");
            }
            return sb.toString();
        }
    }
}
