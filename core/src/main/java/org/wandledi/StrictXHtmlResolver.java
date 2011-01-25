package org.wandledi;

import java.io.IOException;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author markus
 */
public class StrictXHtmlResolver implements EntityResolver {

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        InputSource src = null;
        if ("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd".equals(systemId) ||
                "-//W3C//DTD XHTML 1.0 Strict//EN".equals(publicId)) {
            src = getInputSource("wandledi/dtd/xhtml1-strict.dtd");
        } else if ("http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent".equals(systemId) ||
                "-//W3C//ENTITIES Latin 1 for XHTML//EN".equals(publicId)) {
            src = getInputSource("wandledi/dtd/xhtml-lat1.ent");
        } else if ("http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent".equals(systemId) ||
                "-//W3C//ENTITIES Symbols for XHTML//EN".equals(publicId)) {
            src = getInputSource("wandledi/dtd/xhtml-symbol.ent");
        } else if ("http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent".equals(systemId) ||
                "-//W3C//ENTITIES Special for XHTML//EN".equals(publicId)) {
            src = getInputSource("wandledi/dtd/xhtml-special.ent");
        }
        return src;
    }

    public InputSource getInputSource(String file) {

        URL resource = StrictXHtmlResolver.class.getClassLoader().getResource(file);
        return new InputSource(resource.toExternalForm());
    }
}
