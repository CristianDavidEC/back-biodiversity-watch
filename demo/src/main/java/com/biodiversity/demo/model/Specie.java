package com.biodiversity.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Specie {
    private String id;
    private String name;
    private String description;
    private String distribution;
    private String type;
    private String habitat;
    private String family;
    private String createdAt;
    private String updatedAt;
}
