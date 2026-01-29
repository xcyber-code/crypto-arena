package io.cryptoarena.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core trading engine for crypto-arena.
 */
public class TradingEngine {

    private static final Logger log = LoggerFactory.getLogger(TradingEngine.class);

    private volatile boolean running = false;

    /**
     * Starts the trading engine.
     */
    public void start() {
        log.info("Starting trading engine...");
        running = true;
        // Engine startup logic
    }

    /**
     * Stops the trading engine.
     */
    public void stop() {
        log.info("Stopping trading engine...");
        running = false;
        // Engine shutdown logic
    }

    /**
     * Checks if the engine is running.
     * @return true if running
     */
    public boolean isRunning() {
        return running;
    }
}
