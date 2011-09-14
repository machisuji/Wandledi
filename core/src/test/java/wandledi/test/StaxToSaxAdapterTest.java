package wandledi.test;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wandledi.Wandler;
import org.wandledi.stax.StaxToSaxAdapter;

import static org.testng.Assert.*;

import java.io.*;

/**
 * Tests the StaxToSaxAdapter's ability to enable SAX-based code to process XML parsed by a StAX parser.
 *
 * @author Markus Kahl
 * @version: 1.0
 * 
 * 09.09.11
 */
public class StaxToSaxAdapterTest {

    @Test
    public void testParseXHTML() throws IOException {
        StaxToSaxAdapter parser = new StaxToSaxAdapter();
        Wandler wandler = new Wandler(parser);
        Reader in = new InputStreamReader(new FileInputStream(SpellExperiment.DIR + "entities.xhtml"), "UTF-8");
        Writer out = new StringWriter();

        wandler.wandle(in, out);

        System.out.println(out);

        assertTrue(true, "success");
    }
}
