package com.coreon.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Value("${VMWARE_IP:localhost}")
    private String vmIp;

	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        )

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청 허용
                )
                .build();
    }

 
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 1) Allowed Origins: 허용할 요청 출처
        config.setAllowedOrigins(
                List.of(
                        "http://127.0.0.1:5500",   
                        "http://localhost:5500",  
                        "http://localhost:3000",  
                        "http://" + vmIp + ":5500",
                        "http://" + vmIp + ":8080",
                        "http://" + vmIp + ":8082",
                        "http://localhost:8080",
                        "http://localhost:8082"
                )
        );

        // 2) Allowed Methods: 허용할 HTTP 요청
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        );

        // 3) Allowed Headers: 프론트에서 보낼 수 있는 헤더
        config.setAllowedHeaders(
                List.of("*")
        );

        // 4) Allow Credentials: 쿠키/세션 포함 여부
        config.setAllowCredentials(true);

        // 5) Max Age: Preflight OPTIONS 요청 캐싱 시간 (초 단위)
        config.setMaxAge(3600L); // 1시간

      
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
