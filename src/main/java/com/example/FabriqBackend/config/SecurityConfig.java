package com.example.FabriqBackend.config;

import com.example.FabriqBackend.config.Tenant.TenantFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final TenantFilter tenantFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(customizer -> customizer.disable())
                // Connects to the bean below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/v1/user/login",
                                "/v1/user/register",
                                "/v1/user/refresh",
                                "/v1/user/token-status",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/ws/**",
                                "/topic/**",
                                "/v1/device-attendance/punch",
                                "/v1/attendance/**"
                        ).permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers(
                                "/v1/attire/**",
                                "/v1/category/**"
                        ).permitAll()
                        .requestMatchers("/v1/billing/**",
                                "/v1/attire-rent/**",
                                "/v1/customer/**",
                                "/v1/payroll/payslip/**"
                        ).hasAnyRole("OWNER", "CASHIER")
                        .requestMatchers(
                                "/v1/employees/**",
                                "/v1/employee-allowances/**",
                                "/v1/advance-payments/**",
                                "/v1/allowance-types/**",
                                "/v1/attendance/**",
                                "/v1/deduction-types/**",
                                "/v1/device-attendance/**",
                                "/v1/employee-deductions/**",
                                "/v1/holidays/**",
                                "/v1/payroll/**",
                                "/v1/production-records/**"
                        ).hasRole("OWNER")
                        .anyRequest().authenticated())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tenantFilter, JwtFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origins
        configuration.setAllowedOrigins(Arrays.asList(
                "https://fabriq-frontend.vercel.app",
                "https://myapp.social",
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174"

        ));

        // Methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Headers - Allow ALL
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Exposed Headers
        configuration.setExposedHeaders(Arrays.asList("Authorization", "x-tenant-id", "X-Tenant-ID"));

        // Credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}