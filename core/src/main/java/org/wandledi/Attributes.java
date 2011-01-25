package org.wandledi;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**HTML element attributes.
 *
 * @author markus
 */
public class Attributes implements Map<String, String> {

    private List<Attribute> attributes;
    private Attribute lastFoundAttribute;

    public Attributes() {

        this.attributes = new LinkedList<Attribute>();
    }

    public int size() {

        return attributes.size();
    }

    public boolean isEmpty() {

        return attributes.isEmpty();
    }

    public boolean containsKey(Object key) {

        for (Attribute attr: attributes) {
            if (attr.getName().equals(key)) {
                this.lastFoundAttribute = attr;
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {

        for (Attribute attr: attributes) {
            if (attr.getValue().equals(value.toString())) {
                return true;
            }
        }
        return false;
    }

    public String get(Object key) {

        Attribute attr = getAttribute(key.toString());
        return attr != null ? attr.getValue() : null;
    }

    public Attribute getAttribute(String name) {
        
        if (lastFoundAttribute != null &&
                lastFoundAttribute.getName().equals(name)) {
            return lastFoundAttribute;
        } else {
            for (Attribute attr: attributes) {
                if (attr.getName().equals(name)) {
                    return attr;
                }
            }
            return null;
        }
    }

    public String put(String key, String value) {
        
        Attribute attr = containsKey(key) ? getAttribute(key) : new Attribute(key);
        String oldValue = attr.getValue();
        attr.setValue(value);

        return oldValue;
    }

    public String remove(Object key) {
        
        String value = null;
        Iterator<Attribute> i = attributes.iterator();
        while (i.hasNext()) {
            Attribute attr = i.next();
            if (attr.getName().equals(key.toString())) {
                value = attr.getValue();
                i.remove();
            }
        }
        return value;
    }

    public void putAll(Map<? extends String, ? extends String> m) {

        for (String name: m.keySet()) {
            put(name, m.get(name));
        }
    }

    public void clear() {

        attributes.clear();
    }

    public Set<String> keySet() {

        return new SetView<Attribute, String>(attributes) {
            @Override
            public String map(Attribute element) {
                return element.getName();
            }
        };
    }

    public Collection<String> values() {

        return new SetView<Attribute, String>(attributes) {
            @Override
            public String map(Attribute element) {
                return element.getValue();
            }
        };
    }

    public Set<Entry<String, String>> entrySet() {

        return new SetView<Attribute, Entry<String, String>>(attributes) {
            @Override
            public Entry<String, String> map(final Attribute element) {
                return new Entry<String, String>() {
                    public String getKey() {
                        return element.getName();
                    }
                    public String getValue() {
                        return element.getValue();
                    }
                    public String setValue(String value) {
                        String oldValue = element.getValue();
                        element.setValue(value);
                        return oldValue;
                    }
                };
            }
        };
    }

    public List<Attribute> toList() {

        return attributes;
    }
}
