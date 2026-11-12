package com.spacetraffic.monitor.kafka.consumer;

import com.spacetraffic.monitor.kafka.KafkaConstants;
import com.spacetraffic.monitor.kafka.event.ObservationCreatedEvent;
import com.spacetraffic.monitor.kafka.service.ObservationEventService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
public class ObservationConsumer {

    private static final Logger log = LoggerFactory.getLogger(ObservationConsumer.class);

    private final ObservationEventService observationEventService;

    public ObservationConsumer(ObservationEventService observationEventService) {
        this.observationEventService = observationEventService;
    }

    @PostConstruct
    public void init() {
        log.info("ObservationConsumer initialized");
    }

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(
                    delay = 1000,
                    multiplier = 2.0,
                    maxDelay = 10000
            )
    )
    @KafkaListener(
            topics = KafkaConstants.OBSERVATION_CREATED_TOPIC,
            groupId = "space-traffic-group"
    )
    public void consume(ObservationCreatedEvent event) {
        log.info("ObservationConsumer received event: {}", event.eventId());

        observationEventService.process(event);
    }

    @DltHandler
    public void handleDlt(ObservationCreatedEvent event) {
        log.error(
                "Event sent to DLT. Observation ID: {}, Object ID: {}",
                event.observationId(),
                event.objectId()
        );
    }
}