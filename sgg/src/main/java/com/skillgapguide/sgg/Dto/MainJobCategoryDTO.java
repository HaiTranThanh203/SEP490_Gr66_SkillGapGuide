package com.skillgapguide.sgg.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MainJobCategoryDTO {
    private Integer id;
    private String name;
    private String status;
}
