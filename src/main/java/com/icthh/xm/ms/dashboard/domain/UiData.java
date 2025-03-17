package com.icthh.xm.ms.dashboard.domain;

import com.icthh.xm.ms.dashboard.domain.converter.MapToStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table(name = "ui_data")
@ToString
public class UiData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "data_key", nullable = false)
    private String key;

    @NotNull
    @Column(name = "owner", nullable = false)
    private String owner;

    @NotNull
    @Column(name = "type_key", nullable = false)
    private String typeKey;

    @Convert(converter = MapToStringConverter.class)
    @Column(name = "data")
    private Map<String, Object> data = new HashMap<>();

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate = Instant.now();

    @LastModifiedDate
    @Column(name = "update_date")
    private Instant updateDate = Instant.now();

    @Column(name = "version")
    @Version
    private Integer version;

}
