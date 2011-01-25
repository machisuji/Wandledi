package org.wandledi;

import java.io.IOException;
import java.io.StringReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Markus Kahl
 */
public class VoidResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        return new InputSource(new StringReader(""));
    }
}
