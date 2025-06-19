package com.skillgapguide.sgg.Security;

import com.skillgapguide.sgg.Filter.JWTAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // Bộ lọc JWT để xác thực người dùng
    private final JWTAuthFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;
    private static final String[] url = {
            "/api/auth/**", "/v3/api-docs/**",
            "/v3/api-docs/public",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/api/user/**",

            "/api/feedback/**",
            "/api/admin/**",

            "/api/scrape/**"

    };
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF vì dùng JWT
                .authorizeHttpRequests(auth -> auth
                        // Các endpoint không cần xác thực
                        .requestMatchers(url).permitAll()
                        // Ví dụ phân quyền: Endpoint này chỉ dành cho ADMIN (roleId=2)
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        // Ví dụ phân quyền: Endpoint này chỉ dành cho SYSTEMADMIN (roleId=1)
                        .requestMatchers("/api/systemadmin/**").hasAuthority("ROLE_SYSTEM_ADMIN")
                        // Tất cả các request khác đều cần xác thực
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không tạo session
                .authenticationProvider(authenticationProvider)
                // Thêm bộ lọc JWT vào trước bộ lọc mặc định của Spring Security
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}