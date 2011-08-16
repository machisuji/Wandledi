package org.wandledi.wandlet;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import javax.servlet.Filter;

/**Runs a Java web application (Servlet API) via embedded Jetty.
 *
 * @author Markus Kahl
 */
public class JettyRunner {

    private int port;
    private String descriptor;
    private String webDirectory;
	private Class<? extends Filter> filter;
	private boolean useNio = true;

	/**Creates a new JettyRunner ready to be started via #start().
	 *
	 * @param port The port the HTTP server is to listen to.
	 * @param descriptor The path to the web.xml to use.
	 * @param webDirectory The path to the web directory containing html/jsp files, WEB-INF, etc..
	 * @param filter The filter for precessing incoming HTTP requests.
	 */
    public JettyRunner(int port, String descriptor, String webDirectory, Class<? extends Filter> filter) {
		this.port = port;
		this.descriptor = descriptor;
		this.webDirectory = webDirectory;
		this.filter = filter;
    }

	/**Creates a new Jetty Runner which expects to find the descriptor in the given
	 * web directory under 'WEB-INF/web.xml'.
	 *
	 * @param port The port the HTTP server is to listen to.
	 * @param webDirectory The path to the web directory containing html/jsp files, WEB-INF, etc..
	 * @param filter The filter for precessing incoming HTTP requests.
	 */
	public JettyRunner(int port, String webDirectory, Class<? extends Filter> filter) {
		this(port, concatPaths(webDirectory, "WEB-INF/web.xml"), webDirectory, filter);
	}

	/**Creates a new Jetty Runner with default values for use with sbt.
	 * That is it expects the web directory to be 'src/main/webapp' and
	 * the descriptor to be 'src/main/webapp/WEB-INF/web.xml'.
	 *
	 * @param port The port the HTTP server is to listen to.
	 * @param filter The filter for precessing incoming HTTP requests.
	 */
	public JettyRunner(int port, Class<? extends Filter> filter) {
		this(port, "src/main/webapp", filter);
	}

	private static String concatPaths(String a, String b) {
		StringBuilder path = new StringBuilder(a.length() + b.length());
		path.append(a);
		if (!a.endsWith("/") && !b.startsWith("/")) {
			path.append("/");
		}
		path.append(b);

		return path.toString();
	}

    public static void main(String[] args) throws ClassNotFoundException {
        String nios = getArgument("nio", args);
        String ports = getArgument("port", args);
        String filter = getArgument("filter", args);
        JettyRunner runner = new JettyRunner(8080, (Class<? extends Filter>) Class.forName(filter));

        if (nios != null) {
            boolean nio = Boolean.valueOf(nios);
            runner.setUseNio(nio);
            if (!nio) {
                System.out.println("Jetty: Using blocking IO instead of NIO");
            }
        }
        if (ports != null) {
            int port = Integer.valueOf(ports);
            runner.setPort(port);
        }

        System.out.println("Jetty: Listen to port " + runner.getPort());
        runner.start(true);
    }

	/**Starts Jetty.
	 *
	 * @param join If true the calling Thread will join the Jetty server. It will return immediately otherwise.
	 */
    public void start(boolean join) {

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.addFilter(filter, "/*", 1);
        context.setResourceBase(getWebDirectory());
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);
        Connector connector = null;
        if (!getUseNio()) {
            connector = (Connector) new SocketConnector();
        } else {
            connector = (Connector) new SelectChannelConnector();
        }
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });
        try {
            server.start();
            if (join) {
				server.join();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**Retrieves arguments from an array of possible values.
	 * An argument is expected to be a string of the form 'name=value'.
	 *
	 * @param name The name of the argument to retrieve.
	 * @param args An array in which to look for the desired argument.
	 */
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

	public void setUseNio(boolean useNio) {
		this.useNio = useNio;
	}

	public boolean getUseNio() {
		return useNio;
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