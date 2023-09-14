package com.icthh.xm.ms.dashboard.domain;

import com.icthh.xm.ms.dashboard.listener.CustomRevisionEntityListener;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RevInfo)) return false;
        RevInfo revInfo = (RevInfo) o;
        return Objects.equals(getRev(), revInfo.getRev()) && Objects.equals(getRevtstmp(), revInfo.getRevtstmp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRev(), getRevtstmp());
    }

    @Override
    public String toString() {
        return "RefInfo{" +
            "rev=" + rev +
            ", revtstmp=" + revtstmp +
            '}';
    }
}
