package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.domain.UiData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UiDataRepository extends ResourceRepository<UiData, Long>, JpaRepository<UiData, Long> {

    UiData findResourceById(Long id);

    Page<UiData> findAllByTypeKeyAndOwner(String typeKey, String owner, Pageable pageable);
}
