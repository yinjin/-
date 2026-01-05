package com.material.system.config;

import com.material.system.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Value("${jwt.header:Authorization}")
    private String header;

    @Value("${jwt.prefix:Bearer}")
    private String prefix;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Debug logging for login requests
        if (requestURI.contains("/login")) {
            System.out.println("JWT Filter: Processing login request: " + method + " " + requestURI);
        }

        String authHeader = request.getHeader(header);
        if (authHeader != null && authHeader.startsWith(prefix)) {
            String token = authHeader.substring(prefix.length()).trim();
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // create a simple authentication. roles/authorities are not used in this project.
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null,
                                    Collections.singletonList(new SimpleGrantedAuthority("USER")));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (requestURI.contains("/login")) {
                        System.out.println("JWT Filter: Set authentication for user: " + username);
                    }
                }
            } catch (Exception e) {
                if (requestURI.contains("/login")) {
                    System.out.println("JWT Filter: Token validation failed: " + e.getMessage());
                }
                // if token invalid just continue without authentication
            }
        } else {
            if (requestURI.contains("/login")) {
                System.out.println("JWT Filter: No auth header found, proceeding without authentication");
            }
        }

        if (requestURI.contains("/login")) {
            System.out.println("JWT Filter: Continuing to next filter in chain");
        }
        filterChain.doFilter(request, response);
    }
}
