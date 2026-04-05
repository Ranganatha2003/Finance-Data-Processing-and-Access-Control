package com.example.financebackend.config;

import com.example.financebackend.security.JwtAuthenticationFilter;
import com.example.financebackend.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtUtil jwtUtil;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtUtil jwtUtil) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.jwtUtil = jwtUtil;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    AuthenticationEntryPoint authEntryPoint = (request, response, authException) -> {
      String authorization = request.getHeader("Authorization");
      if (authorization != null && authorization.startsWith("Bearer ")) {
        String token = authorization.substring("Bearer ".length());
        // If the token is valid but access is not allowed, respond as forbidden.
        if (jwtUtil.isTokenValid(token)) {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
          return;
        }
      }
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    };

    AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) ->
        response.sendError(HttpServletResponse.SC_FORBIDDEN);

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Auth endpoints are public
            .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

            // Dashboard APIs are readable by VIEWER/ANALYST/ADMIN
            .requestMatchers("/api/dashboard/**").hasAnyRole("VIEWER", "ANALYST", "ADMIN")

            // Records: analysts can read, admins can read/write
            .requestMatchers(HttpMethod.GET, "/api/records/**").hasAnyRole("ANALYST", "ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/records").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/records/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/records/**").hasRole("ADMIN")

            // User management: admin only
            .requestMatchers("/api/users/**").hasRole("ADMIN")

            // Anything else requires authentication
            .anyRequest().authenticated()
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        );

    // Ensure JWT auth is set before Spring applies the anonymous fallback.
    http.addFilterBefore(jwtAuthenticationFilter, AnonymousAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // BCrypt is simple + standard for beginner-level auth projects.
    return new BCryptPasswordEncoder();
  }
}

