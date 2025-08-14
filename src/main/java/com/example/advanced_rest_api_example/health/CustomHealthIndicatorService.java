package com.example.advanced_rest_api_example.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Eigene Health-Checks
 * Falls du z. B. einen Check für einen externen Service brauchst:
 */
@Component
public class CustomHealthIndicatorService implements HealthIndicator {
    @Override
    public Health health() {
        boolean serviceUp = checkExternalService();
        if (serviceUp) {
            return Health.up().withDetail("externalService", "Available").build();
        }
        return Health.down().withDetail("externalService", "Unavailable").build();
    }

    private boolean checkExternalService() {
        // hier deinen Check implementieren
        return true;
    }
}
