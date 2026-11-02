package com.spacetraffic.monitor.controller;

import com.spacetraffic.monitor.dto.ObservationRequest;
import com.spacetraffic.monitor.dto.ObservationResponse;
import com.spacetraffic.monitor.entity.Observation;
import com.spacetraffic.monitor.service.ObservationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/observations")
@Tag(
        name = "Observations",
        description = "API for managing space object observations"
)
public class ObservationController {

    private final ObservationService observationService;

    public ObservationController(ObservationService observationService) {
        this.observationService = observationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new observation",
            description = "Registers a new observation containing the space object identifier, observation timestamp and position coordinates."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Observation successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid observation data")
    })
    public ObservationResponse createObservation(@Valid @RequestBody ObservationRequest request) {

        Observation observation = observationService.createObservation(
                request.objectId(),
                request.observedAt(),
                request.positionX(),
                request.positionY(),
                request.positionZ()
        );

        return toResponse(observation);
    }

    @GetMapping
    @Operation(
            summary = "Get all observations",
            description = "Returns all observations stored in the system."
    )
    @ApiResponse(responseCode = "200", description = "Observations successfully retrieved")
    public List<ObservationResponse> getAllObservations() {
        return observationService.getAllObservations()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get observation by ID",
            description = "Returns a specific observation using its UUID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Observation successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Observation not found")
    })
    public ObservationResponse getObservationById(
            @Parameter(description = "UUID of the observation", example = "888ccff6-f453-4505-b1ee-a5c0aab8e6d9")
            @PathVariable UUID id) {

        return toResponse(observationService.getObservationById(id));
    }

    @GetMapping("/object/{objectId}")
    @Operation(
            summary = "Get observations by object ID",
            description = "Returns all observations associated with a specific space object."
    )
    @ApiResponse(responseCode = "200", description = "Observations successfully retrieved")
    public List<ObservationResponse> getObservationsByObjectId(
            @Parameter(description = "Identifier of the space object", example = "SAT-001")
            @PathVariable String objectId) {

        return observationService.getObservationsByObjectId(objectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ObservationResponse toResponse(Observation observation) {
        return new ObservationResponse(
                observation.getId(),
                observation.getObjectId(),
                observation.getObservedAt(),
                observation.getPositionX(),
                observation.getPositionY(),
                observation.getPositionZ(),
                observation.getCreatedAt()
        );
    }
}
