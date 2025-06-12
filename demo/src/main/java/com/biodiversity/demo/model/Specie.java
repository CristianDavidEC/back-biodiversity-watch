package com.biodiversity.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specie {
    @JsonProperty("id_specie")
    private String id;

    @JsonProperty("scientific_name")
    private String scientificName;

    @JsonProperty("common_name")
    private String commonName;

    @JsonProperty("type")
    private String type;

    @JsonProperty("habitat")
    private String habitat;

    @JsonProperty("size")
    private String size;

    @JsonProperty("ecological_role")
    private String ecologicalRole;

    @JsonProperty("conservation_status")
    private String conservationStatus;

    @JsonProperty("description")
    private String description;

    @JsonProperty("distribution")
    private String distribution;

    @JsonProperty("family")
    private String family;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;
}
