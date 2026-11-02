
package com.spacetraffic.monitor.dto;

import java.time.Instant;
import java.util.UUID;

public record ObservationResponse(
        UUID id,
        String objectId,
        Instant observedAt,
        double positionX,
        double positionY,
        double positionZ,
        Instant createdAt
) {}
