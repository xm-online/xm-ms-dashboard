package com.icthh.xm.ms.dashboard.config.tenant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds information about incoming user.
 */
@RequiredArgsConstructor
@Getter
public class TenantInfo {

    private final String tenant;
    private final String userLogin;
    private final String userKey;
}
