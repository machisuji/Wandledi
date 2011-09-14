package org.wandledi.stax;

import org.wandledi.Attribute;
import org.wandledi.SimpleAttributes;
import org.xml.sax.*;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to process XML using the SAX API while the XML is actually parsed using a StAX parser.
 * In other words it wraps a pull parser and makes it usable by code working with a push parser.
 *
 * @author Markus Kahl
 * @version: 0.1
 *
 * 09.09.11
 */
public class StaxToSaxAdapter implements XMLReader {

    private ContentHandler contentHandler;

    public StaxToSaxAdapter() {
        
    }

    public void parseXML(Reader source) throws IOException, SAXException {
        XMLInputFactory input = getXMLInputFactory();
        XMLEventReader reader = null;
        long ms = System.currentTimeMillis();
        try {
            reader = input.createXMLEventReader(source);
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    startElement(event.asStartElement(), this.contentHandler);
                } else if (event.isEndElement()) {
                    endElement(event.asEndElement(), this.contentHandler);
                } else if (event.isCharacters()) {
                    characters(event.asCharacters(), this.contentHandler);
                }
                if (event.getEventType() == XMLStreamConstants.DTD) {
                    DTD dtd = (DTD) event;
                    StringWriter sw = new StringWriter();
                    dtd.writeAsEncodedUnicode(sw);
                    System.out.println("DTD: " + sw.toString());
                } else if (event.getEventType() == XMLStreamConstants.START_DOCUMENT) {
                    StartDocument sd = (StartDocument) event;
                } else if (event.getEventType() == XMLStreamConstants.ENTITY_DECLARATION) {
                    System.out.println("EDECL");
                } else if (event.getEventType() == XMLStreamConstants.ENTITY_REFERENCE) {
                    System.out.println("EREF");
                }
            }
        } catch (XMLStreamException e) {
            System.out.println(e.getMessage());
            throw new SAXException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException e) {
                    throw new SAXException("Error closing XMLStreamReader", e);
                }
            }
            source.close();
            ms = System.currentTimeMillis() - ms;
            System.out.println("took " + ms + " ms");
        }
    }

    protected void startElement(StartElement start, ContentHandler ch) throws SAXException {
        List<Attribute> attributes = new LinkedList<Attribute>();
        Iterator<javax.xml.stream.events.Attribute> ai = start.getAttributes();
        while (ai.hasNext()) {
            javax.xml.stream.events.Attribute attr = ai.next();
            attributes.add(new Attribute(attr.getName().getLocalPart(), attr.getValue()));
        }
        SimpleAttributes sa = new SimpleAttributes(attributes.toArray(new Attribute[attributes.size()]));
        ch.startElement(first(start.getNamespaces()), start.getName().getLocalPart(), "", sa);
    }

    protected String first(Iterator<Namespace> nss) {
        if (nss.hasNext()) {
            return nss.next().getNamespaceURI();
        } else {
            return null;
        }
    }

    protected void endElement(EndElement end, ContentHandler ch) throws SAXException {
        ch.endElement(first(end.getNamespaces()), end.getName().getLocalPart(), "");
    }

    protected void characters(Characters characters, ContentHandler ch) throws SAXException {
        char[] chars = characters.getData().toCharArray();
        int offset = 0;
        boolean isEntity = false;
        List<Integer> lengths = parseEntities(chars, offset, chars.length);
        System.out.println("text: " + new String(chars, offset, chars.length));
        System.out.println("lengths: " + lengths);
        for (int length: lengths) {
            if (isEntity) {
                ch.skippedEntity(new String(chars, offset + 1, length - 2));
                System.out.println("Skip " + new String(chars, offset + 1, length - 2));
            } else {
                ch.characters(chars, offset, length);
                System.out.println("Write \"" + new String(chars, offset, length) + "\"");
            }
            offset = offset + length;
            isEntity = !isEntity;
        }
    }

    protected List<Integer> parseEntities(char[] characters, int offset, int length) {
        List<Integer> lengths = new LinkedList<Integer>();
        boolean entityStarted = false;
        int lastIndex = offset;
        int end = offset + length;
        for (int i = offset; i < end; ++i) {
            char ch = characters[i];
            if (!entityStarted && ch == '&') {
                lengths.add(i - lastIndex);
                lastIndex = i;
                entityStarted = true;
            } else if (entityStarted && ch == ';') {
                lengths.add(i - lastIndex + 1);
                lastIndex = i + 1;
                entityStarted = false;
            }
        }
        lengths.add(end - lastIndex);
        return lengths;
    }

    public void parse(InputSource source) throws IOException, SAXException {
        System.out.println("Encoding: " + source.getEncoding());
        parseXML(source.getCharacterStream());
    }

    public void parse(String s) throws IOException, SAXException {
        parseXML(new StringReader(s));
    }

    protected XMLInputFactory getXMLInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, true);
        factory.setProperty(XMLInputFactory.RESOLVER, new XMLResolver() {
            public Object resolveEntity(String s, String s1, String s2, String s3) throws XMLStreamException {
                System.out.println("Resolve " + s + ", " + s1 + ", " + s2 + ", " + s3);
                return null;
            }
        });

        return factory;
    }

    public boolean getFeature(String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException("This Parser currently supports no features.");
    }

    public void setFeature(String s, boolean b) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException("This Parser currently supports no features.");
    }

    public Object getProperty(String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException("This Parser currently supports no properties.");
    }

    public void setProperty(String s, Object o) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotSupportedException("This Parser currently supports no properties.");
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public EntityResolver getEntityResolver() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public DTDHandler getDTDHandler() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ErrorHandler getErrorHandler() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
