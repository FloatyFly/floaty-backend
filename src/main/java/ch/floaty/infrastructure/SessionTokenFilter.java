package ch.floaty.infrastructure;

import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.SessionTokenService;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class SessionTokenFilter extends OncePerRequestFilter {

    private final SessionTokenService sessionTokenService;
    private static final String sessionTokenCookieName = "sessionToken";

    public SessionTokenFilter(SessionTokenService sessionTokenService) {
        this.sessionTokenService = sessionTokenService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain)
            throws ServletException, IOException {

        String sessionToken = extractSessionTokenFromCookie(request);
        if (sessionToken == null) {
            chain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().startsWith("/auth/")) {
            logger.debug("Bypassing security filter for URI: " + request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        SessionToken validatedSessionToken;
        try {
            validatedSessionToken = sessionTokenService.validateToken(sessionToken);
        } catch (ResponseStatusException exception) {
            response.setStatus(exception.getStatus().value());
            response.getWriter().write(Objects.requireNonNull(exception.getReason()));
            response.getWriter().flush();
            return;
        }

        sessionTokenService.renewToken(validatedSessionToken);

        User user = validatedSessionToken.getUser();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private String extractSessionTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (sessionTokenCookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}