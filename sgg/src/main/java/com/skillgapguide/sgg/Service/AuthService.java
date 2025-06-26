package com.skillgapguide.sgg.Service;
import com.skillgapguide.sgg.Dto.AuthRequest;
import com.skillgapguide.sgg.Dto.AuthResponse;
import com.skillgapguide.sgg.Dto.GoogleLoginRequest;
import com.skillgapguide.sgg.Dto.RegisterRequest;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Entity.UserStatus;
import com.skillgapguide.sgg.Entity.VerificationToken;
import com.skillgapguide.sgg.Filter.JWTUtil;
import com.skillgapguide.sgg.Repository.UserRepository;
import com.skillgapguide.sgg.Repository.UserStatusRepository;
import com.skillgapguide.sgg.Repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final VerificationTokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final UserStatusRepository userStatusRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Value("${application.base-url}")
    private String baseUrl;

    @Transactional
    public String register(RegisterRequest request) {
        // 1. Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalStateException("Email này đã được đăng ký");
        }
        UserStatus notVerifiedStatus = userStatusRepository.findByName("NOT_VERIFIED")
                .orElseThrow(() -> new IllegalStateException("Status 'NOT_VERIFIED' không tồn tại."));
        // 2. Tạo một đối tượng User mới
        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setFullName(request.fullName());
        newUser.setPhone(request.phone());
        newUser.setStatus(notVerifiedStatus);
        // 3. Mã hóa mật khẩu trước khi lưu
        newUser.setPassword(passwordEncoder.encode(request.password()));

        // 4. Gán vai trò và gói đăng ký mặc định
        newUser.setRoleId(3); // Giả sử 1 là vai trò "USER"
        newUser.setSubscriptionId(1); // Gói mặc định
        newUser.setProvider(User.Provider.LOCAL);
        // 5. Lưu người dùng vào cơ sở dữ liệu
        User savedUser = userRepository.save(newUser);

        // 6. Tạo và trả về token cho người dùng mới
        // Tạo token xác thực
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                savedUser,
                LocalDateTime.now().plusMinutes(3) // Token hết hạn sau 24 giờ
        );
        tokenRepository.save(verificationToken);

        // Tạo link xác thực
        String verificationLink = baseUrl + "/api/auth/verify?token=" + token;

        // Gửi email (Bạn cần tự implement EmailService)
        emailService.sendMail(
                savedUser.getEmail(),
                "Xác thực tài khoản của bạn",
                "Cảm ơn bạn đã đăng ký. Vui lòng nhấp vào đường link bên dưới để kích hoạt tài khoản của bạn:\n" + verificationLink
        );
        return "Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản.";
    }

    @Transactional
    public void verifyAccount(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token không hợp lệ"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(verificationToken);
            throw new IllegalStateException("Token đã hết hạn");
        }

        // Lấy đối tượng status "VERIFIED" từ DB
        UserStatus verifiedStatus = userStatusRepository.findByName("VERIFIED")
                .orElseThrow(() -> new IllegalStateException("Status 'VERIFIED' không tồn tại"));

        User user = verificationToken.getUser();

        // Cập nhật status của người dùng
        user.setStatus(verifiedStatus);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }
    @Transactional
    public AuthResponse login(AuthRequest request) {
        try {
            var user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new IllegalStateException("Người dùng không tồn tại"));

            if (user.getStatus().getName().equals("NOT_VERIFIED")) {
                throw new IllegalStateException("Tài khoản chưa được xác thực. Vui lòng liên hệ với quản trị viên");
            }
            // 1. Xác thực thông tin
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            // 2. Tìm user


            // 3. Tạo JWT
            String jwtToken = jwtUtil.generateToken(user.getUsername());

            return new AuthResponse(jwtToken);

        } catch (Exception ex) {
            if (ex instanceof ResponseStatusException) {
                throw ex; // ném lại để Spring xử lý đúng HTTP status
            }
            // 4. Bắt lỗi nếu xác thực thất bại
            logger.error("Xác thực thất bại cho email {}: {}", request.email(), ex.getMessage());
            throw new IllegalStateException("Xác thực thất bại cho email " + request.email()+ ex.getMessage());
        }
    }
    public AuthResponse processGoogleLogin(GoogleLoginRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required for Google login");
        }

        String email = request.getEmail().trim().toLowerCase();

        // 1. Check user existence by email
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            // Nếu user đã đăng ký bằng provider khác, trả lỗi rõ ràng
            if (existingUser.getProvider() != User.Provider.GOOGLE) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Email này đã đăng ký bằng phương thức khác. Vui lòng đăng nhập bằng phương thức cũ hoặc liên hệ hỗ trợ.");
            }
            // Nếu là Google user, chỉ generate token
            String token = jwtUtil.generateToken(existingUser.getUsername());
            return new AuthResponse(token);
        }

        try {
            // 2. Nếu chưa tồn tại, tạo user Google mới
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(request.getName());
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // random password
            newUser.setAvatar(request.getAvatar() != null ? request.getAvatar() : "https://example.com/default-avatar.png");
            newUser.setRoleId(3);
            newUser.setSubscriptionId(1);
            newUser.setProvider(User.Provider.GOOGLE);
            newUser.setPhone("0000000000");

            UserStatus verifiedStatus = userStatusRepository.findByName("VERIFIED")
                    .orElseThrow(() -> new IllegalStateException("Status 'VERIFIED' not found"));
            newUser.setStatus(verifiedStatus);

            User savedUser = userRepository.save(newUser);
            String token = jwtUtil.generateToken(savedUser.getUsername());
            return new AuthResponse(token);
        } catch (DataIntegrityViolationException e) {
            // Phòng trường hợp có 2 request insert trùng email cùng lúc
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này đã tồn tại. Vui lòng đăng nhập.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi không xác định khi đăng nhập Google.");
        }
    }


}