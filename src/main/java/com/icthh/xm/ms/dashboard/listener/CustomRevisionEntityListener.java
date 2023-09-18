package com.icthh.xm.ms.dashboard.listener;

import com.icthh.xm.ms.dashboard.domain.RevInfo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

@Slf4j
public class CustomRevisionEntityListener implements RevisionListener {
    public void newRevision(Object revisionEntity) {
        RevInfo customRevisionEntity =
            (RevInfo) revisionEntity;

        customRevisionEntity.setRevtstmp(new Date());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        customRevisionEntity.setLastModifiedBy(currentPrincipalName);
    }
}
