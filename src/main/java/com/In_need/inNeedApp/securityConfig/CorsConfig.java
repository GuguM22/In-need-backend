package com.In_need.inNeedApp.securityConfig;

import com.In_need.inNeedApp.services.CustomUserDetailsService;
import com.In_need.inNeedApp.services.TokenBlacklistService;
import com.In_need.inNeedApp.utils.JwtAuthenticationFilter;
import com.In_need.inNeedApp.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class CorsConfig {



        @Autowired
        private CustomUserDetailsService customUserDetailsService;

//
//        @Bean
//        public WebMvcConfigurer corsConfigurer() {
//            return new WebMvcConfigurer() {
//                @Override
//                public void addCorsMappings(CorsRegistry registry) {
//                    registry.addMapping("/**")
//                            .allowedOrigins("http://localhost:4200")
//                            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                            .allowedHeaders("*");
//                }
//            };
//        }



    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/auth/images/**")
                    .addResourceLocations("file:uploads/");
        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                // Public endpoints
                                .requestMatchers(
                                        "/auth/register",
                                        "/auth/login",
                                        "/auth/forgot-password",
                                        "/auth/reset-password",
                                        "/auth/logout",
                                        "/auth/upload-profile-image",
                                        "/auth/profile",
                                        "/api/verify/verification",
                                        "/api/verify/upload",

                                        "/api/individual-requests",   // exact match
                                        // allow all subpaths


                                        // Role-based endpoints


                                        //"/api/verify/exists/phone/**",
                                        "/api/verify/verified",

                                        "/api/verify/download/**",
                                        "/documents/**",
                                        "/auth/donations/post",
                                        "/auth/donations/details"

                                ).permitAll()
                                .requestMatchers("/auth/images/**").permitAll()
                                .requestMatchers("/api/verify/exists/phone/**").permitAll()
                                .requestMatchers("/api/verify/verification/status/**").permitAll()
                                .requestMatchers("/auth/profile").authenticated()

                                .requestMatchers("/ADMIN/**").hasRole("ADMIN")
//                        .requestMatchers("/ORGANIZATION/**").hasRole("ORGANIZATION")
                                .requestMatchers("/INDIVIDUAL/**").hasRole("INDIVIDUAL")
                                .requestMatchers("/SPONSORS/**").hasAnyRole("SPONSORS", "ADMIN")
                                .requestMatchers("/auth/donations/**").hasAnyRole("SPONSORS")

                                // Everything else requires authentication
                                .anyRequest().authenticated()
                )
                // Make sure JWT filter is only applied after public endpoints are bypassed
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
    @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
            AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
            authBuilder.userDetailsService(customUserDetailsService)
                    .passwordEncoder(passwordEncoder());
            return authBuilder.build();
        }

        @Bean
        public UserDetailsService userDetailsService(CustomUserDetailsService authService) {
            return authService;
        }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtil, TokenBlacklistService tokenBlacklistService) {
        return new JwtAuthenticationFilter(jwtUtil, tokenBlacklistService);
    }

    @Bean
    public JwtUtils jwtUtil() {
        return new JwtUtils();
    }

    @Bean
    public TokenBlacklistService tokenBlacklistService() {
        return new TokenBlacklistService();
    }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            // Makes a new object called CorsConfiguration.  All the CORS (Cross-Origin Resource Sharing) settings are stored in this object.
            CorsConfiguration configuration = new CorsConfiguration();
            // Gives information about which origins (frontend URLs) can send cross-origin requests to your backend.
            // Your Angular development server, typically located at http://localhost:4200, is the only permitted IP address.
            // We will block requests from other sources.
            configuration.addAllowedOriginPattern("http://localhost:4200");
            //configuration.addAllowedOriginPattern("http://10.100.3.53:4200");
            // Indicates which HTTP methods from the permitted origins are permitted.
            //  Requests such as GET, POST, PUT, DELETE, and OPTIONS are supported.
            // Requests made with methods not on this list will be rejected.
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            // Gives the frontend permission to include specific headers in the request.
            //  All headers are permitted when "*" is used.
            //  Both default and custom headers (such as Authorization, Content-Type, etc.) can be sent by the frontend.
            configuration.setAllowedHeaders(List.of("*"));
            // Allows or prohibits the inclusion of user credentials (such as cookies, authorization headers, or TLS client certificates) in cross-origin requests.
            // It's true, so credentials are sent.
            // Lets you include cookies or other auth tokens in requests.
            configuration.setAllowCredentials(true);
            // Specifies how long (in seconds) the browser may cache the preflight request's results.
            //  3600 seconds, or one hour.
            // Within an hour, the browser is not required to send the same preflight (OPTIONS) request again.
            configuration.setMaxAge(3600L);

            // Creates a new UrlBasedCorsConfigurationSource that associates CORS configurations with URL paths.
            org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            // To make your CorsConfiguration applicable to all paths, register it (/**).
            // This CORS configuration will be used by all backend endpoints.
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }



    }


