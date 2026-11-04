package com.spacetraffic.monitor.controller;

import com.spacetraffic.monitor.dto.ObservationRequest;
import com.spacetraffic.monitor.entity.Observation;
import com.spacetraffic.monitor.exception.GlobalExceptionHandler;
import com.spacetraffic.monitor.exception.ObservationNotFoundException;
import com.spacetraffic.monitor.service.ObservationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ObservationController.class)
@Import(GlobalExceptionHandler.class)
class ObservationControllerTest {

    private static final String BASE_URL = "/api/observations";
    private static final String SAT_001 = "SAT-001";
    private static final String UNKNOWN_OBJECT_ID = "UNKNOWN";

    private static final Instant OBSERVED_AT =
            Instant.parse("2026-11-01T16:30:00Z");

    private static final double POSITION_X = 6871.2;
    private static final double POSITION_Y = 120.5;
    private static final double POSITION_Z = 320.8;

    private static final int MAX_OBJECT_ID_LENGTH = 50;
    private static final String INVALID_UUID = "invalid-uuid";

    private static final String VALID_OBSERVATION_JSON = """
            {
                "objectId": "%s",
                "observedAt": "%s",
                "positionX": %s,
                "positionY": %s,
                "positionZ": %s
            }
            """.formatted(
            SAT_001,
            OBSERVED_AT,
            POSITION_X,
            POSITION_Y,
            POSITION_Z
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ObservationService observationService;

    private Observation observation;
    private UUID observationId;

    @BeforeEach
    void setUp() {
        observationId = UUID.randomUUID();

        observation = new Observation(
                SAT_001,
                OBSERVED_AT,
                POSITION_X,
                POSITION_Y,
                POSITION_Z
        );
    }

    // POST /api/observations

    @Test
    void shouldCreateObservation() throws Exception {
        ObservationRequest request = new ObservationRequest(
                SAT_001,
                OBSERVED_AT,
                POSITION_X,
                POSITION_Y,
                POSITION_Z
        );

        when(observationService.createObservation(
                anyString(),
                any(Instant.class),
                anyDouble(),
                anyDouble(),
                anyDouble()
        )).thenReturn(observation);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.objectId").value(SAT_001))
                .andExpect(jsonPath("$.positionX").value(POSITION_X))
                .andExpect(jsonPath("$.positionY").value(POSITION_Y))
                .andExpect(jsonPath("$.positionZ").value(POSITION_Z));

        verify(observationService).createObservation(
                SAT_001,
                OBSERVED_AT,
                POSITION_X,
                POSITION_Y,
                POSITION_Z
        );
    }

    @Test
    void shouldReturn400WhenObjectIdIsBlank() throws Exception {
        String request = VALID_OBSERVATION_JSON.replace(
                "\"objectId\": \"" + SAT_001 + "\"",
                "\"objectId\": \"\""
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.objectId").exists());

        verifyNoInteractions(observationService);
    }

    @Test
    void shouldReturn400WhenObjectIdExceedsMaximumLength() throws Exception {
        String longObjectId = "A".repeat(MAX_OBJECT_ID_LENGTH + 1);

        String request = VALID_OBSERVATION_JSON.replace(
                SAT_001,
                longObjectId
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(observationService);
    }

    @Test
    void shouldReturn400WhenObservedAtIsNull() throws Exception {
        String request = VALID_OBSERVATION_JSON.replace(
                "\"" + OBSERVED_AT + "\"",
                "null"
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(observationService);
    }

    @Test
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(observationService);
    }

    // GET /api/observations

    @Test
    void shouldReturnAllObservations() throws Exception {
        when(observationService.getAllObservations())
                .thenReturn(List.of(observation));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].objectId").value(SAT_001))
                .andExpect(jsonPath("$[0].positionX").value(POSITION_X));

        verify(observationService).getAllObservations();
    }

    @Test
    void shouldReturnEmptyListWhenNoObservationsExist() throws Exception {
        when(observationService.getAllObservations())
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // GET /api/observations/{id}

    @Test
    void shouldReturnObservationById() throws Exception {
        when(observationService.getObservationById(observationId))
                .thenReturn(observation);

        mockMvc.perform(get(BASE_URL + "/{id}", observationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.objectId").value(SAT_001))
                .andExpect(jsonPath("$.positionX").value(POSITION_X));

        verify(observationService).getObservationById(observationId);
    }

    @Test
    void shouldReturn404WhenObservationDoesNotExist() throws Exception {
        when(observationService.getObservationById(observationId))
                .thenThrow(new ObservationNotFoundException(observationId));

        mockMvc.perform(get(BASE_URL + "/{id}", observationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Observation not found"));
    }

    @Test
    void shouldReturn400WhenUuidIsInvalid() throws Exception {
        mockMvc.perform(get(BASE_URL + "/" + INVALID_UUID))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(observationService);
    }

    // GET /api/observations/object/{objectId}

    @Test
    void shouldReturnObservationsByObjectId() throws Exception {
        when(observationService.getObservationsByObjectId(SAT_001))
                .thenReturn(List.of(observation));

        mockMvc.perform(get(BASE_URL + "/object/{objectId}", SAT_001))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].objectId").value(SAT_001));

        verify(observationService).getObservationsByObjectId(SAT_001);
    }

    @Test
    void shouldReturnEmptyListWhenObjectHasNoObservations() throws Exception {
        when(observationService.getObservationsByObjectId(UNKNOWN_OBJECT_ID))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL + "/object/{objectId}", UNKNOWN_OBJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn400WhenObjectIdIsBlankInGetRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/object/{objectId}", " "))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(observationService);
    }

    @Test
    void shouldReturn400WhenObjectIdExceedsMaximumLengthInGetRequest() throws Exception {
        String longObjectId = "A".repeat(MAX_OBJECT_ID_LENGTH + 1);

        mockMvc.perform(get(BASE_URL + "/object/{objectId}", longObjectId))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(observationService);
    }
}