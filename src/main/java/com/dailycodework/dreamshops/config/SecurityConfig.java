package com.dailycodework.dreamshops.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.dailycodework.dreamshops.security.jwt.AuthEntryPointJwt;
import com.dailycodework.dreamshops.security.jwt.AuthTokenFilter;
import com.dailycodework.dreamshops.security.user.ShopUserDetailsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private ShopUserDetailsService userDetailsService;
    @Autowired
    private AuthEntryPointJwt authEntryPoint;

    private static final List<String> PUBLIC_URLS = List.of(
            "/api/v1/auth/login", "/api/v1/auth/signup", "/api/v1/auth/logout",
            "/api/v1/products/**");
    private static final List<String> SECURED_URLS = List
            .of("/api/v1/cartItems/**", "/api/v1/users/**");

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authEntryPoint));
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()
                .requestMatchers(SECURED_URLS.toArray(String[]::new))
                .authenticated().anyRequest().authenticated());

        http.authenticationProvider(daoAuthenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
                userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
