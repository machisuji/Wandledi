package wandledi.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import wandledi.java.Controller;

/**
 *
 * @author Markus Kahl
 */
public class Grimoire extends Controller {

    private GrimoireSection[] sections;

    public Grimoire() {

        initSections();
    }

    @Override
    public boolean isSpellController() {
        
        return true;
    }

    public GrimoireSection getSection(String name) {

        for (GrimoireSection section: getSections()) {
            if (section.getName().equals(name)) {
                return section;
            }
        }
        return null;
    }

    public GrimoireSection[] getSections() {

        return sections;
    }

    private void initSections() throws RuntimeException {
        
        List<Method> methods = lookupMethods();
        sections = new GrimoireSection[methods.size()];
        for (int i = 0; i < methods.size(); ++i) {
            Method method = methods.get(i);
            try {
                sections[i] = (GrimoireSection) method.invoke(this);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(Grimoire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Grimoire.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException("Could not read section.", ex);
            }
        }
    }

    protected List<Method> lookupMethods() {

        List<Method> ret = new LinkedList<Method>();
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())
                    && Grimoire.class.isAssignableFrom(method.getReturnType())) {
                ret.add(method);
            }
        }
        return ret;
    }
}
