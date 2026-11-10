package com.spacetraffic.monitor.service;

import com.spacetraffic.monitor.entity.Observation;
import com.spacetraffic.monitor.kafka.ObservationCreatedEvent;
import com.spacetraffic.monitor.kafka.ObservationProducer;
import com.spacetraffic.monitor.repository.ObservationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservationServiceTest {

    @Mock
    private ObservationRepository observationRepository;

    @Mock
    private ObservationProducer observationProducer;

    @InjectMocks
    private ObservationService observationService;

    private Observation observation;

    private final String SAT_001 = "SAT-001";

    @BeforeEach
    void setUp() {
        observation = new Observation(
                SAT_001,
                Instant.parse("2026-11-01T16:30:00Z"),
                6871.2,
                120.5,
                320.8
        );
    }

    @Test
    void shouldReturnAllObservations() {
        when(observationRepository.findAll())
                .thenReturn(List.of(observation));

        List<Observation> result = observationService.getAllObservations();

        assertEquals(1, result.size());
        assertEquals(SAT_001, result.getFirst().getObjectId());

        verify(observationRepository).findAll();
        verifyNoInteractions(observationProducer);
    }

    @Test
    void shouldReturnObservationById() {
        UUID id = UUID.randomUUID();

        when(observationRepository.findById(id))
                .thenReturn(Optional.of(observation));

        Observation result = observationService.getObservationById(id);

        assertNotNull(result);
        assertEquals(SAT_001, result.getObjectId());

        verify(observationRepository).findById(id);
        verifyNoInteractions(observationProducer);
    }

    @Test
    void shouldReturnObservationsByObjectId() {
        when(observationRepository.findByObjectId(SAT_001))
                .thenReturn(List.of(observation));

        List<Observation> result =
                observationService.getObservationsByObjectId(SAT_001);

        assertEquals(1, result.size());
        assertEquals(SAT_001, result.getFirst().getObjectId());

        verify(observationRepository).findByObjectId(SAT_001);
        verifyNoInteractions(observationProducer);
    }

    @Test
    void shouldReturnObservationsByTimeRange() {
        Instant start = Instant.parse("2026-11-01T00:00:00Z");
        Instant end = Instant.parse("2026-11-02T00:00:00Z");

        when(observationRepository.findByObservedAtBetween(start, end))
                .thenReturn(List.of(observation));

        List<Observation> result =
                observationService.getObservationsByTimeRange(start, end);

        assertEquals(1, result.size());

        verify(observationRepository)
                .findByObservedAtBetween(start, end);
        verifyNoInteractions(observationProducer);
    }

    @Test
    void shouldReturnObservationsByObjectIdAndTimeRange() {
        Instant start = Instant.parse("2026-11-01T00:00:00Z");
        Instant end = Instant.parse("2026-11-02T00:00:00Z");

        when(observationRepository
                .findByObjectIdAndObservedAtBetween(SAT_001, start, end))
                .thenReturn(List.of(observation));

        List<Observation> result =
                observationService.getObservationsByObjectAndTimeRange(
                        SAT_001, start, end
                );

        assertEquals(1, result.size());

        verify(observationRepository)
                .findByObjectIdAndObservedAtBetween(SAT_001, start, end);
        verifyNoInteractions(observationProducer);
    }

    @Test
    void shouldCreateObservationAndPublishKafkaEvent() {
        Instant observedAt = Instant.parse("2026-11-01T16:30:00Z");

        ArgumentCaptor<Observation> observationCaptor =
                ArgumentCaptor.forClass(Observation.class);

        ArgumentCaptor<ObservationCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(ObservationCreatedEvent.class);

        when(observationRepository.save(any(Observation.class)))
                .thenAnswer(invocation -> {
                    Observation saved = invocation.getArgument(0);
                    return saved;
                });

        Observation result = observationService.createObservation(
                SAT_001,
                observedAt,
                6871.2,
                120.5,
                320.8
        );

        assertNotNull(result);

        verify(observationRepository).save(observationCaptor.capture());

        Observation savedObservation = observationCaptor.getValue();

        assertEquals(SAT_001, savedObservation.getObjectId());
        assertEquals(observedAt, savedObservation.getObservedAt());
        assertEquals(6871.2, savedObservation.getPositionX());
        assertEquals(120.5, savedObservation.getPositionY());
        assertEquals(320.8, savedObservation.getPositionZ());
        assertNotNull(savedObservation.getCreatedAt());

        verify(observationProducer).publish(eventCaptor.capture());

        ObservationCreatedEvent publishedEvent = eventCaptor.getValue();

        assertEquals(savedObservation.getId(), publishedEvent.observationId());
        assertEquals(SAT_001, publishedEvent.objectId());
        assertEquals(observedAt, publishedEvent.observedAt());

        verifyNoMoreInteractions(observationProducer);
    }
}
