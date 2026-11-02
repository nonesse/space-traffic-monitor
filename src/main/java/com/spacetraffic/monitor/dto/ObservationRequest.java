
package com.spacetraffic.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record ObservationRequest(

        @NotBlank
        @Size(max = 50)
        String objectId,

        @NotNull
        Instant observedAt,

        double positionX,

        double positionY,

        double positionZ

) {}
