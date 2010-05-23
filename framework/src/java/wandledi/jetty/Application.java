package wandledi.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import wandledi.java.WandlediFilter;

/**
 * @author Markus Kahl
 */
public class Application {

    private int port = 8080;
    private String descriptor = "src/conf/web.xml";
    private String webDirectory = "web";

    public static void main(String[] args) {

        new Application().start();
    }

    public void start() {

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.addFilter(WandlediFilter.class, "/*", 1);
        //context.setDescriptor(getDescriptor());
        context.setResourceBase(getWebDirectory());
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
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
