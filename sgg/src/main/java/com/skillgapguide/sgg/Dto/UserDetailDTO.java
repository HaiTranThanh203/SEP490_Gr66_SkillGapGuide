package com.skillgapguide.sgg.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDetailDTO {
    private String name;
    private String email;
    private String phone;
    private String role;
    private String subscription;
    private String status;

    public UserDetailDTO(String name, String email, String phone, String role, String subscription, String status) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.subscription = subscription;
        this.status = status;
    }
}
