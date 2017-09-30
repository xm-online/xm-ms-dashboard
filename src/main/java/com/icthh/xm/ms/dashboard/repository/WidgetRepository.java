package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.ms.dashboard.domain.Widget;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.List;


/**
 * Spring Data JPA repository for the Widget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WidgetRepository extends JpaRepository<Widget,Long> {

    /**
     * Finds Widgets by Dashboard.
     * @param id the Dashboard id
     * @return a list of widgets
     */
    List<Widget> findByDashboardId(Long id);

}
