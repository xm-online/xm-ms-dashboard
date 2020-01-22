package com.icthh.xm.ms.dashboard.domain;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;

import com.icthh.xm.ms.dashboard.domain.converter.MapToStringConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Dashboard is a user web page which collates information about a business via set of widgets.
 */
@ApiModel(description = "Dashboard is a user web page which collates information about a business via set of widgets.")
@Entity
@Table(name = "dashboard")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Dashboard implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    /**
     * Dashboard display name.
     */
    @NotNull
    @ApiModelProperty(value = "Dashboard display name.", required = true)
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Dashboard owner user key.
     */
    @NotNull
    @ApiModelProperty(value = "Dashboard owner user key.", required = true)
    @Column(name = "owner", nullable = false)
    private String owner;

    /**
     * Dashboard type key.
     */
    @NotNull
    @ApiModelProperty(value = "Dashboard type key.", required = true)
    @Column(name = "type_key", nullable = false)
    private String typeKey;

    /**
     * Dashboard layout configuration. Format on API level is JSON. Format on Java level is Map<String, Object>
     */
    @ApiModelProperty(value = "Dashboard layout configuration. Format on API level is JSON. Format on Java level is Map<String, Object>")
    @Convert(converter = MapToStringConverter.class)
    @Column(name = "layout")
    private Map<String, Object> layout = new HashMap<>();

    /**
     * Particular dashboard configuration that is not related to the layout. Format on API level is JSON. Format on Java
     * level is Map<String, Object>
     */
    @ApiModelProperty(value = "Particular dashboard configuration that is not related to the layout. Format on API level is JSON. Format on Java level is Map<String, Object>")
    @Convert(converter = MapToStringConverter.class)
    @Column(name = "config")
    private Map<String, Object> config = new HashMap<>();

    /**
     * Public dashboard could be shown for not authorized users. At same time, only public widgets should be shown.
     */
    @ApiModelProperty(value = "Public dashboard could be shown for not authorized users. At same time, only public widgets should be shown.")
    @Column(name = "is_public")
    private Boolean isPublic;

    @OneToMany(mappedBy = "dashboard", cascade = {PERSIST, MERGE, REMOVE})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Widget> widgets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Dashboard name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public Dashboard owner(String owner) {
        this.owner = owner;
        return this;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Map<String, Object> getLayout() {
        return layout;
    }

    public Dashboard layout(Map<String, Object> layout) {
        this.layout = layout;
        return this;
    }

    public void setLayout(Map<String, Object> layout) {
        this.layout = layout;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public Dashboard config(Map<String, Object> config) {
        this.config = config;
        return this;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public Boolean isIsPublic() {
        return isPublic;
    }

    public Dashboard isPublic(Boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Set<Widget> getWidgets() {
        return widgets;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public Dashboard typeKey(String typeKey) {
        this.typeKey = typeKey;
        return this;
    }

    public void setTypeKey(String typeKey) {
        this.typeKey = typeKey;
    }

    public Dashboard widgets(Set<Widget> widgets) {
        this.widgets = widgets;
        return this;
    }

    public Dashboard addWidgets(Widget widget) {
        this.widgets.add(widget);
        widget.setDashboard(this);
        return this;
    }

    public Dashboard addWidgets(Set<Widget> widgets) {
        this.widgets.addAll(widgets);
        widgets.forEach(widget -> widget.setDashboard(this));
        return this;
    }

    public Dashboard removeWidgets(Widget widget) {
        this.widgets.remove(widget);
        widget.setDashboard(null);
        return this;
    }

    public void setWidgets(Set<Widget> widgets) {
        this.widgets = widgets;
    }

    public <T> void updateDashboardReference(Collection<T> objects, BiConsumer<T, Dashboard> dashboardSetter) {
        if (CollectionUtils.isNotEmpty(objects)) {
            objects.forEach(object -> dashboardSetter.accept(object, this));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dashboard dashboard = (Dashboard) o;
        if (dashboard.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), dashboard.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Dashboard{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", owner='" + getOwner() + "'" +
            ", typeKey='" + getTypeKey() + "'" +
            ", layout='" + getLayout() + "'" +
            ", config='" + getConfig() + "'" +
            ", isPublic='" + isIsPublic() + "'" +
            "}";
    }
}
