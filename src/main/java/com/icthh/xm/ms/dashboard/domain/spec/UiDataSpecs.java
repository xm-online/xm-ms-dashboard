package com.icthh.xm.ms.dashboard.domain.spec;

import com.icthh.xm.commons.domain.BaseSpecification;
import com.icthh.xm.commons.domain.DefinitionSpec;
import com.icthh.xm.commons.domain.FormSpec;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class UiDataSpecs implements BaseSpecification {

    private Collection<UiDataSpec> items;
    private List<DefinitionSpec> definitions = null;
    private List<FormSpec> forms = null;

}
