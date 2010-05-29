package wandledi.jetty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
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

        boolean nio = true;
        String nios = getArgument("nio", args);
        if (nios != null) {
            nio = Boolean.valueOf(nios);
        }
        new Application().start(nio);
    }

    public void start(boolean nio) {

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.addFilter(WandlediFilter.class, "/*", 1);
        context.setResourceBase(getWebDirectory());
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        if (!nio) {
            System.out.println("Server: Using blocking IO instead of NIO");
            server.setConnectors(new Connector[] { new SocketConnector() });
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
