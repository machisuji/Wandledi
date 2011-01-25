package org.wandledi;

import org.xml.sax.Attributes;

public class TransformedElementStart extends ElementStart {

    public TransformedElementStart(String name, Attribute... attributes) {

        super(name, attributes);
    }

    public TransformedElementStart(String name, Attributes attributes) {

        super(name, attributes);
    }

    @Override
    public void perform(Spell parent) {

        parent.startTransformedElement(name, attributes);
    }
}
