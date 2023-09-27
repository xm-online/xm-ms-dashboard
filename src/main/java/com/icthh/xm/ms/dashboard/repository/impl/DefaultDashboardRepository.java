package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Dashboard;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the Dashboard entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DefaultDashboardRepository extends JpaRepository<Dashboard, Long> {

    Dashboard findOneById(Long id);

    void deleteAllByIdIn(Collection<Long> ids);

    Object findResourceById(Object id);
}
