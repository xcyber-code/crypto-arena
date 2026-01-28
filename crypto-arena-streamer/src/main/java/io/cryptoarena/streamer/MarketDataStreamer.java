package io.cryptoarena.streamer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

/**
 * Real-time market data streamer using Java Flow API.
 */
public class MarketDataStreamer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(MarketDataStreamer.class);

    private final SubmissionPublisher<MarketData> publisher;
    private volatile boolean streaming = false;

    public MarketDataStreamer() {
        this.publisher = new SubmissionPublisher<>();
    }

    /**
     * Subscribes to market data updates.
     * @param subscriber the subscriber to receive market data
     */
    public void subscribe(Flow.Subscriber<? super MarketData> subscriber) {
        publisher.subscribe(subscriber);
    }

    /**
     * Starts streaming market data.
     */
    public void startStreaming() {
        log.info("Starting market data streaming...");
        streaming = true;
    }

    /**
     * Stops streaming market data.
     */
    public void stopStreaming() {
        log.info("Stopping market data streaming...");
        streaming = false;
    }

    /**
     * Publishes market data to all subscribers.
     * @param data the market data to publish
     */
    public void publish(MarketData data) {
        if (streaming) {
            publisher.submit(data);
        }
    }

    /**
     * Checks if streaming is active.
     * @return true if streaming
     */
    public boolean isStreaming() {
        return streaming;
    }

    @Override
    public void close() {
        stopStreaming();
        publisher.close();
    }

    /**
     * Market data record.
     */
    public record MarketData(
        String symbol,
        double price,
        double volume,
        long timestamp
    ) {}
}
