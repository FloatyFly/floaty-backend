package ch.floaty.config;

import ch.floaty.domain.SessionTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SessionTokenFilter sessionTokenFilter) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/auth/register", "/auth/login").permitAll()
                                .anyRequest().authenticated() // Protect all other endpoints
                )
                .addFilterBefore(sessionTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable(); // TODO: Disable CSRF for now (enable for production with appropriate configurations)

        return http.build();
    }
}

