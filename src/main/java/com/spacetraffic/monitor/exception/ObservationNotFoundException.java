package com.spacetraffic.monitor.exception;

import java.util.UUID;

public class ObservationNotFoundException extends RuntimeException {

    public ObservationNotFoundException(UUID id) {
        super("Observation not found with id: " + id);
    }
}
