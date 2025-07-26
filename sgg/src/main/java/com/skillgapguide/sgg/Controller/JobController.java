package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.CVService;
import com.skillgapguide.sgg.Service.JobService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobController {
    @Autowired
    private JobService jobService;
    @PostMapping(value="/upload-jd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<?> uploadCv(
            @Parameter(
                    description = "Tối đa 5 file CV (.pdf, .docx)",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
                    )
            )
            @RequestParam("file") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return new Response<>(EHttpStatus.BAD_REQUEST, "Vui lòng chọn ít nhất 1 file để upload");
        }
        if (files.length > 5) {
            return new Response<>(EHttpStatus.BAD_REQUEST, "Chỉ chấp nhận tối đa 5 file mỗi lần");
        }


        return new Response<>(EHttpStatus.OK, jobService.loadMultiFile(files));

    }
    @GetMapping("/getJobSkill")
    public Response<?> getCvSkill(@RequestParam int jobId){
        return new Response<>(EHttpStatus.OK, jobService.getJobSkill(jobId));
    }
    @GetMapping("/getJobList")
    public Response<?> getJobList(){
        return new Response<>(EHttpStatus.OK, jobService.getJobList());
    }

}
