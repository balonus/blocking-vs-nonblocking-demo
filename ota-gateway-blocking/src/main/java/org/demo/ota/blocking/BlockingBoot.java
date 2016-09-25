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

        final int submissionWorkerThreads = Integer.parseInt(System.getenv("OTA_SUBMISSION_MAX_WORKER_THREADS"));
        final int pollWorkerThreads = Integer.parseInt(System.getenv("OTA_POLL_MAX_WORKER_THREADS"));

        return Arrays.asList(
                createJettyServerForJaxRsApplication(8080, submissionWorkerThreads, new ScriptSubmissionResource()),
                createJettyServerForJaxRsApplication(8081, pollWorkerThreads, new ScriptPollingResource())
        );
    }
}

