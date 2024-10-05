package ch.floaty.domain;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SessionTokenFilter extends OncePerRequestFilter {

    private final SessionTokenService sessionTokenService;

    public SessionTokenFilter(SessionTokenService sessionTokenService) {
        this.sessionTokenService = sessionTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {

        String sessionToken = null;

        // Extract the sessionToken from cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("sessionToken".equals(cookie.getName())) {
                    sessionToken = cookie.getValue();
                    break;
                }
            }
        }

        // If sessionToken exists, validate it
        if (sessionToken != null) {
            SessionToken validatedSessionToken = sessionTokenService.validateToken(sessionToken);

            // If the session token is valid, set the security context
            if (validatedSessionToken != null) {
                User user = validatedSessionToken.getUser();

                // Create authentication token with the user's authorities
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());

                // Set request details in the authentication token
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }
}