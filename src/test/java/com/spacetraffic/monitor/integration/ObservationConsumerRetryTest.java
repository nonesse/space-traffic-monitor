package com.spacetraffic.monitor.integration;

import com.spacetraffic.monitor.kafka.KafkaConstants;
import com.spacetraffic.monitor.kafka.event.ObservationCreatedEvent;
import com.spacetraffic.monitor.kafka.service.ObservationEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:observation-retry-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@EmbeddedKafka(
        partitions = 1,
        topics = KafkaConstants.OBSERVATION_CREATED_TOPIC,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class ObservationConsumerRetryTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockitoBean
    private ObservationEventService observationEventService;

    @Test
    void shouldRetryProcessingWhenServiceFails() throws Exception {
        ObservationCreatedEvent event = new ObservationCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "SAT-001",
                Instant.now()
        );

        doThrow(new RuntimeException("Simulated processing failure"))
                .when(observationEventService)
                .process(event);

        kafkaTemplate.send(
                KafkaConstants.OBSERVATION_CREATED_TOPIC,
                event.observationId().toString(),
                event
        ).get(10, TimeUnit.SECONDS);

        verify(observationEventService, timeout(20000).times(4))
                .process(event);
    }
}
