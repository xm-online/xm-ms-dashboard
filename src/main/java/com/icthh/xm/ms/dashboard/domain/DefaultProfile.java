package com.icthh.xm.ms.dashboard.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Default dashboard per role configuration. Tenant admin can configure it.
 */
@Schema(description = "Default dashboard per role configuration. Tenant admin can configure it.")
@Entity
@Table(name = "default_profile")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DefaultProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "role_key", nullable = false)
    private String roleKey;

    @ManyToOne(optional = false)
    @NotNull
    private Dashboard dashboard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public DefaultProfile roleKey(String roleKey) {
        this.roleKey = roleKey;
        return this;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public DefaultProfile dashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
        return this;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultProfile defaultProfile = (DefaultProfile) o;
        if (defaultProfile.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), defaultProfile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DefaultProfile{" +
            "id=" + getId() +
            ", roleKey='" + getRoleKey() + "'" +
            "}";
    }
}
