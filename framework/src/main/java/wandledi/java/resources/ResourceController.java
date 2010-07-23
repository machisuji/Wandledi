package wandledi.java.resources;

import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import wandledi.java.Controller;
import wandledi.java.Database;
import wandledi.java.DefaultRoute;
import wandledi.java.Parameter;
import wandledi.java.annotations.Stateful;

/**
 *
 * @author Markus Kahl
 * @param <M> Model this resource controller is used for.
 */
@Stateful
public class ResourceController extends Controller {

    protected static Map<String, ResourceMetaData> metaData = new HashMap<String, ResourceMetaData>();
    protected static Map<String, Class> models = new HashMap<String, Class>();

    public static void addModel(Class model) {

        models.put(model.getSimpleName(), model);
    }

    public static void addModels(Class... models) {

        for (Class model: models) {
            addModel(model);
        }
    }

    public void index() {

        PrintWriter out = getWriter();
        out.println("<html><head><title>Registered Models</title></head><body>");
        out.println("<h1>Registered Models: </h1><ul>");
        for (Class model : models.values()) {
            out.println("<li><a href='" + request.getContextPath()
                    + DefaultRoute.getURI("resource", "list") + "?model=" + model.getSimpleName()
                    + "'>" + model.getSimpleName() + "</a></li>");
        }
        out.println("</ul></body></html>");
    }

    public void list() {

        list(getClass(param("model")));
    }

    public void show() {

        show(getClass(param("model")));
    }

    public void create() {

        show(getClass(param("model")));
    }

    public void edit() {

        show(getClass(param("model")));
    }

    public void save() {

        save(getClass(param("model")));
    }

    public void delete() {

        delete(getClass(param("model")));
    }

    public Collection<Options> getWriteOptions(String model) {

        return new LinkedList<Options>();
    }

    public Collection<Options> getReadOptions(String model) {

        return new LinkedList<Options>();
    }

    public Class getClass(String model) {

        return models.get(model);
    }

    public void list(Class model) {

        PrintWriter out = getWriter();
        String msg = (String) flash.get("msg");
        String query = param("query");
        String start = param("start");
        String rows = param("rows");
        ResourceMetaData rmd = metaData.get(model.getSimpleName());

        out.println("<html><head><title>" + model.getSimpleName()
                + "s</title></head><body>");
        out.println("<h3>" + model.getSimpleName() + "s</h3>");
        out.println("<p>" + (msg != null ? msg : "") + "</p>");

        String q;
        if (query == null || query.trim().equals("")) {
            query = "";
            q = "";
        } else {
            q = " WHERE " + query;
        }
        List<Object> objects = getDatabase().query(model, "SELECT o FROM "
                + Database.getEntityName(model) + " AS o " + q,
                start != null ? Integer.valueOf(start) : 0,
                rows != null ? Integer.valueOf(rows) : 10);
        if (objects.size() > 0) {
            out.println("<div>");
            out.println("<form action='' method='get'>");
            out.println("SELECT o FROM "
                    + Database.getEntityName(model) + " AS o WHERE "
                    + "<input style='width: 400px;' type='text' name='query' value='"
                    + query + "'/>");
            out.println("<input type='hidden' name='model' value='" + model.getSimpleName() + "'/>");
            out.println("</form>");
            out.println("</div>");
            out.println("<table><tr>");
            for (int i = 0; i < rmd.readNames.length; ++i) {
                String field = rmd.readNames[i];
                out.println("<th>" + field + "</th>");
            }
            out.println("<th>&nbsp;</th><th>&nbsp;</th>");
            out.println("</tr>");
            for (Object object : objects) {
                out.println("<tr>");
                for (int i = 0; i < rmd.readNames.length; ++i) {
                    String field = rmd.readNames[i];
                    out.println("<td>" + getValueFor(object, field) + "</td>");
                }
                out.println("<td><a href='" + request.getContextPath()
                        + DefaultRoute.getURI(getName(), "edit", (Long) call(object, "getId"))
                        + "?model=" + model.getSimpleName() + "'>Edit</a></td>");
                out.println("<td><a href='" + request.getContextPath()
                        + DefaultRoute.getURI(getName(), "delete", (Long) call(object, "getId"))
                        + "?model=" + model.getSimpleName() + "'>Delete</a></td>");
                out.println("</tr>");
            }
            out.println("</table>");
        } else {
            out.println("<p>There actually aren't any " + model.getSimpleName() + "s.</p>");
        }
        out.println("<div><a href='" + request.getContextPath()
                + DefaultRoute.getURI(getName(), "create") + "?model=" + model.getSimpleName() + "'>"
                + "<button>Create a " + model.getSimpleName() + "</button></a></div>");
        out.println("</body></html>");
    }

    public void show(Class model) {

        PrintWriter out = getWriter();
        String id = param("id");
        Object object = getDatabase().find(model, Long.valueOf(id));
        ResourceMetaData rmd = metaData.get(model.getSimpleName());
        out.println("<html><head><title>" + model.getSimpleName() + " " + id);
        out.println("</title></head><body>");
        if (object != null) {
            out.println("<h3>" + model.getSimpleName() + " " + id + "</h3>");
            out.println("<table>");
            for (int i = 0; i < rmd.readNames.length; ++i) {
                String field = rmd.readNames[i];
                out.println("<tr><td>" + field + ": </td>");
                out.println("<td>" + getValueFor(object, field) + "</td></tr>");
            }
            out.println("</table>");
            out.println("<a href='" + request.getContextPath()
                    + DefaultRoute.getURI(getName(), "edit", (Long) call(object, "getId"))
                    + "'>Edit</a>");
            out.println("<a href='" + request.getContextPath()
                    + DefaultRoute.getURI(getName(), "delete", (Long) call(object, "getId"))
                    + "'>Delete</a>");
        } else {
            out.println("Dude, there is no such " + model.getSimpleName() + "!");
        }
        out.println("</body></html>");
    }

    public void create(Class model) {

        PrintWriter out = getWriter();
        ResourceMetaData rmd = metaData.get(model.getSimpleName());
        out.println("<html><head><title>Create a " + model.getSimpleName()
                + "</title></head>");
        out.println("<body><h3>Create a " + model.getSimpleName() + "</h3>");
        out.println("<table><form action='save' method='POST'>");
        out.println("<input type='hidden' name='model' value='" + model.getSimpleName() + "'/>");
        for (int i = 0; i < rmd.writeNames.length; ++i) {
            String field = rmd.writeNames[i];
            Class<?> type = rmd.writeTypes[i];
            out.println("<tr><td>");
            out.println(field + ": </td><td>");
            out.println(getTagFor(model, field, type));
            out.println("</td></tr>");
        }
        out.println("<tr><td>&nbsp;</td><td><input type='submit' value='Save'/></td></tr>");
        out.println("</form></table></body></html>");
    }

    public void edit(Class model) {

        PrintWriter out = getWriter();
        Object object = getDatabase().find(model, Long.valueOf(param("id")));
        ResourceMetaData rmd = metaData.get(model.getSimpleName());
        out.println("<html><head><title>Edit a " + model.getSimpleName()
                + "</title></head>");
        out.println("<body><h3>Edit a " + model.getSimpleName() + "</h3>");
        out.println("<table><form action='save' method='POST'>");
        out.println("<input type='hidden' name='id' value='" + call(object, "getId") + "'/>");
        out.println("<input type='hidden' name='model' value='" + model.getSimpleName() + "'/>");
        for (int i = 0; i < rmd.writeNames.length; ++i) {
            String field = rmd.writeNames[i];
            Class<?> type = rmd.writeTypes[i];
            out.println("<tr><td>");
            out.println(field + ": </td><td>");
            out.println(getTagFor(model, field, type,
                    String.valueOf(call(object, buildMethod("get", field)))));
            out.println("</td></tr>");
        }
        out.println("<tr><td>&nbsp;</td><td><input type='submit' value='Save'/></td></tr>");
        out.println("</form></table></body></html>");
    }

    public void save(Class model) {

        Object bean = getInstance(model);
        if (bean == null) {
            throw new RuntimeException("Cannot create Model " + model.getSimpleName());
        }
        boolean ok = true;
        String id = param("id");
        if (id != null) {
            call(bean, "setId", new Object[]{Long.valueOf(id)});
        }
        ResourceMetaData rmd = metaData.get(model.getSimpleName());
        for (int i = 0; i < rmd.writeNames.length; ++i) {
            String field = rmd.writeNames[i];
            Class<?> type = rmd.writeTypes[i];
            if (type.isArray()) {
                String[] values = params(field);
                if (Number.class.isAssignableFrom(type.getComponentType())) {
                    Object[] numbers = (Object[]) Array.newInstance(type.getComponentType(),
                            values.length);
                    for (int j = 0; j < numbers.length; ++j) {
                        numbers[j] = call(type, null, "valueOf", new Object[]{String.class});
                    }
                    call(bean, buildMethod("set", field), new Object[]{numbers});
                } else if (type.getComponentType().equals(String.class)) {
                    call(bean, buildMethod("set", field), new Object[]{values});
                } else if (type.getComponentType().isAnnotationPresent(Entity.class)) {
                    Object[] entities = (Object[]) Array.newInstance(type.getComponentType(),
                            values.length);
                    for (int j = 0; j < values.length; ++j) {
                        entities[j] = getDatabase().find(type.getComponentType(),
                                Long.valueOf(values[j]));
                    }
                    call(bean, buildMethod("set", field), new Object[]{entities});
                } else {
                    // can't handle it
                    ok = false;
                }
            } else {
                String value = param(field);
                if (Number.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
                    Object number = call(type, null, "valueOf", new Object[]{String.class});
                    call(bean, buildMethod("set", field), new Object[]{number});
                } else if (int.class.isAssignableFrom(type)) {
                    call(bean, buildMethod("set", field), int.class, Integer.valueOf(value));
                } else if (long.class.isAssignableFrom(type)) {
                    call(bean, buildMethod("set", field), long.class, Long.valueOf(value));
                } else if (float.class.isAssignableFrom(type)) {
                    call(bean, buildMethod("set", field), float.class, Float.valueOf(value));
                } else if (double.class.isAssignableFrom(type)) {
                    call(bean, buildMethod("set", field), double.class, Double.valueOf(value));
                } else if (boolean.class.isAssignableFrom(type)) {
                    call(bean, buildMethod("set", field), boolean.class, Boolean.valueOf(value));
                } else if (type.equals(String.class)) {
                    call(bean, buildMethod("set", field), new Object[]{value});
                } else if (type.isAnnotationPresent(Entity.class)) {
                    call(bean, buildMethod("set", field), new Object[]{
                                getDatabase().find(type, Long.valueOf(value))
                            });
                } else {
                    // can't handle it
                    ok = false;
                }
            }
        }
        if (ok && getDatabase().merge(bean) != null) {
            flash.put("msg", model.getSimpleName() + " saved.");
        } else {
            flash.put("msg", "There were some fields I could not handle.");
        }
        redirectTo("resource", "list", new Parameter("model", model.getSimpleName()));
    }

    public void delete(Class model) {

        String id = param("id");
        if (id != null) {
            getDatabase().remove(model, Long.valueOf(id));
            flash.put("msg", model.getSimpleName() + " deleted.");
        }
        redirectTo("resource", "list", new Parameter("model", model.getSimpleName()));
    }

    private String getValueFor(Object object, String field) {

        try {
            String getter = buildMethod("get", field);
            Method method = object.getClass().getMethod(getter);
            Object ret = method.invoke(object);

            if (ret != null) {
                if (ret.getClass().isAnnotationPresent(Entity.class) && 
                        models.containsValue(ret.getClass())) {
                    Long id = (Long) call(ret, "getId");
                    String name = ret.getClass().getSimpleName();
                    name = name.substring(0, 1).toLowerCase() + name.substring(1);
                    return "<a href=\"" + request.getContextPath()
                            + DefaultRoute.getURI("resource", "show") +
                            "?model=" + ret.getClass().getSimpleName() +
                            "&id=" + String.valueOf(id) +
                            "\">" + ret.getClass().getSimpleName() + " " + id + "</a>";
                } else {
                    return ret.toString();
                }
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private <T> T getInstance(Class<T> clazz) {

        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Object call(Object object, String method, Object... args) {

        return call(object.getClass(), object, method, args);
    }

    private Object call(Class clazz, Object object, String method, Object... args) {

        try {
            return clazz.getMethod(method, getTypes(args)).invoke(object, args);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Object call(Object object, String method, Class<?> argType, Object arg) {

        return call(object.getClass(), object, method, argType, arg);
    }

    private Object call(Class clazz, Object object, String method, Class<?> argType, Object arg) {

        try {
            return clazz.getMethod(method, new Class[]{argType}).invoke(object, arg);
        } catch (Exception ex) {
            Logger.getLogger(ResourceController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Class[] getTypes(Object... objects) {

        Class[] types = new Class[objects.length];
        for (int i = 0; i < objects.length; ++i) {
            types[i] = objects[i].getClass();
        }
        return types;
    }

    private String buildMethod(String prefix, String field) {

        return prefix + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    private String getTagFor(Class model, String field, Class<?> type) {

        return getTagFor(model, field, type, null);
    }

    private String getTagFor(Class model, String field, Class<?> type, String value) {

        String ret;
        if (isSupportedType(type)) {
            Options options = getWriteOptionsFor(model, field);
            if (options.useTextArea) {
                ret = "<textarea name='" + field + "' rows='5' cols='30'>#</textarea>";
            } else {
                ret = "<input type='text' name='" + field + "' value='#'/>";
            }
        } else {
            ret = "<span style='color: red;'>Unknown type: " + type.getSimpleName() + "</span>";
        }
        if (value == null) {
            value = "";
        }
        return ret.replaceAll("#", value);
    }

    protected Options getWriteOptionsFor(Class model, String field) {

        ResourceMetaData rmd = metaData.get(model.getSimpleName());
        for (Options options : rmd.writeOptions) {
            if (options.field.equals(field)) {
                return options;
            }
        }
        return new Options("");
    }

    protected Options getReadOptionsFor(Class model, String field) {

        ResourceMetaData rmd = metaData.get(model.getSimpleName());
        for (Options options : rmd.readOptions) {
            if (options.field.equals(field)) {
                return options;
            }
        }
        return new Options("");
    }

    private boolean isSupportedType(Class<?> type) {
        return type.equals(String.class) || Number.class.isAssignableFrom(type)
                || int.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)
                || long.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)
                || boolean.class.isAssignableFrom(type);
    }

    @Override
    protected void controllerRegistered() {

        for (Class<?> model : models.values()) {
            LinkedList<String> wNames = new LinkedList<String>();
            LinkedList<Class<?>> wTypes = new LinkedList<Class<?>>();
            LinkedList<String> rNames = new LinkedList<String>();
            LinkedList<Class<?>> rTypes = new LinkedList<Class<?>>();
            Method[] methods = model.getMethods();
            for (Method method : methods) {
                String name = method.getName();
                if (name.startsWith("set")) {
                    String field = name.substring(3);
                    if (Character.isUpperCase(Character.codePointAt(field, 0))) {
                        field = field.substring(0, 1).toLowerCase() + field.substring(1);
                        Class[] parameters = method.getParameterTypes();
                        if (!field.equals("id") && parameters.length == 1) {
                            wNames.add(field);
                            wTypes.add(parameters[0]);
                        }
                    }
                } else if (name.startsWith("get")) {
                    String field = name.substring(3);
                    if (Character.isUpperCase(Character.codePointAt(field, 0))) {
                        field = field.substring(0, 1).toLowerCase() + field.substring(1);
                        Class<?> type = method.getReturnType();
                        if (!field.equals("class") && !type.isAssignableFrom(void.class)) {
                            rNames.add(field);
                            rTypes.add(type);
                        }
                    }
                }
            }
            ResourceMetaData rmd = new ResourceMetaData();
            rmd.writeNames = wNames.toArray(new String[wNames.size()]);
            rmd.writeTypes = wTypes.toArray(new Class[wTypes.size()]);
            rmd.readNames = rNames.toArray(new String[rNames.size()]);
            rmd.readTypes = rTypes.toArray(new Class[rTypes.size()]);
            rmd.writeOptions = getWriteOptions(model.getSimpleName());
            rmd.readOptions = getReadOptions(model.getSimpleName());
            metaData.put(model.getSimpleName(), rmd);
        }
    }
}
