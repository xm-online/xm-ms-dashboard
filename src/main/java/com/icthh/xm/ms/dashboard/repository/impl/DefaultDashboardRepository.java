package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the Dashboard entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DefaultDashboardRepository extends JpaRepository<Dashboard, Long>, DashboardRepository {

    Dashboard findOneById(Long id);

    void deleteAllByIdIn(Collection<Long> ids);

}
