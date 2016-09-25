package org.demo.ota.nonblocking;

import org.demo.ota.common.BaseServerApp;
import org.demo.ota.nonblocking.rest.ScriptPollingResource;
import org.demo.ota.nonblocking.rest.ScriptSubmissionResource;
import org.eclipse.jetty.server.Server;

import java.util.Arrays;
import java.util.List;

public class NonblockingBoot extends BaseServerApp {
    public static void main(String[] args) throws Exception {
        new NonblockingBoot().run();
    }

    @Override
    protected String title() {
        return "Non-blocking OTA Gateway";
    }

    @Override
    protected List<Server> createServers() {
        return Arrays.asList(
                createJettyServerForJaxRsApplication(8080, 200, new ScriptSubmissionResource()),
                createJettyServerForJaxRsApplication(8081, 200, new ScriptPollingResource())
        );
    }
}
