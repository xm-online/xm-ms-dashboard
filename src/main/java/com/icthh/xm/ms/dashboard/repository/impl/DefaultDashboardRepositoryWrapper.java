package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.internal.property.EntityPropertyName;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

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
        Sort.Order order = ServiceUtil.getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Dashboard.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .add(AuditEntity.property("id").eq(id))
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        Long totalCount = (Long) auditReader.createQuery().forRevisionsOfEntity(Dashboard.class, false, true)
            .add(AuditEntity.property("id").eq(id))
            .addProjection(AuditEntity.revisionNumber().count())
            .getSingleResult();
        return ServiceUtil.getResult(auditQuery.getResultList(), pageable, totalCount);
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        Sort.Order order = ServiceUtil.getPageOrder(pageable);
        AuditQuery auditQuery = auditReader.createQuery()
            .forRevisionsOfEntity(Dashboard.class,false, true)
            .setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize())
            .addOrder(new PropertyAuditOrder(null, new EntityPropertyName(order.getProperty()), order.isAscending()));
        Long totalCount = (Long) auditReader.createQuery().forRevisionsOfEntity(Dashboard.class, false, true)
            .addProjection(AuditEntity.revisionNumber().count())
            .getSingleResult();
        return ServiceUtil.getResult(auditQuery.getResultList(), pageable, totalCount);
    }

    @Override
    public Object findResourceById(Object id) {
        return defaultDashboardRepository.findResourceById(id);
    }
}
