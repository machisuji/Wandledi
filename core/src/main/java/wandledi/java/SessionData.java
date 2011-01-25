package wandledi.java;

import javax.servlet.http.HttpSession;

/**
 *
 * @author Markus Kahl
 */
public class SessionData {

    private HttpSession session;

    public SessionData(HttpSession session) {

        this.session = session;
    }

    public <T> T get(Class<T> clazz, String key) {

        return clazz.cast(session.getAttribute(key));
    }

    public Object get(String key) {

        return session.getAttribute(key);
    }

    public void put(String key, Object value) {

        session.setAttribute(key, value);
    }
}
