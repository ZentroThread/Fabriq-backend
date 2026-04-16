package com.example.FabriqBackend.config;

import com.example.FabriqBackend.config.Tenant.TenantFilter;
import jakarta.servlet.http.HttpServletResponse;
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

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final TenantFilter tenantFilter;
    private final UserDetailsService userDetailsService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                // Connects to the bean below
                .cors(cors -> cors.configurationSource(new CorsConfig().corsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/**",
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
                                "/v1/public/**",
                                "/api/v1/tenant/all",
                                "/v1/rag/**",
                                "/v1/feedback/approved",
                                "/v1/customer/auth/**"
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
                        ).permitAll()
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
                                "/v1/production-records/**",
                                "/v1/attendance/**",
                                "/v1/feedback/approve/**",
                                "/v1/feedback/all/**",
                                "/v1/feedback/delete/**",
                                "/v1/bookings/tenant/**",
                                "/v1/bookings/{requestId}/approve",
                                "/v1/bookings/{requestId}/reject"
                        ).permitAll()
                        .requestMatchers("/v1/feedback", "/v1/bookings/request", "/v1/bookings/user", "/v1/bookings/delete").authenticated()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tenantFilter, JwtFilter.class)
                .build();
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

