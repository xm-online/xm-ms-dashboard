package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface DashboardRepository extends ResourceRepository {

    Dashboard findOneById(Long id);

    <S extends Dashboard> S save(S dashboard);

    Dashboard saveAndFlush(Dashboard dashboard);

    void deleteById(Long id);

    List<Dashboard> findAll();

    void deleteAll(Iterable<? extends Dashboard> entities);

    List<Dashboard> findAllById(Set<Long> ids);

    <S extends Dashboard> List<S> saveAll(Iterable<S> entities);

    Page<Map<String, Object>> findAllAudits(Pageable pageable);

    Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable);
}
