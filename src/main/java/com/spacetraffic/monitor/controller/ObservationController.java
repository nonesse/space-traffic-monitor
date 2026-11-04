package com.spacetraffic.monitor.controller;

import com.spacetraffic.monitor.dto.ObservationRequest;
import com.spacetraffic.monitor.dto.ObservationResponse;
import com.spacetraffic.monitor.entity.Observation;
import com.spacetraffic.monitor.service.ObservationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/observations")
@Validated
public class ObservationController {

    private final ObservationService observationService;

    public ObservationController(ObservationService observationService) {
        this.observationService = observationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
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
    public List<ObservationResponse> getAllObservations() {
        return observationService.getAllObservations()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ObservationResponse getObservationById(@PathVariable UUID id) {
        return toResponse(observationService.getObservationById(id));
    }

    @GetMapping("/object/{objectId}")
    public List<ObservationResponse> getObservationsByObjectId(
            @PathVariable @NotBlank @Size(max = 50) String objectId) {

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
