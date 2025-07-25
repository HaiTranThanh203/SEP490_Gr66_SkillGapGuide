package com.skillgapguide.sgg.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
public class UploadCvRequest {
    @Schema(
            description = "Danh sách file CV",
            type = "array",
            format = "binary"
    )
    private MultipartFile[] files;
}
