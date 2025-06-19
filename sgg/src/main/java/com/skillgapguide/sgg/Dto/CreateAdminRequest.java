package com.skillgapguide.sgg.Dto;

import lombok.Data;

@Data
public class CreateAdminRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Integer roleId;
}
