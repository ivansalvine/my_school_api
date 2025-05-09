package com.souldev.security.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souldev.security.security.jwt.JwtEntryPoint;
import com.souldev.security.security.jwt.JwtProvider;
import com.souldev.security.security.jwt.JwtTokenFilter;
import com.souldev.security.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MainSecurity {

    private static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/images/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**"
    };

    @Autowired
    private JwtEntryPoint jwtEntryPoint;

    private final JwtProvider jwtProvider;
    private final UserService userDetailsService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MainSecurity(JwtProvider jwtProvider, UserService userDetailsService, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtProvider, userDetailsService, objectMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(PUBLIC_URLS).permitAll()
                .antMatchers("/admin/**").hasAuthority("ROLE_SUPER_ADMIN")
                .antMatchers("/direction/**").hasAnyAuthority("ROLE_DIRECTOR", "ROLE_SUPER_ADMIN")
                .antMatchers("/teacher/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_DIRECTOR", "ROLE_SUPER_ADMIN")
                .antMatchers("/student/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_DIRECTOR", "ROLE_SUPER_ADMIN")
                .antMatchers("/devoirs/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_DIRECTOR", "ROLE_SUPER_ADMIN")
                .antMatchers("/notes/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_DIRECTOR", "ROLE_SUPER_ADMIN")
                .antMatchers("/bulletins/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_DIRECTOR", "ROLE_SUPER_ADMIN")
                .antMatchers("/diplomes/**").hasAnyAuthority("ROLE_TEACHER", "ROLE_DIRECTOR", "ROLE_SUPER_ADMIN")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .headers().frameOptions().disable();

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3003"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}