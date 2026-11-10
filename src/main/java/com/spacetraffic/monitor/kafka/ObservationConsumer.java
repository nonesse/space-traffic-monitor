package com.spacetraffic.monitor.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ObservationConsumer {
    private static final Logger log = LoggerFactory.getLogger(ObservationConsumer.class);

    @KafkaListener(
            topics = KafkaConstants.OBSERVATION_CREATED_TOPIC,
            groupId = "space-traffic-group"
    )
    public void consume(ObservationCreatedEvent event) {
        log.info("Evento recibido desde Kafka:");
        log.info("Observation ID: {}", event.observationId());
        log.info("Object ID: {}", event.objectId());
        log.info("Observed at: {}", event.observedAt());
    }
}