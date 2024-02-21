package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.WidgetRepository;
import com.icthh.xm.ms.dashboard.util.ServiceUtil;
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
        Sort.Order order = ServiceUtil.getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .add(AuditEntity.property("id").eq(id))
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        return ServiceUtil.getResult(auditQuery.getResultList(), pageable, getTotalCountAuditWidget());
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        Sort.Order order = ServiceUtil.getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Widget.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        return ServiceUtil.getResult(auditQuery.getResultList(), pageable, getTotalCountAuditWidget());
    }

    private Long getTotalCountAuditWidget() {
        return (Long) auditReader.createQuery().forRevisionsOfEntity(Widget.class, false, true)
            .addProjection(AuditEntity.revisionNumber().count())
            .getSingleResult();
    }

    @Override
    public Object findResourceById(Object id) {
        return defaultWidgetRepository.findResourceById(id);
    }
}
