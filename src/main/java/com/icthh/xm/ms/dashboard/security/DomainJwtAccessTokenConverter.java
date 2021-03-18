package com.icthh.xm.ms.dashboard.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import static com.icthh.xm.ms.dashboard.config.Constants.*;

/**
 * Overrides to get token tenant.
 */
public class DomainJwtAccessTokenConverter extends JwtAccessTokenConverter {

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        final OAuth2Authentication authentication = super.extractAuthentication(map);
        final Map<String, Object> details = new HashMap<>();
        details.put(AUTH_TENANT_KEY, map.get(AUTH_TENANT_KEY));
        details.put(AUTH_XM_TOKEN_KEY, map.get(AUTH_XM_TOKEN_KEY));
        details.put(AUTH_XM_COOKIE_KEY, map.get(AUTH_XM_COOKIE_KEY));
        details.put(AUTH_XM_USERID_KEY, map.get(AUTH_XM_USERID_KEY));
        details.put(AUTH_XM_LOCALE, map.get(AUTH_XM_LOCALE));
        details.put(AUTH_USER_KEY, map.get(AUTH_USER_KEY));
        details.put(AUTH_ADDITIONAL_DETAILS, map.get(AUTH_ADDITIONAL_DETAILS));

        authentication.setDetails(details);

        return authentication;
    }
}
