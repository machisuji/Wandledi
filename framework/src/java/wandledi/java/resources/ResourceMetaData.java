package wandledi.java.resources;

import java.util.Collection;

/**
 *
 * @author markus
 */
public class ResourceMetaData {

    protected String[] readNames;
    protected Class[] readTypes;
    protected String[] writeNames;
    protected Class[] writeTypes;
    protected Collection<Options> readOptions;
    protected Collection<Options> writeOptions;
}
