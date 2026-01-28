package io.cryptoarena.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CryptoArenaCommon.
 */
class CryptoArenaCommonTest {

    @Test
    void shouldHaveCorrectVersion() {
        assertThat(CryptoArenaCommon.VERSION).isEqualTo("1.0-SNAPSHOT");
    }

    @Test
    void shouldHaveModuleName() {
        assertThat(CryptoArenaCommon.MODULE_NAME).isNotEmpty();
    }
}
