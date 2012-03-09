package org.wandledi;

import org.xml.sax.Attributes;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.wandledi.util.Methods.select;


/**
 * Note: This class has a natural ordering that is inconsistent with equals.
 *
 * This class implements simple CSS Selectors.
 * Support for labels, IDs, classes, general attributes and nesting.
 *
 * @author Markus Kahl
 */
public class CssSelector implements Selector {

    private String label;
    private Attributes attributes;
    private CssSelectorChain parents = new CssSelectorChain();

    public CssSelector(String label, Attributes attributes) {
        this.label = label;
        this.attributes = attributes;
    }

    public CssSelector(String label, Attribute... attributes) {
        this(label, new SimpleAttributes(attributes));
    }

    /**
     * Parses a given css selector, which may be nested, too.
     *
     * @param selector CSS selector to be parsed
     * @return A corresponding CssSelector instance
     *
     * @throws IllegalArgumentException if the given css selector is invalid or not supported.
     */
    public static CssSelector valueOf(String selector) {
        LinkedList<CssSelectorChain.Entry> selectors = new LinkedList<CssSelectorChain.Entry>();
        StringBuilder sb = new StringBuilder(selector);
        while (sb.length() > 0) {
            CssSelector sel = parseSingleSelector(sb);
            boolean anscestor = parseAncestor(sb);
            selectors.add(new CssSelectorChain.Entry(sel, anscestor));
        }
        CssSelector sel = selectors.removeLast().getSelector();
        sel.parents = new CssSelectorChain(selectors);

        return sel;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    /**
     * Parses a single, flat css selector. No nesting supported.
     *
     * @param selector CSS selector to be parsed
     * @return A corresponding CssSelector instance
     *
     * @throws IllegalArgumentException if the given css selector is invalid or not supported.
     *
     * @TODO make parse methods instance methods and don't recreate regex every time
     */
    private static CssSelector parseSingleSelector(StringBuilder selector) {
        String attr = "(?:([\\w\\-_]+)\\s*=\\s*(?:([\\w\\./:\\-_&&[^,]]*)|(?:\"([^\"]*)\")|(?:'([^']*)')))";
        String regex = "(\\w+)?((?:\\.[\\w\\-_]+)+)?(#[\\w\\-_]+)?(\\[" + attr + "(?:,\\s*" + attr + ")*\\])?";
        try {
            Pattern c3s = Pattern.compile(regex);
            Pattern atts = Pattern.compile(attr + "(?:,|\\s*\\])");
            Matcher matcher = c3s.matcher(selector.toString());

            if (matcher.find()) {
                String label = matcher.group(1);
                String classes = matcher.group(2);
                String id = matcher.group(3);
                String attsString = matcher.group(4);
                List<Attribute> attributes = new LinkedList<Attribute>();

                if (classes != null) {
                    StringBuilder value = new StringBuilder();
                    for (String klass: classes.split("\\.")) {
                        if (!klass.isEmpty()) {
                            value.append(klass);
                            value.append(" ");
                        }
                    }
                    if (value.charAt(value.length() - 1) == ' ') {
                        value.setLength(value.length() - 1);
                    }
                    attributes.add(new Attribute("class", value.toString()));
                }
                if (id != null) {
                    attributes.add(new Attribute("id", id.substring(1)));
                }
                if (attsString != null) {
                    Matcher am = atts.matcher(attsString);
                    while (am.find()) {
                        String name = am.group(1);
                        String value = am.group(2);
                        if (value == null) {
                            value = am.group(3);
                        }
                        if (value == null) {
                            value = am.group(4);
                        }
                        attributes.add(new Attribute(name, value));
                    }
                }
                selector.delete(0, matcher.end() + 1);
                return new CssSelector(label,
                        new SimpleAttributes(attributes.toArray(new Attribute[attributes.size()])));
            } else {
                throw new IllegalArgumentException("\"" + selector + "\" is not a valid/supported css selector.");
            }
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("Invalid regex (for css selector): " + e.getMessage(), e);
        }
    }

    private static boolean parseAncestor(StringBuilder selector) {
        Pattern regex = Pattern.compile("\\s*>\\s*");
        Matcher matcher = regex.matcher(selector.toString());
        if (matcher.find()) {
            selector.delete(matcher.start(), matcher.end());
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CssSelector(");
        sb.append(parents.toString());
        if (parents.size() > 0) {
            sb.append(" ");
        }
        String id = getId();
        String klass = getElementClass();
        if (label != null) {
            sb.append(label);
        }
        if (klass != null) {
            sb.append(".");
            sb.append(klass.replace(' ', '.'));
        }
        if (id != null) {
            sb.append("#");
            sb.append(id);
        }
        if (attributes != null && attributes.getLength() > (klass == null ? 0 : 1) + (id == null ? 0 : 1)) {
            sb.append("[");
            for (int i = 0; i < attributes.getLength(); ++i) {
                String name = attributes.getLocalName(i);
                String value = attributes.getValue(i);
                if (!"class".equals(name) && !"id".equals(name)) {
                    if (sb.charAt(sb.length() - 1) != '[') {
                        sb.append(", ");
                    }
                    sb.append(name);
                    sb.append("=");
                    sb.append(value);
                }
            }
            sb.append("]");
        }
        sb.append(")");
        return sb.toString();
    }

    public boolean matches(String label, Attributes attributes, List<ElementStart> elementPath) {
        return parents.matches(elementPath) && matches(label, attributes);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CssSelector) {
            CssSelector that = (CssSelector) o;
            return this.matches(that) && that.matches(this);
        }
        return false;
    }

    public boolean matches(CssSelector selector) {
        return matches(selector.label, selector.getAttributes());
    }

    protected boolean matches(String label, Attributes attributes) {
        boolean match = true;
        if (this.label != null) {
            match &= this.label.equals(label);
        }
        for (int i = 0; i < this.attributes.getLength(); ++i) {
            String name = this.attributes.getLocalName(i);
            String value = this.attributes.getValue(i);
            if (name != null && value != null) {
                if ("class".equalsIgnoreCase(name)) {
                    List<String> theseClasses = split(value, " ", true);
                    List<String> thoseClassses = split(attributes.getValue("class"), " ", true);
                    match &= thoseClassses.containsAll(theseClasses);
                } else {
                    match &= (value.equals(attributes.getValue(name)));
                }
            }
        }
        return match;
    }

    protected List<String> split(String value, String delim, boolean toLowerCase) {
        if (value == null) {
            return new LinkedList<String>();
        } else {
            String[] values = value.split(delim);
            for (int i = 0; i < values.length && toLowerCase; ++i) {
                values[i] = values[i].toLowerCase();
            }
            return Arrays.asList(values);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash += 5 * label.hashCode();
        hash += 7 * attributes.hashCode();
        return hash;
    }

    public int compareTo(Selector o) {
        if (o instanceof CssSelector) {
            CssSelector selector = (CssSelector) o;
            if (this.getId() != null && selector.getId() != null) {
                return 0;
            } else if (this.getId() != null && selector.getId() == null) {
                return -2;
            } else if (this.getId() == null && selector.getId() != null) {
                return 2;
            } else {
                if (this.getElementClass() != null && selector.getElementClass() == null) {
                    return -1;
                } else if (this.getElementClass() == null && selector.getElementClass() != null) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return -10;
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return attributes.getValue("id");
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the elementClass
     */
    public String getElementClass() {
        return attributes.getValue("class");
    }
}
