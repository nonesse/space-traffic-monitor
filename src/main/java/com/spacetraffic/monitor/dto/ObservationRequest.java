package com.spacetraffic.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.Instant;

@Schema(description = "Data required to register a space object observation")
public record ObservationRequest(

        @Schema(description = "Identifier of the observed space object",
                example = "SAT-001")
        @NotBlank
        @Size(max = 50)
        String objectId,

        @Schema(description = "Date and time when the observation was made",
                example = "2026-11-01T16:30:00Z")
        @NotNull
        Instant observedAt,

        @Schema(description = "Position on the X axis in kilometers",
                example = "6871.2")
        double positionX,

        @Schema(description = "Position on the Y axis in kilometers",
                example = "120.5")
        double positionY,

        @Schema(description = "Position on the Z axis in kilometers",
                example = "320.8")
        double positionZ
) {}