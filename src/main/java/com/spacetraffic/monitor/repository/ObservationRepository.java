package com.spacetraffic.monitor.repository;

import com.spacetraffic.monitor.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ObservationRepository extends JpaRepository<Observation, UUID> {

    List<Observation> findByObjectId(String objectId);

    List<Observation> findByObservedAtBetween(
            Instant start,
            Instant end
    );

    List<Observation> findByObjectIdAndObservedAtBetween(
            String objectId,
            Instant start,
            Instant end
    );
}