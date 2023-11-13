package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Widget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DefaultWidgetRepository extends JpaRepository<Widget,Long> {

    Object findResourceById(Object id);
}
