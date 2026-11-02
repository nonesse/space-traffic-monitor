package com.spacetraffic.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representation of a space object observation")
public record ObservationResponse(

        @Schema(description = "Unique observation identifier")
        UUID id,

        @Schema(description = "Identifier of the observed space object",
                example = "SAT-001")
        String objectId,

        @Schema(description = "Observation timestamp",
                example = "2026-11-01T16:30:00Z")
        Instant observedAt,

        @Schema(description = "Position on the X axis")
        double positionX,

        @Schema(description = "Position on the Y axis")
        double positionY,

        @Schema(description = "Position on the Z axis")
        double positionZ,

        @Schema(description = "Timestamp when the record was created")
        Instant createdAt
) {}