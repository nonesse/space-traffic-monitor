package com.spacetraffic.monitor.kafka;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObservationConsumerTest {

    private ObservationConsumer observationConsumer;

    private ObservationCreatedEvent event;

    private final ByteArrayOutputStream outputStream =
            new ByteArrayOutputStream();

    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        observationConsumer = new ObservationConsumer();

        event = new ObservationCreatedEvent(
                UUID.randomUUID(),
                "SAT-001",
                Instant.now()
        );

        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void shouldConsumeObservationCreatedEvent() {
        observationConsumer.consume(event);
        String output = outputStream.toString();

        assertTrue(output.contains(
                "Evento recibido desde Kafka:"
        ));

        assertTrue(output.contains(
                "Observation ID: " + event.observationId()
        ));

        assertTrue(output.contains(
                "Object ID: " + event.objectId()
        ));

        assertTrue(output.contains(
                "Observed at: " + event.observedAt()
        ));
    }

}