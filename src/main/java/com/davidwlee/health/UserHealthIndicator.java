package com.davidwlee.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class UserHealthIndicator implements HealthIndicator{

	@Override
	public Health health() {
		return Health.down().build();
	}
}
