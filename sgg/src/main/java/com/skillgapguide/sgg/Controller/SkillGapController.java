package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.CommentResponse;
import com.skillgapguide.sgg.Dto.SkillMatchResultDTO;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.CosineSimilarityService;
import com.skillgapguide.sgg.Service.SkillGapSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/gap")
public class SkillGapController {
    @Autowired
    private CosineSimilarityService cosineSimilarityService;
    @Autowired
    private SkillGapSevice skillGapSevice;
    @GetMapping("/getSkillGap")
    public Response<List<SkillMatchResultDTO>> getSkillGap(@RequestParam int jobId, @RequestParam int cvId) throws Exception {
        cosineSimilarityService.compareCvJob(jobId,cvId);
        return new Response<>(EHttpStatus.OK, skillGapSevice.getMatchingResults(jobId,cvId));
    }
    @GetMapping("/getCommentSkill")
    public Response<CommentResponse> getCommentSkill(@RequestParam int jobId, @RequestParam int cvId) throws Exception {
        return new Response<>(EHttpStatus.OK, skillGapSevice.getComment(jobId,cvId));
    }
}
