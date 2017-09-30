package com.icthh.xm.ms.dashboard.repository;

import com.icthh.xm.ms.dashboard.domain.Profile;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the Profile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {

    @Query("select distinct profile from Profile profile left join fetch profile.dashboards")
    List<Profile> findAllWithEagerRelationships();

    List<Profile> findByUserKey(String userKey);

    @Query("select profile from Profile profile left join fetch profile.dashboards where profile.id =:id")
    Profile findOneWithEagerRelationships(@Param("id") Long id);

}
