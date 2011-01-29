package org.wandledi.wandlet;

import org.wandledi.SelectableImpl;
import org.wandledi.Scroll;

public class Page extends SelectableImpl implements Response {
    private String file;
    
    public Page(String file) {
        super(new Scroll());
        this.file = file;
    }
    
    public String getFile() {
        return file;
    }
}
