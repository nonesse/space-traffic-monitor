package com.spacetraffic.monitor.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "observations")
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "object_id", nullable = false, length = 50)
    private String objectId;

    @Column(name = "observed_at", nullable = false)
    private Instant observedAt;

    @Column(name = "position_x", nullable = false)
    private double positionX;

    @Column(name = "position_y", nullable = false)
    private double positionY;

    @Column(name = "position_z", nullable = false)
    private double positionZ;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Observation() {
    }

    public Observation(
            String objectId,
            Instant observedAt,
            double positionX,
            double positionY,
            double positionZ
    ) {
        this.objectId = objectId;
        this.observedAt = observedAt;
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getObjectId() { return objectId; }
    public Instant getObservedAt() { return observedAt; }
    public double getPositionX() { return positionX; }
    public double getPositionY() { return positionY; }
    public double getPositionZ() { return positionZ; }
    public Instant getCreatedAt() { return createdAt; }
}
