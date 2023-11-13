package com.icthh.xm.ms.dashboard.domain;

import com.icthh.xm.ms.dashboard.listener.CustomRevisionEntityListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "revinfo")
@RevisionEntity(CustomRevisionEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
