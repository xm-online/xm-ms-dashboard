package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Slf4j
public class DefaultWidgetRepositoryWrapper implements WidgetRepository {

    private final DefaultWidgetRepository defaultWidgetRepository;
    private final AuditReader auditReader;

    @Override
    public Widget save(Widget widget) {
        return defaultWidgetRepository.save(widget);
    }

    @Override
    public Widget saveAndFlush(Widget widget) {
        return defaultWidgetRepository.saveAndFlush(widget);
    }

    @Override
    public Optional<Widget> findById(Long id) {
        return defaultWidgetRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        defaultWidgetRepository.deleteById(id);
    }

    @Override
    public Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable) {
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .add(AuditEntity.property("id").eq(id));
        return getResult(auditQuery);
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize());
        return getResult(auditQuery);
    }

    private Page<Map<String, Object>> getResult(AuditQuery auditQuery) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object entry : auditQuery.getResultList()) {
            Object[] row = (Object[]) entry;
            Map<String, Object> resultEntry = new HashMap<>();
            resultEntry.put("audit", row[0]);
            resultEntry.put("revInfo", row[1]);
            resultEntry.put("operation", row[2]);
            result.add(resultEntry);
        }
        return new PageImpl<>(result);
    }

    @Override
    public Object findResourceById(Object id) {
        return defaultWidgetRepository.findResourceById(id);
    }
}
