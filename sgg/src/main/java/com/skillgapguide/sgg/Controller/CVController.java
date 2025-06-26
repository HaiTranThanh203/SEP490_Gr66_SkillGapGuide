package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.CVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/CV")
public class CVController {
    @Autowired
    private CVService cvService;


    @PostMapping(value="/upload-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<String> uploadCv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new Response<>(EHttpStatus.BAD_REQUEST,"Vui lòng chọn file để upload");
        }

        String fileName = file.getOriginalFilename();
        String fileExtension = fileName != null ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";

        if (!fileExtension.equalsIgnoreCase("pdf") && !fileExtension.equalsIgnoreCase("docx")) {
            return new Response<>(EHttpStatus.BAD_REQUEST,"Chỉ chấp nhận file PDF hoặc Word (.docx)");
        }
        return new Response<>(EHttpStatus.OK, cvService.uploadCv(fileName,fileExtension,file));

    }

}
