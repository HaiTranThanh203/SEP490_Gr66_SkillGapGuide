package com.skillgapguide.sgg.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgapguide.sgg.Dto.CommentResponse;
import com.skillgapguide.sgg.Dto.ExtractCvSkillDTO;
import com.skillgapguide.sgg.Dto.SkillMatchResultDTO;
import com.skillgapguide.sgg.Repository.JobCvSkillScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillGapSevice {
    @Autowired
    private JobCvSkillScoreRepository jobCvSkillScoreRepository;
    public List<SkillMatchResultDTO> getMatchingResults(int jobId, int cvId) {
        return jobCvSkillScoreRepository.findByCvSkillAndJobSkill(cvId, jobId);
    }
    public CommentResponse getComment(int jobId, int cvId) throws JsonProcessingException {
        List<SkillMatchResultDTO> data = getMatchingResults(jobId,cvId);
        String prompt = "Hãy đưa ra nhận xét chung tổng quát về ứng viên và yêu cầu tuyển dụng với mức phù hợp là 0.7. Nhận xét với riêng từng kỹ năng về kỹ năng của cv với lời khuyên để cải thiện và chỉ nhận xét với những kỹ năng dưới 0.7 . Chỉ trả về kết quả dưới dạng JSON theo mẫu sau, không thêm bất kỳ nội dung nào khác, không nhắc đến score cụ thể trong nhận xét:\n" +
                "{\n" +
                "\"generalComment\":\"\",\n" +
                "\"skillComment\":[\n" +
                "\"skill\":\"\",\n" +
                "\"comment\":\"\"\n" +
                "]\n" +
                "}\n" +
                "Dữ liệu để nhận xét :\n" + data.toString() ;
        LMStudioService service = new LMStudioService(WebClient.builder());
        String resultAI = service.callLMApi(prompt).block();
        ObjectMapper mapper = new ObjectMapper();
        CommentResponse result = mapper.readValue(resultAI, CommentResponse.class);
        return result;
    }
}
