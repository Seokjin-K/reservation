package com.reservation.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer "; // 인증 타입

    private final JwtTokenProvider tokenProvider;

    /**
     * JWT 토큰으로 인증 시도시에 호출
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = this.resolveTokenFromRequest(request);

        if (StringUtils.hasText(token) &&
                this.tokenProvider.validateToken(token)) {

            // JWT 토큰에서 사용자 정보 추출
            Authentication auth = this.tokenProvider.getAuthentication(token);
            // SecurityContext 에 저장
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info(String.format(
                    "\u001B[32[%s] -> %s", this.tokenProvider.getUsername(token),
                    request.getRequestURI() + "\u001B[0m"));
        }
        filterChain.doFilter(request, response);
    }

    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
