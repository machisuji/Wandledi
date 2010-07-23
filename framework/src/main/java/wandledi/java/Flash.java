package wandledi.java;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**Like flash in Rails.
 *
 * Example action:
 *
 * flash.put("message", "Everything is alright");
 * redirect("aController", "anAction"); // message will still be available within anAction
 *
 * @author Markus Kahl
 */
public class Flash implements Map<String, Object> {

    protected HashMap<String, FlashEntry> map = new HashMap<String, FlashEntry>();

    /**Puts the given values into the flash context under the specified key.
     *
     * @param key Flash entry's name
     * @param value Flash entry's value
     * @param ttl Time to live, the entry will be removed after <ttl> requests
     *
     * @return The previous entry or null if there was none
     */
    public Object put(String key, Object value, int ttl) {

        return map.put(key, new FlashEntry(value, ttl));
    }

    /**Returns a flash entry.
     *
     * @param type Flash entry's class
     * @param key Flash entry's name
     * @param <T> Flash entry's type
     *
     * @return The flash entry with the given name (key) or null if there is no such entry.
     */
    public <T> T get(Class<T> type, String key) {

        return type.cast(map.get(key).value);
    }

    /**Returns a flash entry.
     *
     * @param key Flash entry's name
     *
     * @return The flash entry with the given name (key) or null if there is no such entry.
     */
    public Object get(String key) {

        FlashEntry ret = map.get(key);
        return ret != null ? ret.value : null;
    }

    /**Called after each request in order to remove all entries whose time to live has run out.
     */
    public void purge() {

        Iterator<Entry<String, FlashEntry>> i = map.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, FlashEntry> entry = i.next();
            if (entry.getValue().decreaseTTL()) {
                i.remove();
            }
        }
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key.toString());
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object get(Object key) {

        return get(key.toString());
    }

    public Object put(String key, Object value) {

        return put(key, value, 1); // defaul ttl = 1 (next request)
    }

    public Object remove(Object key) {

        FlashEntry entry = map.remove(key.toString());
        if (entry != null) {
            return entry.value;
        } else {
            return null;
        }
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public void clear() {

        map.clear();
    }

    public Set<String> keySet() {

        return map.keySet();
    }

    public Collection<Object> values() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Set<Entry<String, Object>> entrySet() {
        throw new UnsupportedOperationException("Not supported.");
    }

    private static class FlashEntry {

        Object value;
        int ttl; // time (requests) to live

        public FlashEntry(Object value, int ttl) {

            this.value = value;
            this.ttl = ttl;
        }

        /**Decreases this entry's ttl by one.
         *
         * @return True if this entry's ttl has run out.
         */
        public boolean decreaseTTL() {

            return --ttl < 0;
        }
    }
}