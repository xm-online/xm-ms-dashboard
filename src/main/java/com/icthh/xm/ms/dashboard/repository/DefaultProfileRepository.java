package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.DefaultProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data JPA repository for the DefaultProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DefaultProfileRepository extends JpaRepository<DefaultProfile,Long> {

    List<DefaultProfile> findByRoleKey(String roleKey);

}
