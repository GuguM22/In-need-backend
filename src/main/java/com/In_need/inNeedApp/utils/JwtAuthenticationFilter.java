package com.In_need.inNeedApp.utils;


import com.In_need.inNeedApp.services.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtil;
    private final TokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(JwtUtils jwtUtil, TokenBlacklistService blacklistService) {
        this.jwtUtil = jwtUtil;
        this.blacklistService = blacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // skip public endpoints (as you already had)
        if (requestURI.startsWith("/auth/login") ||
                requestURI.startsWith("/auth/register") ||
                requestURI.startsWith("/auth/verify") ||
                requestURI.startsWith("/auth/forgot-password") ||
                requestURI.startsWith("/auth/reset-password")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token != null) {
            // 1) Check blacklist first
            if (blacklistService.isBlacklisted(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token revoked");
                return;
            }

            // 2) Validate token and set Authentication
            try {
                if (jwtUtil.validateToken(token)) {
                    String email = jwtUtil.extractUsername(token);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, getAuthorities(token));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ Set authentication with authorities: " + authentication.getAuthorities());

                }
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtUtil.getSecret().getBytes());
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = claims.get("roles");
        System.out.println("Extracted roles from JWT: " + rolesObject);

        if (rolesObject instanceof Collection<?>) {
            Collection<?> roles = (Collection<?>) rolesObject;

            return roles.stream()
                    .filter(role -> role instanceof String)
                    .map(role -> new SimpleGrantedAuthority((String) role))
                    .toList();
        }

        return Collections.emptyList();
    }
}
