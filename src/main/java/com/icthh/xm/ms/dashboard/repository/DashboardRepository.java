package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Dashboard entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DashboardRepository extends JpaRepository<Dashboard,Long> {
    
}
