package ch.floaty.config;

import ch.floaty.infrastructure.SessionTokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static java.util.Arrays.asList;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private SecurityFilterChain filterChain;
    @Value("${cors.allowedOrigins}") String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SessionTokenFilter sessionTokenFilter) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource()).and()
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/auth/logout").authenticated()
                                .antMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterBefore(sessionTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<SessionTokenFilter> tenantFilterRegistration(SessionTokenFilter filter) {
        FilterRegistrationBean<SessionTokenFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(asList(corsAllowedOrigins.split(",")));
        configuration.setAllowedMethods(asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(asList("Content-Type", "Authorization", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
