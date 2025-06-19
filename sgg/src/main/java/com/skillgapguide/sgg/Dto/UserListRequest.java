package com.skillgapguide.sgg.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserListRequest {
        private String searchText;
        private String role;
        private String status;
        private Integer pageNo = 0;
        private Integer pageSize = 10;
}

