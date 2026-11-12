package com.spacetraffic.monitor.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record ObservationCreatedEvent(
        UUID eventId,
        UUID observationId,
        String objectId,
        Instant observedAt
) {}