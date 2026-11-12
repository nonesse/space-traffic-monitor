package com.spacetraffic.monitor.kafka.service;

import com.spacetraffic.monitor.kafka.event.ObservationCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObservationEventService {

    private static final Logger log = LoggerFactory.getLogger(ObservationEventService.class);

    private final JdbcTemplate jdbcTemplate;

    public ObservationEventService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void process(ObservationCreatedEvent event) {
        log.info("ObservationEventService.process() invoked");
        int inserted = jdbcTemplate.update(
                """ 
                INSERT INTO inbox_event (event_id)
                VALUES (?)
                ON CONFLICT (event_id) DO NOTHING
                """,
                event.eventId()
        );

        boolean isEventProcessed = inserted == 0;
        if (isEventProcessed) {
            log.info("Duplicate event ignored: {}", event.eventId());
            return;
        }

        log.info(
                "Processing event {} for observation {}. Object Id {}",
                event.eventId(),
                event.observationId(),
                event.objectId()
        );
    }
}