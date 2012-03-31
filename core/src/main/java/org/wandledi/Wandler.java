package org.wandledi;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wandledi.io.MagicReader;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
import org.wandledi.spells.ArchSpell;

import nu.validator.htmlparser.sax.HtmlParser;
import nu.validator.htmlparser.common.XmlViolationPolicy;

/**
 *
 * @author Markus Kahl
 */
public class Wandler implements ContentHandler, Spell {

    private XMLReader parser;
    private Locator locator;
    private BufferedWriter out;
    private boolean xhtml = false;
    private boolean preserve = false;
    private boolean startUnderConstruction = false;
    private boolean hideEntitiesAndCharacterReferencesFromSax = false;
    private long calls = 0;
    private long lastStart = -1;
    private final static Set<String> emptyElements = new HashSet<String>(
        java.util.Arrays.asList(
            "area", "base", "basefont", "br", "col", "hr", "img", "input",
            "isindex", "link", "meta", "param"
    ));
    private ArchSpell rootSpell = new ArchSpell(new Scroll());
    private Resources resources = new Resources() {
        public Reader open(String file) throws IOException {
            return new FileReader(file);
        }
    };
    private MagicReader magic = new MagicReader(null);

    /**
     * Debug Log Level
     */
    public static volatile int dlogLevel = 0;

    public static final int DLOG_LEVEL_0 = 0;
    public static final int DLOG_LEVEL_1 = 1;
    public static final int DLOG_LEVEL_2 = 2;
    public static final int DLOG_LEVEL_3 = 3;

    public static final int DLOG_OFF_LEVEL = DLOG_LEVEL_0;
    public static final int DLOG_MAX_LEVEL = DLOG_LEVEL_3;

    protected Wandler(XMLReader xmlReader) {
        rootSpell.setParent(this);
        try {
            parser = xmlReader != null ? xmlReader : getXHTMLParser();
            parser.setContentHandler(this);
            parser.setEntityResolver(new VoidResolver());
        } catch (SAXException ex) {
            throw new RuntimeException("Could not create Wandler", ex);
        }
    }

    /**Creates a Wandler which uses an XMLReader from XMLReaderFactory.
     *
     * Calling this is equivalent to 'new Wandler(XMLReaderFactory.createXMLReader())'.
     */
    protected Wandler() {
        this(null);
    }

    /**
     * Wandler for XHTML. Notice: No DTD support, meaning entity references are taboo.
     * Use only character references instead.
     *
     * @return A new Wandler used for processing XHTML input.
     */
    public static Wandler forXHTML() {
        Wandler wandler = new Wandler();
        wandler.setXHTML(true);

        return wandler;
    }

    /**
     * Wandler for HTML5.
     *
     * @return A new Wandler used for processing HTML input.
     */
    public static Wandler forHTML() {
        Wandler wandler = new Wandler(getHTMLParser());
        wandler.setHideEntitiesAndCharacterReferencesFromSax(true);

        return wandler;
    }

    public static XMLReader getXHTMLParser() throws SAXException {
        return XMLReaderFactory.createXMLReader();
    }

    public static XMLReader getHTMLParser() {
        return new HtmlParser(XmlViolationPolicy.ALLOW);
    }

    public XMLReader getParser() {
        return parser;
    }

    public void reset() {
        preserve = false;
        startUnderConstruction = false;
        calls = 0;
        lastStart = -1;
        rootSpell.reset();
    }

    @Override
    public Spell clone() {
        throw new UnsupportedOperationException("Sorry, but this won't work.");
    }

    /**Sets the resources of this Wandler.
     * Resources may be accessed by Spells, such as
     * the Inclusion to be able to include files.
     *
     * @param resources Resources for this Wandler.
     */
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    /**Gets the resources of this Wandler.
     * Resources may be accessed by Spells, such as
     * the Inclusion to be able to include files.
     *
     * @return This Wandler's resources.
     */
    public Resources getResources() {
        return resources;
    }

    public Wandler getWandler() {
        return this;
    }

    public boolean hierarchyContains(Spell spell) {
        return spell == this; // Wandler is the end of the hierarchy
    }

    protected boolean preserveSpace(String localName) {
        return !emptyElements.contains(localName);
    }

    public void wandle(Reader in, Writer out) {
        try {
            reset();
            InputSource src = isHideEntitiesAndCharacterReferencesFromSax() ?
                    new InputSource(new MagicReader(in)) : new InputSource(in);
            this.out = new BufferedWriter(out, 2048);
            parser.parse(src);
        } catch (IOException ex) {
            Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, "IOException", ex);
        } catch (SAXException ex) {
            int lineNumber = locator != null ? locator.getLineNumber() : -1;
            String message = "Error parsing input" +
                (lineNumber != -1 ? " at line " + lineNumber : "");
            Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, message, ex);
        } finally {
            if (this.out != null) {
                try {
                    this.out.close();
                } catch (IOException ex) {
                    Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (dlogLevel >= DLOG_LEVEL_1) {
                rootSpell.getScroll().checkUsed(true);
            }
        }
    }

    public void startElement(String name, Attribute... attributes) {
        startElement(name, new SimpleAttributes(attributes));
    }

    public void startElement(String name, org.xml.sax.Attributes attributes) {
        ++calls;
        if (startUnderConstruction) {
            write(">");
        }
        openElement(name, attributes);
        startUnderConstruction = true;
        lastStart = calls;
    }

    public void endElement(String name) {
        ++calls;
        if (startUnderConstruction && noNestedElement() && !preserve) {
            write("/>");
        } else {
            if (startUnderConstruction) {
                write(">");
            }
            write("</" + name + ">");
        }
        startUnderConstruction = false;
    }

    public void writeCharacters(char[] characters, int offset, int length, boolean safe) {
        if (startUnderConstruction) {
            write(">");
            startUnderConstruction = false;
        }
        if (safe) {
            write(characters, offset, length);
        } else {
            writeSanitized(characters, offset, length);
        }
    }

    protected void writeSanitized(char[] chars, int offset, int length) {
        int from = offset;
        int end = offset + length;
        for (int i = offset; i < end; ++i) {
            char ch = chars[i];
            String sanitized = sanitize(ch);
            if (sanitized != null) {
                write(chars, from, i - from);
                from = i + 1;
                write(sanitized);
            }
        }
        if (from < end) {
            write(chars, from, end - from);
        }
    }

    protected String sanitize(char ch) {
        // this will be called *pretty* often, so micro-optimization to bypass the lookupswitch is justifiable IMO
        if (ch > '>' || ch < '"') return null;
        switch (ch) {
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '&': return "&amp;";
            case '"': return "&quot;";
            case '\'': return "&apos;";
            default: return null;
        }
    }

    public void writeString(String string, boolean safe) {
        char[] characters = string.toCharArray();
        writeCharacters(characters, 0, characters.length, safe);
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
        try {
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes atts)
            throws SAXException {
        try {
            rootSpell.startElement(localName, new DisenchantedAttributes(atts));
        } catch (Exception e) {
            throw new SAXException("Could not start " + stringFor(localName, atts), e);
        }
    }

    protected String stringFor(String label, org.xml.sax.Attributes attr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<"); sb.append(label);
        for (int i = 0; i < attr.getLength(); ++i) {
            sb.append(" "); sb.append(attr.getLocalName(i));
            sb.append("=\""); sb.append(attr.getValue(i)); sb.append("\"");
        }
        sb.append(">");
        return sb.toString();
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            rootSpell.endElement(localName);
        } catch (Exception e) {
            throw new SAXException("Could not end " + stringFor(localName, new SimpleAttributes()));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (isHideEntitiesAndCharacterReferencesFromSax()) {
                magic.showAllIn(ch, start, length);
            }
            rootSpell.writeCharacters(ch, start, length, true);
        } catch (Exception e) {
            throw new SAXException("Could not write \"" + new String(ch, start, length) + "\"", e);
        }
    }

    private boolean noNestedElement() {
        return (calls - lastStart) == 1;
    }

    protected void openElement(String name, org.xml.sax.Attributes atts) {
        write("<");
        write(name);
        if (atts != null) {
            for (int i = 0; i < atts.getLength(); ++i) {
                write(" ");
                write(atts.getLocalName(i));
                write("=\"");
                write(atts.getValue(i));
                write("\"");
            }
        }
        preserve = preserveSpace(name);
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (!startUnderConstruction) {
            write(new String(ch, start, length));
        }
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public final void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public final void endPrefixMapping(String prefix) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
        write("&");
        write(name);
        write(";");
    }

    protected final void write(String s) {
        try {
            out.write(s);
        } catch (IOException ex) {
            throw new RuntimeException("Write failed", ex);
        }
    }

    protected final void write(char[] characters, int offset, int length) {
        try {
            out.write(characters, offset, length);
        } catch (IOException ex) {
            throw new RuntimeException("Write failed", ex);
        }
    }

    public void setParent(Spell transformation) {
    }

    public Spell getParent() {
        return null;
    }

    public void startTransformedElement(String name, org.xml.sax.Attributes attributes) {
        startElement(name, attributes);
    }

    public void endTransformedElement(String name) {
        endElement(name);
    }

    public Scroll usedScroll() {
        return rootSpell.getScroll();
    }

    public void useScroll(Scroll scroll) {
        rootSpell.setScroll(scroll);
    }

    public void ignoreBounds(boolean ignoreBounds) {
        // does not matter here
    }

    public boolean ignoreBounds() {
        return false;
    }

    public void setHideEntitiesAndCharacterReferencesFromSax(boolean hide) {
        this.hideEntitiesAndCharacterReferencesFromSax = hide;
    }

    public boolean isHideEntitiesAndCharacterReferencesFromSax() {
        return hideEntitiesAndCharacterReferencesFromSax;
    }

    protected void setXHTML(boolean xhtml) {
        this.xhtml = xhtml;
    }

    public boolean isXHTML() {
        return xhtml;
    }
}
