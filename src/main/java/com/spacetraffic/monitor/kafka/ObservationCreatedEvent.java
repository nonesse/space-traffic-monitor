package com.spacetraffic.monitor.kafka;

import java.time.Instant;
import java.util.UUID;

public record ObservationCreatedEvent(
        UUID observationId,
        String objectId,
        Instant observedAt
) {}
