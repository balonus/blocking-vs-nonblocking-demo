package org.demo.secure.module;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;

import javax.ws.rs.core.Application;

public class SecureModuleBoot {

    public static void main(String[] args) throws Exception {

        DefaultExports.initialize();
        startServer(27070, new ServletHolder(new MetricsServlet()));

        startRestServer(7070, SecureModuleResource.class);
    }

    private static Server startRestServer(int port, Class<? extends Application> restApplicationClass) throws Exception {
        ServletHolder h = new ServletHolder(new HttpServlet30Dispatcher());
        h.setInitParameter("javax.ws.rs.Application", restApplicationClass.getName());
        return startServer(port, h);
    }

    private static Server startServer(int port, ServletHolder sh) throws Exception {
        final Server server = new Server(port);
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");
        server.setHandler(context);
        server.start();
        return server;
    }

}
