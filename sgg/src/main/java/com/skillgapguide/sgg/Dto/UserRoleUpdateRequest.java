package com.skillgapguide.sgg.Dto;

import lombok.Data;

@Data
public class UserRoleUpdateRequest {
    private Integer userId;
    private Integer newRoleId;
}
