
package com.spacetraffic.monitor.kafka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ObservationProducerTest {

    @Mock
    private KafkaTemplate<String, ObservationCreatedEvent> kafkaTemplate;

    @InjectMocks
    private ObservationProducer observationProducer;

    private ObservationCreatedEvent event;

    @BeforeEach
    void setUp() {
        event = new ObservationCreatedEvent(
                UUID.randomUUID(),
                "SAT-001",
                Instant.now()
        );
    }

    @Test
    void shouldPublishObservationEventToKafka() {
        observationProducer.publish(event);

        verify(kafkaTemplate, times(1)).send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        );
    }
}
