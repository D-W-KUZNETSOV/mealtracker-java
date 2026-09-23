package com.e.mealtracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 🔒 Пропускаем H2 Console без всякой обработки
        if (requestURI.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String authHeader = request.getHeader("Authorization");

            // ✅ Добавьте лог для отладки
            log.info("🔍 Request URI: {}", request.getRequestURI());
            log.info("🔍 Authorization header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.info("⏭️ No Bearer token, skipping authentication");
                filterChain.doFilter(request, response);
                return;
            }

            final String jwtToken = authHeader.substring(7);
            log.info("🔑 Token: {}", jwtToken);

            final String username = jwtService.extractUsername(jwtToken);
            log.info("👤 Extracted username: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("✅ UserDetails loaded: {}", userDetails.getUsername());
                log.info("✅ Authorities: {}", userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("✅ Authentication set for user: {}", username);
                } else {
                    log.warn("❌ JWT token is invalid or expired for user: {}", username);
                }
            }

        } catch (UsernameNotFoundException e) {
            log.warn("❌ User not found: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error during JWT authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
