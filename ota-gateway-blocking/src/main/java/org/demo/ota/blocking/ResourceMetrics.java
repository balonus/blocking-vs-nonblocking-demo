package org.demo.ota.blocking;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

import java.util.function.Supplier;

public final class ResourceMetrics {

    private final Histogram latency;
    private final Counter receivedTotal;
    private final Counter processedTotal;

    public ResourceMetrics(String namespace) {
        latency = Histogram
                .build()
                .namespace(namespace)
                .name("request_processed_latency_seconds")
                .help("Request latency")
                .register();

        receivedTotal = Counter.build()
                .namespace(namespace)
                .name("requests_received_total")
                .help("Received requests total")
                .register();

        processedTotal = Counter.build()
                .namespace(namespace)
                .name("request_processed_total")
                .help("Serviced requests total")
                .labelNames("status")
                .register();
    }

    public final <T> T instrument(Supplier<T> supplier) {
        final Histogram.Timer timer = latency.startTimer();

        try {
            receivedTotal.inc();

            final T result = supplier.get();

            processedTotal.labels("success").inc();
            return result;
        } catch (Exception e) {
            processedTotal.labels("failure").inc();
            throw e;
        } finally {
            timer.observeDuration();
        }
    }
}
