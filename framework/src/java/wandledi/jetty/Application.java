package wandledi.jetty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.AbstractNIOConnector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import wandledi.java.WandlediFilter;

import javax.swing.*;

/**
 * @author Markus Kahl
 */
public class Application {

    private int port = 8080;
    private String descriptor = "src/conf/web.xml";
    private String webDirectory = "web";

    public static void main(String[] args) {

        boolean useDirectBuffers = true;
        String directBuffers = getArgument("useDirectBuffers", args);
        if (directBuffers != null) {
            useDirectBuffers = Boolean.valueOf(directBuffers);
        }
        new Application().start(useDirectBuffers);
    }

    public void start(boolean useDirectBuffers) {

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.addFilter(WandlediFilter.class, "/*", 1);
        context.setResourceBase(getWebDirectory());
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        if (!useDirectBuffers) {
            checkDirectBuffers(server);
        }
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getArgument(String name, String[] args) {

        for (String arg: args) {
            if (arg.startsWith(name)) {
                if (arg.contains("=")) {
                    return arg.substring(arg.indexOf('=') + 1);
                } else {
                    return "";
                }
            }
        }
        return null;
    }

    /**On Windows there is a problem with direct buffers, because of which
     * static files (css, html, etc.) are locked by Jetty and hence cannot
     * be edited while it is running.
     *
     * @param server
     */
    protected void checkDirectBuffers(Server server) {

        Connector[] connectors = server.getConnectors();
        for (Connector connector: connectors) {
            if (connector instanceof AbstractNIOConnector) {
                ((AbstractNIOConnector)connector).setUseDirectBuffers(false);
                System.out.println("Server: Don't use direct buffers.");
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWebDirectory() {
        return webDirectory;
    }

    public void setWebDirectory(String webDirectory) {
        this.webDirectory = webDirectory;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }
}
