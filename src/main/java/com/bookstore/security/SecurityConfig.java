package com.bookstore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers(
                                "/api/health",
                                "/api/users/register",
                                "/api/users/login"
                        ).permitAll()

                        // Public book browsing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books",
                                "/api/books/**"
                        ).permitAll()

                        // CORS preflight
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/cart/**"
                        ).hasRole("USER")

                        .requestMatchers(
                                "/api/wishlist/**"
                        ).hasRole("USER")

                        .requestMatchers(
                                "/api/customers/**"
                        ).hasRole("USER")

                                // =========================================================
// ADMIN ORDER OPERATIONS
// IMPORTANT: Keep these BEFORE /api/orders/{id}
// =========================================================

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/orders/all"
                                ).hasRole("ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/orders/{id}/status"
                                ).hasRole("ADMIN")

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/orders/admin/{id}"
                                ).hasRole("ADMIN")


// =========================================================
// USER ORDER OPERATIONS
// =========================================================

                                .requestMatchers(
                                        HttpMethod.POST,
                                        "/api/orders"
                                ).hasRole("USER")

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/orders"
                                ).hasRole("USER")

                                .requestMatchers(
                                        HttpMethod.GET,
                                        "/api/orders/{id}"
                                ).hasAnyRole("USER", "ADMIN")

                                .requestMatchers(
                                        HttpMethod.PUT,
                                        "/api/orders/{id}/cancel"
                                ).hasRole("USER")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}