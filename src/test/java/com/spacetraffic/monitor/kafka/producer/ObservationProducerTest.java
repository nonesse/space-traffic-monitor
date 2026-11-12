
package com.spacetraffic.monitor.kafka.producer;

import com.spacetraffic.monitor.kafka.KafkaConstants;
import com.spacetraffic.monitor.kafka.event.ObservationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservationProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ObservationProducer observationProducer;

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
    void shouldPublishObservationEventToKafka() {
        when(kafkaTemplate.send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        )).thenReturn(new CompletableFuture<>());

        observationProducer.publish(event);

        verify(kafkaTemplate, times(1)).send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        );
    }

    @Test
    void shouldHandleSuccessfulPublication() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();

        when(kafkaTemplate.send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        )).thenReturn(future);

        observationProducer.publish(event);

        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition(
                        KafkaConstants.OBSERVATION_CREATED_TOPIC,
                        0
                ),
                0,
                5,
                System.currentTimeMillis(),
                0,
                0
        );

        SendResult<String, Object> sendResult = mock(SendResult.class);

        when(sendResult.getRecordMetadata()).thenReturn(metadata);

        future.complete(sendResult);

        verify(kafkaTemplate).send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        );
    }

    @Test
    void shouldHandlePublicationFailure() {
        CompletableFuture<SendResult<String, Object>> future =new CompletableFuture<>();

        when(kafkaTemplate.send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        )).thenReturn(future);

        observationProducer.publish(event);

        future.completeExceptionally(new RuntimeException("Kafka publication failed"));

        verify(kafkaTemplate).send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        );
    }
}
