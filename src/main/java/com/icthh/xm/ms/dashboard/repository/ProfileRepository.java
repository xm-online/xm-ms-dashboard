package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.commons.permission.access.repository.ResourceRepository;
import com.icthh.xm.ms.dashboard.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Profile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {

    List<Profile> findByUserKey(String userKey);

    @Query("select profile from Profile profile left join fetch profile.dashboards where profile.id =:id")
    Profile findOneWithEagerRelationships(@Param("id") Long id);

}
