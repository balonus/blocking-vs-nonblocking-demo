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
        final int submissionWorkerThreads = Integer.parseInt(System.getenv("OTA_SUBMISSION_MAX_WORKER_THREADS"));
        final int pollWorkerThreads = Integer.parseInt(System.getenv("OTA_POLL_MAX_WORKER_THREADS"));

        return Arrays.asList(
                createJettyServerForJaxRsApplication(8080, submissionWorkerThreads, new ScriptSubmissionResource()),
                createJettyServerForJaxRsApplication(8081, pollWorkerThreads, new ScriptPollingResource())
        );
    }
}
