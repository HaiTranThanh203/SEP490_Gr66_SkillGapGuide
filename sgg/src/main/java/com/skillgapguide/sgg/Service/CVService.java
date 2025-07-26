package com.skillgapguide.sgg.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.skillgapguide.sgg.Entity.Cv;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Entity.UserCvSkills;
import com.skillgapguide.sgg.Repository.CVRepository;
import com.skillgapguide.sgg.Repository.UserCvSkillsRepository;
import com.skillgapguide.sgg.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CVService {
    @Autowired
    private CVRepository cvRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserCvSkillsRepository userCvSkillsRepository;
    @Autowired
    private EmbedService embedService;
    @Autowired
    private CvSkillService cvSkillService;
    private final String UPLOAD_DIR = "D:/CvData/";

    public String uploadCv(String fileName,String fileExtension, MultipartFile file){
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
            Integer userId = userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());
            Cv cv = cvRepository.findByUserId(userId);
            int finalCvId;
            if(cv != null){
                cv.setFileName(fileName);
                cv.setFilePath(path.toAbsolutePath().toString());
                cv.setFileType(fileExtension);
                cv.setUploadDate(LocalDateTime.now());
                cvRepository.save(cv);
                finalCvId = cv.getId();
            }
            else {
                Cv cvMetadata = new Cv();
                cvMetadata.setUserId(userId);
                cvMetadata.setFileName(fileName);
                cvMetadata.setFilePath(path.toAbsolutePath().toString());
                cvMetadata.setFileType(fileExtension);
                cvMetadata.setUploadDate(LocalDateTime.now());
                cvRepository.save(cvMetadata);
                finalCvId = cvMetadata.getId();
            }
            extractSkill(path.toAbsolutePath().toString(), finalCvId);
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
        String prompt = "Hãy phân tích CV dưới đây và trích xuất tất cả các kỹ năng chính của ứng viên. Chỉ trả về kết quả dưới dạng JSON theo mẫu sau, không thêm bất kỳ nội dung nào khác:\n" +
                "\n" +
                "{\n" +
                "  \"skills\": [\n" +
                "  ]\n" +
                "}\n" +
                "\n" +
                "CV:\n" + text ;

        LMStudioService service = new LMStudioService(WebClient.builder());
        String content = service.callLMApi(prompt).block(); // <- CHỜ kết quả trả về

        try {
            cvSkillService.saveCvSkillsToDb(content, cvId);
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu kỹ năng vào DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public List<UserCvSkills> getCvSkill(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
        Integer userId = userRepository.findByEmail(email)
                .map(User::getUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cv cv = cvRepository.findByUserId(userId);
        return userCvSkillsRepository.findByCvId(cv.getId());
    }
}
