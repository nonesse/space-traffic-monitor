package com.spacetraffic.monitor.kafka.producer;

import com.spacetraffic.monitor.kafka.KafkaConstants;
import com.spacetraffic.monitor.kafka.event.ObservationCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class ObservationProducer {

    private static final String TOPIC = KafkaConstants.OBSERVATION_CREATED_TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(ObservationProducer.class);

    public ObservationProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ObservationCreatedEvent event) {
        log.info("Publishing event: {}", event.eventId());

        kafkaTemplate.send(
                TOPIC,
                event.observationId().toString(),
                event
        ).whenComplete(this::showCompletedLogs);
    }

    private void showCompletedLogs(SendResult<String, Object> result, Throwable exception){
        if (exception != null) {
            log.error("Error publishing event", exception);
        } else {
            log.info(
                    "Event published successfully. Topic: {}, Partition: {}, Offset: {}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        }
    }
}