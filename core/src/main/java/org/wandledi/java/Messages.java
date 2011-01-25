package wandledi.java;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

/**Encapsulates a ResourceBundle for i18n.
 *
 * @author markus
 */
public class Messages implements Map<String, String> {

    private ResourceBundle bundle;

    public Messages(String baseName) {

        this.bundle = ResourceBundle.getBundle(baseName);
    }

    public Messages(String baseName, String language) {

        this.bundle = ResourceBundle.getBundle(baseName, new Locale(language));
    }

    public int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsKey(Object key) {

        return bundle.containsKey(key.toString());
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String get(Object key) {

        if (bundle.containsKey(key.toString())) {
            return bundle.getString(key.toString());
        } else {
            return null;
        }
    }

    public String put(String key, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String remove(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clear() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<String> keySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> values() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<Entry<String, String>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
