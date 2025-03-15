package com.icthh.xm.ms.dashboard.service.dto;

import com.icthh.xm.ms.dashboard.domain.UiData;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
public class UiDataDto {

    private Long id;
    private String owner;
    private String typeKey;
    private Map<String, Object> data = new HashMap<>();
    private Instant createdDate = Instant.now();
    private Instant updateDate = Instant.now();

    public UiDataDto() {
    }

    public UiDataDto(UiData uiData) {
        this.id = uiData.getId();
        this.owner = uiData.getOwner();
        this.typeKey = uiData.getTypeKey();
        this.data = uiData.getData();
        this.createdDate = uiData.getCreatedDate();
        this.updateDate = uiData.getUpdateDate();
    }

    public UiData toEntity() {
        UiData uiData = new UiData();
        uiData.setId(this.id);
        uiData.setOwner(this.owner);
        uiData.setTypeKey(this.typeKey);
        uiData.setData(this.data);
        uiData.setCreatedDate(Instant.now());
        uiData.setUpdateDate(Instant.now());
        return uiData;
    }

    public void updateEntity(UiData uiData) {
        uiData.setTypeKey(this.typeKey);
        uiData.setData(this.data);
        uiData.setUpdateDate(Instant.now());
    }

}
