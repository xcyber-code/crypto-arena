package io.cryptoarena.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service registry for crypto-arena components.
 * Thread-safe implementation using ConcurrentHashMap.
 */
public class ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

    private final Map<String, Object> services = new ConcurrentHashMap<>();

    /**
     * Registers a service with the given name.
     * @param name the service name
     * @param service the service instance
     * @param <T> the service type
     */
    public <T> void register(String name, T service) {
        log.info("Registering service: {}", name);
        services.put(name, service);
    }

    /**
     * Retrieves a service by name.
     * @param name the service name
     * @param type the expected service type
     * @param <T> the service type
     * @return an Optional containing the service if found and of correct type
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String name, Class<T> type) {
        Object service = services.get(name);
        if (service != null && type.isInstance(service)) {
            return Optional.of((T) service);
        }
        return Optional.empty();
    }

    /**
     * Unregisters a service by name.
     * @param name the service name
     * @return true if the service was removed
     */
    public boolean unregister(String name) {
        log.info("Unregistering service: {}", name);
        return services.remove(name) != null;
    }

    /**
     * Checks if a service is registered.
     * @param name the service name
     * @return true if registered
     */
    public boolean isRegistered(String name) {
        return services.containsKey(name);
    }

    /**
     * Gets the number of registered services.
     * @return the count of services
     */
    public int size() {
        return services.size();
    }

    /**
     * Clears all registered services.
     */
    public void clear() {
        log.info("Clearing all services");
        services.clear();
    }
}
