package org.demo.ota.common;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseServerApp implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(BaseServerApp.class);

    @Override
    public void run() {
        final List<Server> servers = new ArrayList<>();

        log.info("Starting {}...", title());

        servers.add(createMetricsServer());
        servers.addAll(createServers());

        log.info("Starting {} server(s)...", servers.size());

        for (Server s : servers) {
            try {
                s.start();
            } catch (Exception e) {
                throw new AssertionError("Can't start application", e);
            }
        }

        log.info("All {} server(s) was started successfully", servers.size());
        log.info("{} successfully started", title());

        Runtime.getRuntime().addShutdownHook(new Thread() {

            {
                this.setName("shutdown-hook");
            }

            @Override
            public void run() {

                log.info("Graceful shutdown requested. Stopping {} server(s)...", servers.size());

                for (Server s : servers) {
                    try {
                        s.stop();
                    } catch (Exception e) {
                        throw new AssertionError("Can't gracefully stop application", e);
                    }
                }

                log.info("All {} servers was stopped successfully", servers.size());
            }
        });
    }

    protected abstract String title();

    protected abstract List<Server> createServers();

    protected static Server createJettyServerForJaxRsApplication(int port, int workerThreads, Application jaxRsApplication) {
        final Server server = createJettyServer(port, workerThreads);
        server.setHandler(createJaxRsServletContextHandler(jaxRsApplication));
        return server;
    }

    private static Server createMetricsServer() {
        DefaultExports.initialize();

        final Server server = createJettyServer(28080, 2);
        server.setHandler(createServletContextHandler(new MetricsServlet()));
        return server;
    }

    private static ServletContextHandler createServletContextHandler(Servlet servlet) {
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(servlet), "/*");
        return context;
    }

    private static ServletContextHandler createJaxRsServletContextHandler(Application jaxRsApplication) {
        final ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setApplication(jaxRsApplication);

        final ServletContextHandler context = createServletContextHandler(new HttpServlet30Dispatcher());
        context.setAttribute(ResteasyDeployment.class.getName(), deployment);
        return context;
    }

    private static Server createJettyServer(int port, int workerThreads) {
        assert port > 0;
        assert workerThreads > 0;

        final QueuedThreadPool thrPool = new QueuedThreadPool();
        thrPool.setMaxThreads(workerThreads + 3);
        final Server server = new Server(thrPool);

        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        return server;
    }
}
