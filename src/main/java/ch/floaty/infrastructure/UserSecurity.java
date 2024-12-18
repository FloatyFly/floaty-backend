package ch.floaty.infrastructure;

import ch.floaty.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserSecurity {

    public boolean hasUserIdOrAdmin(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = (User) authentication.getPrincipal();

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        boolean hasUserId = user.getId().equals(userId);
        boolean hasUserIdOrIsAdmin = isAdmin || hasUserId;
        if (!hasUserIdOrIsAdmin) {
            log.info("Unauthorized request: hasUserIdOrIsAdmin is false.");
        }
        return hasUserIdOrIsAdmin;
    }
}
