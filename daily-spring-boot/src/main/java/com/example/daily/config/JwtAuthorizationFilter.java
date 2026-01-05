package com.example.daily.config;

import com.example.daily.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(JwtUtil.BEARER_PREFIX)) {
            String token = tokenValue.substring(7);

            if (!jwtUtil.validateToken(token)) {
                log.error("Token Error");
                response.setStatus(401);
                return;
            }

            Claims info = jwtUtil.getUserInfoFromToken(token);
            jwtUtil.setAuthentication(info.getSubject());
        }
        filterChain.doFilter(request, response);
    }

}
