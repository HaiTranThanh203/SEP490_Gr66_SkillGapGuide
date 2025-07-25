package com.skillgapguide.sgg.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddOccupationRequestDTO {
    private String name;
    private Integer occupationGroupId;
    private String status;
}
