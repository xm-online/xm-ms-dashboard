package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.criteria.internal.SimpleAuditExpression;
import org.hibernate.envers.query.internal.property.EntityPropertyName;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;
import org.springframework.data.domain.*;
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
        Sort.Order order = getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .add(AuditEntity.property("id").eq(id))
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        Long totalCountElements = (Long) auditReader.createQuery().forRevisionsOfEntity(Widget.class, false,true)
            .addProjection(AuditEntity.revisionNumber().count())
            .getSingleResult();
        return getResult(auditQuery, pageable, totalCountElements);
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        Sort.Order order = getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        Long totalCountElements = (Long) auditReader.createQuery().forRevisionsOfEntity(Widget.class, false,true)
            .addProjection(AuditEntity.revisionNumber().count())
            .getSingleResult();
        return getResult(auditQuery, pageable, totalCountElements);
    }

    private Sort.Order getPageOrder(Pageable pageable) {
        Sort.Order order = pageable.getSortOr(Sort.by("originalId.revision.revtstmp").ascending()).stream().findFirst().get();
        String orderProperty = order.getProperty();
        if ("revtstmp".equals(orderProperty) || "rev".equals(orderProperty)) {
            order = new Sort.Order(order.getDirection(), "originalId.revision." + orderProperty, order.getNullHandling());
        }
        return order;
    }

    private Page<Map<String, Object>> getResult(AuditQuery auditQuery, Pageable pageable, Long totalCountElements) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object entry : auditQuery.getResultList()) {
            Object[] row = (Object[]) entry;
            Map<String, Object> resultEntry = new HashMap<>();
            resultEntry.put("audit", row[0]);
            resultEntry.put("revInfo", row[1]);
            resultEntry.put("operation", row[2]);
            result.add(resultEntry);
        }
        return new PageImpl<>(result, pageable, totalCountElements);
    }

    @Override
    public Object findResourceById(Object id) {
        return defaultWidgetRepository.findResourceById(id);
    }
}
