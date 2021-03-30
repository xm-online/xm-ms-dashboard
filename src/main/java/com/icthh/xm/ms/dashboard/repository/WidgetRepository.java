package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.Widget;
import java.util.Optional;

public interface WidgetRepository extends ResourceRepository {

    Widget save(Widget widget);

    Widget saveAndFlush(Widget widget);

    Optional<Widget> findById(Long id);

    void deleteById(Long id);
}
