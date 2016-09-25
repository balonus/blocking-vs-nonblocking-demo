package org.demo.ota.blocking;

import org.demo.ota.blocking.rest.ScriptPollingResource;
import org.demo.ota.blocking.rest.ScriptSubmissionResource;
import org.demo.ota.common.BaseServerApp;
import org.eclipse.jetty.server.Server;

import java.util.Arrays;
import java.util.List;

public class BlockingBoot extends BaseServerApp {
    public static void main(String[] args) throws Exception {
        new BlockingBoot().run();
    }

    @Override
    protected String title() {
        return "Blocking OTA Gateway";
    }

    @Override
    protected List<Server> createServers() {
        return Arrays.asList(
                createJettyServerForJaxRsApplication(8080, 1000, new ScriptSubmissionResource()),
                createJettyServerForJaxRsApplication(8081, 1000, new ScriptPollingResource())
        );
    }
}

