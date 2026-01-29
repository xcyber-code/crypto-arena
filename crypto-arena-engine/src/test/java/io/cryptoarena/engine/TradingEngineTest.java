package io.cryptoarena.engine;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TradingEngine.
 */
class TradingEngineTest {

    @Test
    void shouldCreateEngineInstance() {
        TradingEngine engine = new TradingEngine();
        assertThat(engine).isNotNull();
    }

    @Test
    void shouldStartEngine() {
        TradingEngine engine = new TradingEngine();
        engine.start();
        assertThat(engine.isRunning()).isTrue();
    }

    @Test
    void shouldStopEngine() {
        TradingEngine engine = new TradingEngine();
        engine.start();
        engine.stop();
        assertThat(engine.isRunning()).isFalse();
    }
}
