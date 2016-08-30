package org.demo.ota;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.demo.ota.nonblocking.rest.ScriptPollingResource;
import org.demo.ota.nonblocking.rest.ScriptSubmitionResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.ws.rs.core.Application;

public class App 
{
    public static void main(String[] args) throws Exception {

        DefaultExports.initialize();
        startServer(28080, new ServletHolder(new MetricsServlet()));

        startRestServer(8080, ScriptSubmitionResource.class);
        startRestServer(8181, ScriptPollingResource.class);
    }

    private static Server startRestServer(int port, Class<? extends Application> restApplicationClass) throws Exception {
        ServletHolder h = new ServletHolder(new HttpServletDispatcher());
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
