package io.cryptoarena.streamer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MarketDataStreamer.
 */
class MarketDataStreamerTest {

    @Test
    void shouldCreateStreamerInstance() {
        try (MarketDataStreamer streamer = new MarketDataStreamer()) {
            assertThat(streamer).isNotNull();
        }
    }

    @Test
    void shouldStartStreaming() {
        try (MarketDataStreamer streamer = new MarketDataStreamer()) {
            streamer.startStreaming();
            assertThat(streamer.isStreaming()).isTrue();
        }
    }

    @Test
    void shouldStopStreaming() {
        try (MarketDataStreamer streamer = new MarketDataStreamer()) {
            streamer.startStreaming();
            streamer.stopStreaming();
            assertThat(streamer.isStreaming()).isFalse();
        }
    }

    @Test
    void shouldCreateMarketData() {
        var data = new MarketDataStreamer.MarketData("BTC/USD", 50000.0, 1.5, System.currentTimeMillis());
        assertThat(data.symbol()).isEqualTo("BTC/USD");
        assertThat(data.price()).isEqualTo(50000.0);
    }
}
