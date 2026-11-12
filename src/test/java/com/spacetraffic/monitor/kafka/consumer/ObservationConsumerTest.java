package com.spacetraffic.monitor.kafka.consumer;

import com.spacetraffic.monitor.kafka.event.ObservationCreatedEvent;
import com.spacetraffic.monitor.kafka.service.ObservationEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ObservationConsumerTest {

    @Mock
    private ObservationEventService observationEventService;

    @InjectMocks
    private ObservationConsumer observationConsumer;

    private ObservationCreatedEvent event;

    @BeforeEach
    void setUp() {

        event = new ObservationCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "SAT-001",
                Instant.now()
        );
    }

    @Test
    void shouldProcessObservationCreatedEvent() {
        observationConsumer.consume(event);

        verify(observationEventService).process(event);

        verifyNoMoreInteractions(observationEventService);
    }
}