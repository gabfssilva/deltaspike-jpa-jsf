package com.wehavescience.jetty;

import com.sun.faces.config.ConfigureListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener;
import org.jboss.weld.environment.servlet.Listener;

import java.net.URL;
import java.security.ProtectionDomain;

/**
 * @author Gabriel Francisco  <gabfssilva@gmail.com>
 */
public class ApplicationRunner {
    private static Server server;

    private ApplicationRunner() {
    }

    public static void main(String[] args) {
        run(ApplicationRunner.class, "/deltaspike-cdi-jpa", 8080, true);
    }

    public static void run(Class<?> clazz, String applicationRoot, int port, boolean join) {
        try {
            if(isRunning()){
                throw new IllegalStateException("Server already running!");
            }

            server = new Server(port);

            ProtectionDomain domain = clazz.getProtectionDomain();
            URL location = domain.getCodeSource().getLocation();

            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath(applicationRoot);
            webAppContext.setWar(location.toExternalForm());

            webAppContext.addEventListener(new ConfigureListener());
            webAppContext.addEventListener(new BeanManagerResourceBindingListener());
            webAppContext.addEventListener(new Listener());

            server.setHandler(webAppContext);

            server.start();

            if (join) {
                server.join();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isRunning() {
        return server != null && server.isRunning();
    }

    public static void stop() {
        if (!isRunning()) {
            throw new IllegalStateException("Server not running...");
        }

        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
