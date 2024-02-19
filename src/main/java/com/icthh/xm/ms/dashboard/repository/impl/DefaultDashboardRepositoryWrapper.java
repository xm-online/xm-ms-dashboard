package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.Widget;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.internal.property.EntityPropertyName;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@RequiredArgsConstructor
@Repository
@Slf4j
public class DefaultDashboardRepositoryWrapper implements DashboardRepository {

    private final DefaultDashboardRepository defaultDashboardRepository;
    private final AuditReader auditReader;

    @Override
    public Dashboard findOneById(Long id) {
        return defaultDashboardRepository.findOneById(id);
    }

    @Override
    public Dashboard save(Dashboard dashboard) {
        return defaultDashboardRepository.save(dashboard);
    }

    @Override
    public Dashboard saveAndFlush(Dashboard dashboard) {
        return defaultDashboardRepository.saveAndFlush(dashboard);
    }

    @Override
    public void deleteById(Long id) {
        defaultDashboardRepository.deleteById(id);
    }

    @Override
    public List<Dashboard> findAll() {
        return defaultDashboardRepository.findAll();
    }

    @Override
    public void deleteAll(Iterable<? extends Dashboard> entities) {
        defaultDashboardRepository.deleteAll(entities);
    }

    @Override
    public List<Dashboard> findAllById(Set<Long> ids) {
        return defaultDashboardRepository.findAllById(ids);
    }

    @Override
    public <S extends Dashboard> List<S> saveAll(Iterable<S> entities) {
        return defaultDashboardRepository.saveAll(entities);
    }

    @Override
    public Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable) {
        Sort.Order order = getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Dashboard.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .add(AuditEntity.property("id").eq(id))
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        Long totalCountElements = (Long) auditReader.createQuery().forRevisionsOfEntity(Dashboard.class, false,true)
            .addProjection(AuditEntity.revisionNumber().count())
            .getSingleResult();
        return getResult(auditQuery, pageable, totalCountElements);
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        Sort.Order order = getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Dashboard.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        Long totalCountElements = (Long) auditReader.createQuery().forRevisionsOfEntity(Dashboard.class, false,true)
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
        return defaultDashboardRepository.findResourceById(id);
    }
}
