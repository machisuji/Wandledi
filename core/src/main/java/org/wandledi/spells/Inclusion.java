package org.wandledi.spells;

import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wandledi.Wandler;
import org.wandledi.io.MagicReader;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.wandledi.Scroll;
import org.wandledi.Spell;
import org.wandledi.VoidResolver;

/**
 * With this spell you can include other html files into the current one.
 * Note that the element this spell is apply upon will cease existing.
 * You could say that it is sacrificed for the summoning of the
 * new content.
 *
 * On another note: if the root element of the included file is `html` it will be dropped
 * because it doesn't make much sense to include an html element into another one.
 * The main motivation for this is being able to have (more or less) valid xhtml documents (with a single root node)
 * for html fragments.
 *
 * @author Markus Kahl
 */
public class Inclusion extends ArchSpell implements ContentHandler {

    private XMLReader parser;
    private Locator locator;
    private InclusionIntent intent;
    private MagicReader magic = new MagicReader(null);
    private boolean useMagic;

    public Inclusion(final String file) {
        this(new InclusionIntent() {
            public String getFile() {
                return file;
            }
            public Scroll getScroll() {
                return new Scroll();
            }
        });
    }

    public Inclusion(final String file, final Scroll scroll) {
        this(new InclusionIntent() {
            public String getFile() {
                return file;
            }

            public Scroll getScroll() {
                return scroll;
            }
        });
    }

    public Inclusion(InclusionIntent intent) {
        super(intent.getScroll() != null ? intent.getScroll() : new Scroll());
        this.intent = intent;
        boolean xhtml = intent.getFile().endsWith(".xhtml");
        this.useMagic = !xhtml;
        try {
            parser = xhtml ? Wandler.getXHTMLParser() : Wandler.getHTMLParser();
            parser.setContentHandler(this);
            parser.setEntityResolver(new VoidResolver());
        } catch (SAXException ex) {
            throw new RuntimeException("Inclusion failed", ex);
        }
    }

    @Override
    public Spell clone() {
        return new Inclusion(intent);
    }

    public void startTransformedElement(String name, Attributes attributes) {
        if (ignoreBounds()) return;
        try {
            Reader in = getResources().open(intent.getFile());
            parser.parse(new InputSource(useMagic ? new MagicReader(in) : in));
        } catch (IOException ex) {
            String message = "Error reading " + intent.getFile();
            Logger.getLogger(Inclusion.class.getName()).log(Level.SEVERE, message, ex);
        } catch (SAXException ex) {
            int lineNumber = locator != null ? locator.getLineNumber() : -1;
            String message = "Error parsing " + intent.getFile() +
                (lineNumber != -1 ? " at line " + lineNumber : "");
            Logger.getLogger(Inclusion.class.getName()).log(Level.SEVERE, message, ex);
        } finally {
            if (Wandler.dlogLevel >= Wandler.DLOG_LEVEL_1 && intent.getScroll() != null) {
                intent.getScroll().checkUsed(true);
            }
        }
    }

    public void startElement(String name, Attributes attributes) { }

    public void endElement(String name) { }

    public void writeCharacters(char[] characters, int offset, int length, boolean safe) { }

    public void endTransformedElement(String name) { }

    public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {

        if (!localName.equalsIgnoreCase("html")) {
            super.startElement(localName, atts);
        } // else skip HTML root element
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!localName.equalsIgnoreCase("html")) {
            super.endElement(localName);
        } // else skip HTML root element
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (useMagic) {
            magic.showAllIn(ch, start, length);
        }
        super.writeCharacters(ch, start, length, true);
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public void startDocument() throws SAXException { }

    public void endDocument() throws SAXException { }

    public void startPrefixMapping(String prefix, String uri) throws SAXException { }

    public void endPrefixMapping(String prefix) throws SAXException { }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException { }

    public void processingInstruction(String target, String data) throws SAXException { }

    public void skippedEntity(String name) throws SAXException {
        writeString("&", true);
        writeString(name, true);
        writeString(";", true);
    }

    @Override
    public String toString() {
        return "Inclusion(useMagic: " + useMagic + ", file: " + intent.getFile() + ", intent: " + intent + ")";
    }
}
