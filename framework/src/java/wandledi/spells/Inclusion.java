package wandledi.spells;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import wandledi.core.AbstractSpell;
import wandledi.core.VoidResolver;
import wandledi.java.Switchboard;

/**With this spell you can include other html files into the current one.
 * Note that the element this spell is cast upon will cease existing.
 * You could say that it is sacrificed for the summoning of the
 * new content.
 *
 * @author Markus Kahl
 */
public class Inclusion extends AbstractSpell implements ContentHandler {

    private XMLReader parser;
    private InclusionIntent intent;

    public Inclusion(final String file) {

        this(new InclusionIntent() {
            public String getFile() {
                return file;
            }
        });
    }

    public Inclusion(InclusionIntent intent) {

        this.intent = intent;
        try {
            parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(this);
            parser.setEntityResolver(new VoidResolver());
        } catch (SAXException ex) {
            throw new RuntimeException("Inclusion failed", ex);
        }
    }

    public void startTransformedElement(String name, Attributes attributes) {
        
        try {
            Switchboard board = Switchboard.getInstance();
            String file = board.getServletContext().getRealPath(
                    board.getViewDirectory() + intent.getFile());
            parser.parse(new InputSource(new FileReader(intent.getFile())));
        } catch (IOException ex) {
            Logger.getLogger(Inclusion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Inclusion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void endTransformedElement(String name) {

    }

    public void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {

        parent.startElement(localName, atts);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        parent.endElement(localName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {

        parent.writeCharacters(ch, start, length);
    }

    public void setDocumentLocator(Locator locator) { }

    public void startDocument() throws SAXException { }

    public void endDocument() throws SAXException { }

    public void startPrefixMapping(String prefix, String uri) throws SAXException { }

    public void endPrefixMapping(String prefix) throws SAXException { }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException { }

    public void processingInstruction(String target, String data) throws SAXException { }

    public void skippedEntity(String name) throws SAXException { }
}
