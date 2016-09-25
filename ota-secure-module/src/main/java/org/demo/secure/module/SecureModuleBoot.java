package org.demo.secure.module;

import org.demo.ota.common.BaseServerApp;
import org.eclipse.jetty.server.Server;

import java.util.Collections;
import java.util.List;

public class SecureModuleBoot extends BaseServerApp {
    public static void main(String[] args) throws Exception {
        new SecureModuleBoot().run();
    }

    @Override
    protected String title() {
        return "Secure Module";
    }

    @Override
    protected List<Server> createServers() {

        final int workerThreads = Integer.parseInt(System.getenv("SM_MAX_WORKER_THREADS"));

        return Collections.singletonList(
                createJettyServerForJaxRsApplication(
                        8080,
                        workerThreads,
                        new SecureModuleResource())
        );
    }
}
