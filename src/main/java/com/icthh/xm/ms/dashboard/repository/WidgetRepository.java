package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

public interface WidgetRepository extends ResourceRepository<Widget, Long> {

    Widget save(Widget widget);

    Widget saveAndFlush(Widget widget);

    Optional<Widget> findById(Long id);

    void deleteById(Long id);

    Page<Map<String, Object>> findAllAudits(Pageable pageable);

    Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable);
}
