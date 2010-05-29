package wandledi.java.html;

/**
 *
 * @author Markus Kahl
 */
public interface Plan<T> {

    public abstract void execute(Element e, T item);
}
