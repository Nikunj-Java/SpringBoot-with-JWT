package com.neueda.learning.jwt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String sharedSecret;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
                sharedSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Health API - everyone can access
                        .requestMatchers(
                                "/v1/accounts/health"
                        ).permitAll()

                        // GET -> VIEW OR OPERATOR
                        .requestMatchers(
                                HttpMethod.GET,
                                "/v1/accounts/**"
                        ).hasAnyRole(
                                "MISSION_VIEW",
                                "MISSION_OPERATOR"
                        )

                        // POST -> OPERATOR
                        .requestMatchers(
                                HttpMethod.POST,
                                "/v1/accounts/**"
                        ).hasRole(
                                "MISSION_OPERATOR"
                        )

                        // PUT -> OPERATOR
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/v1/accounts/**"
                        ).hasRole(
                                "MISSION_OPERATOR"
                        )

                        // DELETE -> ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/v1/accounts/**"
                        ).hasRole(
                                "MISSION_ADMIN"
                        )

                        // Everything else
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(
                        (OAuth2ResourceServerConfigurer<HttpSecurity> oauth2) ->
                                oauth2.jwt(
                                        jwt -> jwt.jwtAuthenticationConverter(
                                                authenticationConverter
                                        )
                                )
                );

        return http.build();
    }
}