package com.biodiversity.demo.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Observation {
    private String id;
    private Date date;
    private float latitude;
    private float longitude;
    private String note;
    private String state;
    private String typeObservation;
    private boolean verificationStatus;
    private String idSpecies;
    private String idObserverUser;
    private String createdAt;
    private String updatedAt;
}
