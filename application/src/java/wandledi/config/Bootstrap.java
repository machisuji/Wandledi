package wandledi.config;

import javax.servlet.ServletContext;
import wandledi.example.controllers.HomeJsp;
import wandledi.example.controllers.Home;
import wandledi.java.WandlediBootstrap;

/**
 *
 * @author Markus Kahl
 */
public class Bootstrap extends WandlediBootstrap {

    public void init(ServletContext servletContext) {

        setPersistenceUnit("MyPU");
        addControllers(HomeJsp.class, Home.class);
    }

    public void destroy() {
    }
}
