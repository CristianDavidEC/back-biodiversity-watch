package com.biodiversity.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    private String id;
    private String username;
    private String email;
    private String password;
    private String createdAt;
}
