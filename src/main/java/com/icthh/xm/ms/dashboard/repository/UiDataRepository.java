package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.UiData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.data.jpa.domain.Specification.where;


public interface UiDataRepository extends ResourceRepository<UiData, Long>, JpaRepository<UiData, Long>,
    JpaSpecificationExecutor<UiData> {

    UiData findResourceById(Long id);

    default Page<UiData> findAllByTypeKeyAndKeyAndOwner(String typeKey, String key, String owner, Pageable pageable) {
        return this.findAll(
            where(eq("typeKey", typeKey))
                .and(eq("key", key))
                .and(eq("owner", owner)), pageable);
    }

    private Specification<UiData> eq(String fieldName, String fieldValue) {
        return (root, query, cb) -> isBlank(fieldValue) ?
            cb.conjunction() : cb.equal(root.get(fieldName), fieldValue);
    }

    Page<UiData> findByTypeKeyAndKey(String typeKey, String key, Pageable pageable);

}
