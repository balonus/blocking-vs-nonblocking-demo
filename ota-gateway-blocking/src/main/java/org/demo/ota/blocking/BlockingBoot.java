package org.demo.ota.blocking;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.demo.ota.blocking.rest.ScriptPollingResource;
import org.demo.ota.blocking.rest.ScriptSubmissionResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.ws.rs.core.Application;

public class BlockingBoot {

    public static void main(String[] args) throws Exception {

        DefaultExports.initialize();
        startServer(28080, new ServletHolder(new MetricsServlet()));

        startRestServer(8080, ScriptSubmissionResource.class);
        startRestServer(8081, ScriptPollingResource.class);
    }

    private static Server startRestServer(int port, Class<? extends Application> restApplicationClass) throws Exception {
        ServletHolder h = new ServletHolder(new HttpServletDispatcher());
        h.setInitParameter("javax.ws.rs.Application", restApplicationClass.getName());
        return startServer(port, h);
    }

    private static Server startServer(int port, ServletHolder sh) throws Exception {

        QueuedThreadPool thrPool = new QueuedThreadPool();
        thrPool.setMaxThreads(1000); // TODO should be parametrized
        final Server server = new Server(thrPool);

        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(sh, "/*");
        server.setHandler(context);

        server.start();
        return server;
    }

}
