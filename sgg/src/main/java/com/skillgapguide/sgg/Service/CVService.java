package com.skillgapguide.sgg.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.skillgapguide.sgg.Dto.ExtractCvSkillDTO;
import com.skillgapguide.sgg.Entity.Cv;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Entity.UserCvSkills;
import com.skillgapguide.sgg.Repository.CVRepository;
import com.skillgapguide.sgg.Repository.UserCvSkillsRepository;
import com.skillgapguide.sgg.Repository.UserRepository;
import com.skillgapguide.sgg.Response.AIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CVService {
    @Autowired
    private CVRepository cvRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCvSkillsRepository userCvSkillsRepository;
    private final String UPLOAD_DIR = "D:/CvData/";
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CVService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String uploadCv(String fileName,String fileExtension, MultipartFile file){
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
            Integer userId = userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());

            Cv cvMetadata = new Cv();
            cvMetadata.setUserId(userId);
            cvMetadata.setFileName(fileName);
            cvMetadata.setFilePath(path.toAbsolutePath().toString());
            cvMetadata.setFileType(fileExtension);
            cvMetadata.setUploadDate(LocalDateTime.now());
            cvRepository.save(cvMetadata);
            // Chạy extract skill trong thread riêng
            new Thread(() -> {
                try {
                    extractSkill(path.toAbsolutePath().toString(),cvMetadata.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            return "File CV đã được upload thành công: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload file CV thất bại";
        }
    }
    public static String extractTextFromPdf(String filePath) throws IOException {
        try{

            StringBuilder text = new StringBuilder();
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filePath));
            int pages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
            }
            pdfDoc.close();
            return text.toString();
        } catch(IOException ioException){
            throw new IOException(ioException.getMessage());
        }
    }
    public void extractSkill(String filePath, int cvId) throws IOException {
        String text = extractTextFromPdf(filePath);
        String prompt = "kể tên các kỹ năng chính từ CV sau:\n" + text;
        Map<String, Object> requestBody = Map.of(
                "model", "mistralai/mistral-7b-instruct-v0.3",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 2048,
                "stream", false
        );
        LMStudioService service = new LMStudioService(WebClient.builder());
        service.callLMApi(requestBody)
                .subscribe(content -> {
                    try {
                        saveCvSkillsToDb(content, cvId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
    public void saveCvSkillsToDb(String aiResponseJson, int cvId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ExtractCvSkillDTO response = mapper.readValue(aiResponseJson, ExtractCvSkillDTO.class);
        List<String> skills = response.getSkills();
        for (String skill : skills) {
            UserCvSkills userCvSkill = new UserCvSkills();
            userCvSkill.setSkill(skill);
            userCvSkill.setCvId(cvId);
            userCvSkillsRepository.save(userCvSkill);
            System.out.println("Skill:-----  "+skill);
        }
    }
}
