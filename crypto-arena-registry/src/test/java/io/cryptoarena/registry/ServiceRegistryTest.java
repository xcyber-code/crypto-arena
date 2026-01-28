package io.cryptoarena.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ServiceRegistry.
 */
class ServiceRegistryTest {

    private ServiceRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ServiceRegistry();
    }

    @Test
    void shouldRegisterAndRetrieveService() {
        String service = "TestService";
        registry.register("test", service);

        var result = registry.get("test", String.class);

        assertThat(result).isPresent().contains("TestService");
    }

    @Test
    void shouldReturnEmptyForNonExistentService() {
        var result = registry.get("nonexistent", String.class);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldCheckIfServiceIsRegistered() {
        registry.register("myService", new Object());

        assertThat(registry.isRegistered("myService")).isTrue();
        assertThat(registry.isRegistered("unknown")).isFalse();
    }

    @Test
    void shouldUnregisterService() {
        registry.register("toRemove", "value");

        boolean removed = registry.unregister("toRemove");

        assertThat(removed).isTrue();
        assertThat(registry.isRegistered("toRemove")).isFalse();
    }

    @Test
    void shouldReturnCorrectSize() {
        assertThat(registry.size()).isZero();

        registry.register("service1", "v1");
        registry.register("service2", "v2");

        assertThat(registry.size()).isEqualTo(2);
    }

    @Test
    void shouldClearAllServices() {
        registry.register("s1", "v1");
        registry.register("s2", "v2");

        registry.clear();

        assertThat(registry.size()).isZero();
    }
}
