package com.icthh.xm.ms.dashboard.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * User's Dashboard container. Here user can customize which dashboards should be displayed. If user does not define any
 * dashboard then default one will be returned from the config.
 */
@Schema(description = "User's Dashboard container. Here user can customize which dashboards should be displayed. If user does not define any dashboard then default one will be returned from the config.")
@Entity
@Table(name = "profile")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "user_key", nullable = false)
    private String userKey;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "profile_dashboards",
        joinColumns = @JoinColumn(name = "profiles_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "dashboards_id", referencedColumnName = "id"))
    private Set<Dashboard> dashboards = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserKey() {
        return userKey;
    }

    public Profile userKey(String userKey) {
        this.userKey = userKey;
        return this;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Set<Dashboard> getDashboards() {
        return dashboards;
    }

    public Profile dashboards(Set<Dashboard> dashboards) {
        this.dashboards = dashboards;
        return this;
    }

    public Profile addDashboards(Dashboard dashboard) {
        this.dashboards.add(dashboard);
        return this;
    }

    public Profile removeDashboards(Dashboard dashboard) {
        this.dashboards.remove(dashboard);
        return this;
    }

    public void setDashboards(Set<Dashboard> dashboards) {
        this.dashboards = dashboards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Profile profile = (Profile) o;
        if (profile.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), profile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Profile{" +
            "id=" + getId() +
            ", userKey='" + getUserKey() + "'" +
            "}";
    }
}
