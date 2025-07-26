package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "User")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true) // Google user có thể không có password
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "subscription_id", nullable = false)
    private Integer subscriptionId;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = true)
    private String avatar;
    @Enumerated(EnumType.STRING) // Lưu status dưới dạng chuỗi trong DB (VD: "VERIFIED")
    @Column(nullable = false)
    private static final String STATUS_VERIFIED = "VERIFIED";
    private static final String STATUS_BANNED = "BANNED";
    @ManyToOne(fetch = FetchType.EAGER) // EAGER để luôn tải thông tin status cùng với User
    @JoinColumn(name = "status_id", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    public enum Provider {
        LOCAL,    // Tài khoản thường
        GOOGLE    // Tài khoản Google
    }

    // ------ Spring Security ------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role;
        switch (this.roleId) {
            case 1 -> role = "ROLE_SYSTEM_ADMIN";
            case 2 -> role = "ROLE_CONTENT_MANAGER";
            case 3 -> role = "ROLE_FINANCE_ADMIN";
            case 4 -> role = "ROLE_USER";
            case 5 -> role = "ROLE_PREMIUM_USER";
            default -> role = "ROLE_UNKNOWN";
        }
        return List.of(new SimpleGrantedAuthority(role));
    }
    @Override
    public String getUsername() {
        // Spring Security sẽ dùng email làm username để xác thực
        return this.email;
    }


    @Override
    public String getPassword() {
        return this.password;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true; // Không kiểm soát
    }

    @Override
    public boolean isAccountNonLocked() {
        // BANNED = không cho login
        return this.status != null && !"BANNED".equalsIgnoreCase(this.status.getName());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Không kiểm soát
    }

    @Override
    public boolean isEnabled() {
        // Tài khoản có thể đăng nhập nếu status là "VERIFIED"
        return this.status != null && this.status.getName().equals(STATUS_VERIFIED);
    }
}
