package org.wandledi.wandlet;

import org.wandledi.SelectableImpl;
import org.wandledi.Selector;
import org.wandledi.Scroll;
import org.wandledi.PathSelector;

public class Page extends SelectableImpl implements Response {
    private String file;

    public Page(String file) {
        super(new Scroll());
        this.file = file;
    }
    /** Drops all the content of this page except for the target Element.
     */
    public void extract(Selector target) {
        get(new PathSelector()).extract(target);
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "Page(" + getFile() + ")";
    }
}
