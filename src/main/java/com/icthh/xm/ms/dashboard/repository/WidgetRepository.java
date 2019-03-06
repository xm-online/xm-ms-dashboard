package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Widget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WidgetRepository extends JpaRepository<Widget,Long>, ResourceRepository {

    @Override
    Object findById(Object id);
}
