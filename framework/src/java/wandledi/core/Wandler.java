package wandledi.core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author Markus Kahl
 */
public class Wandler implements ContentHandler, Spell {

    private XMLReader parser;
    private BufferedWriter out;
    private Locator locator;
    private boolean preserve;
    private boolean startUnderConstruction = false;
    private long calls = 0;
    private long lastStart = -1;
    private Scroll scroll = new Scroll();
    private LinkedList<SpellLevel> spellLevels = new LinkedList<SpellLevel>();
    private final static Map<String, Boolean> preserveMap = new HashMap<String, Boolean>(1);

    static {
        preserveMap.put("script", true);
    }

    public Wandler() {

        try {
            parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(this);
            parser.setEntityResolver(new VoidResolver());
        } catch (SAXException ex) {
            throw new RuntimeException("Could not create Wandler", ex);
        }
    }

    public void reset() {

        preserve = false;
        startUnderConstruction = false;
        calls = 0;
        lastStart = -1;
        spellLevels.clear();
    }

    @Override
    public Spell clone() {

        throw new UnsupportedOperationException("Sorry, but this won't work.");
    }

    public boolean hierarchyContains(Spell spell) {

        return spell == this; // Wandler is the end of the hierarchy
    }

    protected boolean preserveSpace(String localName) {

        return preserveMap.containsKey(localName);
    }

    public void wandle(Reader in, Writer out) {

        try {
            reset();
            this.out = new BufferedWriter(out, 2048);
            parser.parse(new InputSource(in));
        } catch (IOException ex) {
            Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, "IOException", ex);
        } catch (SAXException ex) {
            Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, "SAXException", ex);
        } finally {
            if (this.out != null) {
                try {
                    this.out.close();
                } catch (IOException ex) {
                    Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void startElement(String name, Attribute... attributes) {

        startElement(name, new SimpleAttributes(attributes));
    }

    public void startElement(String name, Attributes attributes) {

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

    public void writeCharacters(char[] characters, int offset, int length) {

        if (startUnderConstruction) {
            write(">");
            startUnderConstruction = false;
        }
        write(characters, offset, length);
    }

    public void writeString(String string) {

        char[] characters = string.toCharArray();
        writeCharacters(characters, 0, characters.length);
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

    private void checkSpell(String label, Attributes attributes) {

        List<Spell> spells = usedScroll().readSpellsFor(label, attributes);
        Spell parent = this;
        if (spellLevels.size() > 0) {
            parent = spellLevels.getLast().spell;
        }
        Iterator<Spell> i = spells.iterator();
        while (i.hasNext()) {
            Spell spell = copyIfNested(i.next());
            spell.setParent(parent);
            parent = spell;
            if (!i.hasNext()) {
                spellLevels.add(new SpellLevel(spell));
            }
        }
    }

    private void checkSingleSpell(String label, Attributes attributes) {

        List<Spell> spells = usedScroll().readSpellsFor(label, attributes);
        if (spells.size() > 0) {
            Spell spell = copyIfNested(spells.get(0));
            Spell parent = this;
            if (spellLevels.size() > 0) {
                parent = spellLevels.getLast().spell;
            }
            spell.setParent(parent);
            spellLevels.add(new SpellLevel(spell));
        }
    }

    /**If the very same spell is applied to nested elements we
     * need to clone the spell for any further appliance to prevent
     * an infinite loop.
     *
     * @param spell
     * @return
     */
    private Spell copyIfNested(Spell spell) {

        if (spellLevels.size() > 0 && spellLevels.getLast().spell.hierarchyContains(spell)) {
            return spell.clone();
        }
        return spell;
    }

    public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {

        checkSpell(localName, atts);
        if (spellLevels.size() == 0) {
            startElement(localName, atts);
        } else {
            SpellLevel level = spellLevels.getLast();
            ++level.tagLevel;
            if (level.tagLevel > 1) {
                level.spell.startElement(localName, atts);
            } else {
                level.spell.startTransformedElement(localName, atts);
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (spellLevels.size() == 0) {
            endElement(localName);
        } else {
            SpellLevel level = spellLevels.getLast();
            --level.tagLevel;
            if (level.tagLevel > 0) {
                level.spell.endElement(localName);
            } else {
                level.spell.endTransformedElement(localName);
                spellLevels.removeLast();
            }
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {

        if (spellLevels.size() == 0) {
            writeCharacters(ch, start, length);
        } else {
            SpellLevel level = spellLevels.getLast();
            level.spell.writeCharacters(ch, start, length);
        }
    }

    private boolean noNestedElement() {
        return (calls - lastStart) == 1;
    }

    protected void openElement(String name, Attributes atts) {

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
    }

    protected final void write(String s) {

        try {
            out.write(s);
        } catch (IOException ex) {
            Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected final void write(char[] characters, int offset, int length) {

        try {
            out.write(characters, offset, length);
        } catch (IOException ex) {
            Logger.getLogger(Wandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setParent(Spell transformation) {
    }

    public void startTransformedElement(String name, Attributes attributes) {
        startElement(name, attributes);
    }

    public void endTransformedElement(String name) {
        endElement(name);
    }

    public Scroll usedScroll() {
        return scroll;
    }

    public void useScroll(Scroll grimoire) {
        this.scroll = grimoire;
    }
}
