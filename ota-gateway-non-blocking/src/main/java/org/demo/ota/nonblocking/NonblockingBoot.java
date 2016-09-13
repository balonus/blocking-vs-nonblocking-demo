package org.demo.ota.nonblocking;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.demo.ota.nonblocking.rest.ScriptPollingResource;
import org.demo.ota.nonblocking.rest.ScriptSubmitionResource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;

import javax.ws.rs.core.Application;

public class NonblockingBoot
{
    public static void main(String[] args) throws Exception {

        DefaultExports.initialize();
        startServer(28080, new ServletHolder(new MetricsServlet()));

        startRestServer(8080, ScriptSubmitionResource.class);
        startRestServer(8081, ScriptPollingResource.class);
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

//    private static NettyJaxrsServer startRestServer(int port, Application restApp) throws Exception {
//        final ResteasyDeployment deployment = new ResteasyDeployment();
//        deployment.setApplication(restApp);
//
//        final NettyJaxrsServer server = new NettyJaxrsServer();
//        server.setDeployment(deployment);
//        server.setHostname("0.0.0.0");
//        server.setPort(port);
//        server.setIoWorkerCount(Runtime.getRuntime().availableProcessors() * 2);
//        server.setSecurityDomain(null);
//
//        server.start();
//
//        return server;
//    }
}
