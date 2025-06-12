package com.icthh.xm.ms.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.icthh.xm.ms.dashboard.domain.converter.MapToStringConverter;
import com.icthh.xm.ms.dashboard.domain.idresolver.DashboardIdResolver;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Widget is a web component intended to be used within dashboards. Widgets could communication with each other only via
 * event manager. Widget can be of particular type.
 */
@Schema(description = "Widget is a web component intended to be used within dashboards. Widgets could communication with each other only via event manager. Widget can be of particular type.")
@Entity
@Audited
@Table(name = "widget")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Widget extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * Widget unique web component selector. Examples: xm-widget-map, xm-widget-profile, xm-widget-last-viewed, etc.
     */
    @NotNull
    @Schema(description = "Widget unique web component selector. Examples: xm-widget-map, xm-widget-profile, xm-widget-last-viewed, etc.", required = true)
    @Column(name = "selector", nullable = false)
    private String selector;

    /**
     * Widget display name.
     */
    @NotNull
    @Schema(description = "Widget display name.", required = true)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Widget configuration in JSON format. Format on Java level is Map<String, Object>
     */
    @Schema(description = "Widget configuration in JSON format. Format on Java level is Map<String, Object>")
    @Convert(converter = MapToStringConverter.class)
    @Column(name = "config")
    private Map<String, Object> config = new HashMap<>();

    /**
     * Public widgets could be shown for not authorized users.
     */
    @Schema(description = "Public widgets could be shown for not authorized users.")
    @Column(name = "is_public")
    private Boolean isPublic;

    @ManyToOne
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver =
        DashboardIdResolver.class)
    @JsonIdentityReference(alwaysAsId = true)
    private Dashboard dashboard;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSelector() {
        return selector;
    }

    public Widget selector(String selector) {
        this.selector = selector;
        return this;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getName() {
        return name;
    }

    public Widget name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public Widget config(Map<String, Object> config) {
        this.config = config;
        return this;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Boolean isIsPublic() {
        return isPublic;
    }

    public Widget isPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public Widget dashboard(Dashboard dashboard) {
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
        Widget widget = (Widget) o;
        if (widget.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), widget.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Widget{" +
            "id=" + getId() +
            ", selector='" + getSelector() + "'" +
            ", name='" + getName() + "'" +
            ", config='" + getConfig() + "'" +
            ", isPublic='" + isIsPublic() + "'" +
            "}";
    }
}
