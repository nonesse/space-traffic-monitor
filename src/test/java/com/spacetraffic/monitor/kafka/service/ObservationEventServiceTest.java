
package com.spacetraffic.monitor.kafka.service;

import com.spacetraffic.monitor.kafka.event.ObservationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObservationEventServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ObservationEventService observationEventService;

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
    void shouldProcessEventWhenItIsNew() {
        when(jdbcTemplate.update(anyString(), eq(event.eventId()))).thenReturn(1);

        observationEventService.process(event);

        verify(jdbcTemplate).update(anyString(),eq(event.eventId()));

        verifyNoMoreInteractions(jdbcTemplate);
    }

    @Test
    void shouldIgnoreEventWhenItIsDuplicate() {
        when(jdbcTemplate.update(anyString(), eq(event.eventId()))).thenReturn(0);

        observationEventService.process(event);

        verify(jdbcTemplate).update(anyString(),eq(event.eventId()));

        verifyNoMoreInteractions(jdbcTemplate);
    }
}
