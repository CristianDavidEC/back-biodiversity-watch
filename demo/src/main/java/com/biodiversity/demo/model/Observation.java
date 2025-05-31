package com.biodiversity.demo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.lang.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Entity
@Table(name = "observations")
public class Observation {
    @Id
    @JsonProperty("id_observation")
    private String id;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("latitude")
    private float latitude;

    @JsonProperty("longitude")
    private float longitude;

    @Nullable
    @JsonProperty("note")
    private String note;

    @Nullable
    @JsonProperty("state")
    private String state;

    @Nullable
    @JsonProperty("type_observation")
    @Column(name = "type_observation")
    private String typeObservation;

    @JsonProperty("verification_status")
    @Column(name = "verification_status")
    private boolean verificationStatus;

    @JsonProperty("id_specie")
    @Column(name = "id_specie")
    private String idSpecies;

    @JsonProperty("id_observer_user")
    @Column(name = "id_observer_user")
    private String idObserverUser;

    @JsonProperty("created_at")
    @Column(name = "created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private String updatedAt;
}
