package com.icthh.xm.ms.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.icthh.xm.ms.dashboard.listener.CustomRevisionEntityListener;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * Dashboard is a user web page which collates information about a business via set of widgets.
 */
@Entity
@Table(name = "revinfo")
@RevisionEntity(CustomRevisionEntityListener.class)
public class RevInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @RevisionNumber
    @Column(name = "REV", nullable = false)
    private Long rev;

    /**
     * Dashboard display name.
     */
    @NotNull
    @RevisionTimestamp
    @Column(name = "REVTSTMP", nullable = false)
    private Date revtstmp;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    private String lastModifiedBy;

    public Long getRev() {
        return rev;
    }

    public void setRev(Long rev) {
        this.rev = rev;
    }

    public Date getRevtstmp() {
        return revtstmp;
    }

    public void setRevtstmp(Date revtstmp) {
        this.revtstmp = revtstmp;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RevInfo)) return false;
        RevInfo revInfo = (RevInfo) o;
        return Objects.equals(getRev(), revInfo.getRev()) && Objects.equals(getRevtstmp(), revInfo.getRevtstmp()) && Objects.equals(getLastModifiedBy(), revInfo.getLastModifiedBy());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRev(), getRevtstmp(), getLastModifiedBy());
    }

    @Override
    public String toString() {
        return "RevInfo{" +
            "rev=" + rev +
            ", revtstmp=" + revtstmp +
            ", lastModifiedBy='" + lastModifiedBy + '\'' +
            '}';
    }
}
