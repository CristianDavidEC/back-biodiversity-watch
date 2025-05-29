package com.biodiversity.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    private String id;
    private String name;
    private String email;
    private String description;
    private String profession;
    private String image;
    private String createdAt;
    private String updatedAt;
}
