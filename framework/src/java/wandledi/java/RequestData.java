package wandledi.java;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Markus Kahl
 */
public class RequestData {

    private HttpServletRequest request;

    public RequestData(HttpServletRequest request) {

        this.request = request;
    }

    public Object get(String key) {

        return request.getAttribute(key);
    }

    public <T> T get(Class<T> clazz, String key) {

        return clazz.cast(request.getAttribute(key));
    }

    public Object put(String key, Object value) {

        Object ret = request.getAttribute(key);
        request.setAttribute(key, value);
        return ret;
    }

    public Object remove(String key) {
        
        Object ret = request.getAttribute(key);
        if (ret != null) {
            request.setAttribute(key.toString(), null);
        }
        return ret;
    }
}
