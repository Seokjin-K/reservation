package com.reservation.config;

import com.reservation.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * api 관련 권한을 조정
     *
     * @param http
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable() // 기존 http 인증 비활성화
                .csrf().disable() // CSRF 보호 비활성화 (REST API 이므로)
                .sessionManagement() // 세션 관리 설정 시작
                // 세션 사용하지 않음 (JWT 사용 중)
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests() // URL 별 권한 설정 시작
                // 회원가입, 로그인은 모두 허용
                .antMatchers(
                        "/**/signup", "/**/signin").permitAll()
                .and()
                // JWT 필터를 UsernamePassword 필터 전에 추가
                .addFilterBefore(this.jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration)
            throws Exception {

        // Spring Security 의 인증을 담당하는 AuthenticationManager 설정
        return authenticationConfiguration.getAuthenticationManager();
    }
}
