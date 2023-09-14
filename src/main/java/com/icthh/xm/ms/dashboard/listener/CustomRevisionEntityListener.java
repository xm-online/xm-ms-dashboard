package com.icthh.xm.ms.dashboard.listener;

import com.icthh.xm.ms.dashboard.domain.RevInfo;
import org.hibernate.envers.RevisionListener;

import java.time.Instant;
import java.util.Date;

public class CustomRevisionEntityListener implements RevisionListener {
    public void newRevision(Object revisionEntity) {
        RevInfo customRevisionEntity =
            (RevInfo) revisionEntity;

        customRevisionEntity.setRevtstmp(new Date());
    }
}
