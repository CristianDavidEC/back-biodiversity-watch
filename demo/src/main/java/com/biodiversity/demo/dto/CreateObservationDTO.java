package com.biodiversity.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.lang.Nullable;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateObservationDTO {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private float latitude;
    private float longitude;

    @Nullable
    private String note;

    @Nullable
    private String state;

    @Nullable
    @JsonProperty("images")
    private List<String> images;

    @Nullable
    @JsonProperty("type_observation")
    private String typeObservation;

    @JsonProperty("verification_status")
    private boolean verificationStatus;

    @JsonProperty("similarity_percentage")
    private Double similarityPercentage;

    @JsonProperty("specie_scientific_name")
    private String specieScientificName;

    @JsonProperty("specie_common_name")
    private String specieCommonName;

    @JsonProperty("id_specie")
    private String idSpecies;

    @JsonProperty("id_observer_user")
    private String idObserverUser;
}