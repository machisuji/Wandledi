package org.wandledi;

import java.io.IOException;
import java.io.Reader;

/**Provides access to resources in the system.
 *
 * @author Markus Kahl
 */
public interface Resources {
    /**Opens a Reader to a given resource.
     *
     * @param resource The resource (e.g. file in classpath) to be opened.
     * @throws IOException
     */
    Reader open(String resource) throws IOException;
}
