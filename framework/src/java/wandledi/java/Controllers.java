package wandledi.java;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author markus
 */
public class Controllers {

    private Map<String, ControllerEntry> map = new HashMap<String, ControllerEntry>();

    public Class<? extends Controller> put(String name, Class<? extends Controller> klass,
            boolean stateful) {

        ControllerEntry entry = map.put(name, new ControllerEntry(klass, stateful));
        return entry != null ? entry.controller : null;
    }

    public Class<? extends Controller> put(String name, Class<? extends Controller> klass) {

        return put(name, klass, false);
    }

    public boolean isStateful(String controller) {

        ControllerEntry entry = map.get(controller);
        if (entry == null) {
            throw new IllegalStateException("No such controller: " + controller);
        }
        return entry.stateful;
    }

    public Method put(String controller, String action, Method method) {

        ControllerEntry entry = map.get(controller);
        if (entry == null) {
            throw new IllegalStateException("No such controller: " + controller);
        }
        return entry.actions.put(action, method);
    }

    public boolean contains(String controller) {

        return map.containsKey(controller);
    }

    public boolean contains(String controller, String action) {

        ControllerEntry entry = map.get(controller);
        return entry != null ? entry.actions.containsKey(action) : false;
    }

    public Method get(String controller, String action) {

        ControllerEntry entry = map.get(controller);
        return entry != null ? entry.actions.get(action) : null;
    }

    public Class<? extends Controller> get(String controller) {

        ControllerEntry entry = map.get(controller);
        return entry != null ? entry.controller : null;
    }

    private static class ControllerEntry {

        private Class<? extends Controller> controller;
        private boolean stateful;
        private Map<String, Method> actions = new HashMap<String, Method>();

        public ControllerEntry(Class<? extends Controller> controller, boolean stateful) {

            this.controller = controller;
            this.stateful = stateful;
        }

        public ControllerEntry(Class<? extends Controller> controller) {

            this(controller, false);
        }
    }
}
