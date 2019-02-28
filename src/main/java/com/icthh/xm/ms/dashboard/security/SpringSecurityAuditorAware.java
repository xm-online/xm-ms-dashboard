package com.icthh.xm.ms.dashboard.security;

import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.ms.dashboard.config.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private final XmAuthenticationContextHolder authenticationContextHolder;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(authenticationContextHolder.getContext().getLogin().orElse(Constants.SYSTEM_ACCOUNT));
    }
}
