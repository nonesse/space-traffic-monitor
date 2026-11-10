package com.spacetraffic.monitor.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ObservationProducer {

    private static final String TOPIC = KafkaConstants.OBSERVATION_CREATED_TOPIC;

    private final KafkaTemplate<String, ObservationCreatedEvent> kafkaTemplate;

    public ObservationProducer(KafkaTemplate<String, ObservationCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ObservationCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.observationId().toString(),
                event
        );
    }
}