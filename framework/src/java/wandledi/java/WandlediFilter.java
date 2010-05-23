package wandledi.java;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**The filter which handles all Wandledi requests.
 * Has to be registered in the web.xml in order to make Wandledi work.
 *
 * @author Markus Kahl
 */
public class WandlediFilter implements Filter {

    private FilterConfig filterConfig = null;

    public WandlediFilter() {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        boolean dispatched = false;
        if (request instanceof HttpServletRequest) {
            long ns = System.nanoTime();
            HttpServletRequest r = (HttpServletRequest) request;
            dispatched = Switchboard.getInstance().dispatch(r, (HttpServletResponse) response);
            if (false && dispatched) {
                ns = System.nanoTime() - ns;
                System.out.println("Served " + r.getRequestURI() + " within " +
                        (ns / 1000000) + " ms.");
            }
        }
        if (!dispatched) {
            chain.doFilter(request, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter 
     */
    public void destroy() {

        createBootstrap().destroy();
    }

    /**
     * Init method for this filter 
     */
    public void init(FilterConfig filterConfig) {

        this.filterConfig = filterConfig;
        this.bootstrap(filterConfig.getServletContext());
        Switchboard.getInstance().setServletContext(filterConfig.getServletContext());
    }

    protected void bootstrap(ServletContext servletContext) {

        WandlediBootstrap bootstrap = createBootstrap();
        if (bootstrap != null) {
            bootstrap.init(servletContext);
        } else {
            throw new RuntimeException("Could not instantiate Wandledi bootstrap.");
        }
    }

    protected WandlediBootstrap createBootstrap() {

        try {
            Class<?> clazz = Class.forName("wandledi.config.Bootstrap");
            WandlediBootstrap bootstrap = (WandlediBootstrap) clazz.newInstance();

            return bootstrap;
        } catch (InstantiationException ex) {
            Logger.getLogger(WandlediFilter.class.getName()).log(Level.SEVERE, "Instantiation failed", ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(WandlediFilter.class.getName()).log(Level.SEVERE, "Illegal Access", ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WandlediFilter.class.getName()).log(Level.SEVERE, "Class not found", ex);
        }
        return null;
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("ControllerFilter()");
        }
        StringBuffer sb = new StringBuffer("ControllerFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }
}
