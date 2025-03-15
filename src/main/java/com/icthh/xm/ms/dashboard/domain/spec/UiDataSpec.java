package com.icthh.xm.ms.dashboard.domain.spec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.icthh.xm.commons.domain.DataSpec;
import com.icthh.xm.commons.domain.HasInputDataForm;
import com.icthh.xm.commons.domain.HasInputDataSpec;
import com.icthh.xm.commons.domain.SpecificationItem;
import lombok.Data;

@Data
public class UiDataSpec implements DataSpec, SpecificationItem, HasInputDataSpec, HasInputDataForm {

    private String key;
    private String dataSpec;
    private String dataForm;
    private Boolean disableJsonSchemaValidation;

    @JsonIgnore
    public String getInputDataSpec() {
        return dataSpec;
    }

    @JsonIgnore
    public void setInputDataSpec(String spec) {
        dataSpec = spec;
    }

    @JsonIgnore
    public String getInputFormSpec() {
        return dataForm;
    }

    @JsonIgnore
    public void setInputFormSpec(String spec) {
        dataForm = spec;
    }

}
