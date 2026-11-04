package com.spacetraffic.monitor.service;

import com.spacetraffic.monitor.entity.Observation;
import com.spacetraffic.monitor.exception.ObservationNotFoundException;
import com.spacetraffic.monitor.repository.ObservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ObservationService {

    private final ObservationRepository observationRepository;

    public ObservationService(ObservationRepository observationRepository) {
        this.observationRepository = observationRepository;
    }

    public List<Observation> getAllObservations() {
        return observationRepository.findAll();
    }

    public Observation getObservationById(UUID id) {
        return observationRepository.findById(id)
                .orElseThrow(() ->
                        new ObservationNotFoundException(id));
    }

    public List<Observation> getObservationsByObjectId(String objectId) {
        return observationRepository.findByObjectId(objectId);
    }

    public List<Observation> getObservationsByTimeRange(Instant start, Instant end) {
        return observationRepository.findByObservedAtBetween(start, end);
    }

    public List<Observation> getObservationsByObjectAndTimeRange(String objectId, Instant start, Instant end) {
        return observationRepository.findByObjectIdAndObservedAtBetween(objectId, start, end);
    }

    @Transactional
    public Observation createObservation(
            String objectId,
            Instant observedAt,
            double positionX,
            double positionY,
            double positionZ
    ) {
        Observation observation = new Observation(
                objectId,
                observedAt,
                positionX,
                positionY,
                positionZ
        );

        return observationRepository.save(observation);
    }
}
