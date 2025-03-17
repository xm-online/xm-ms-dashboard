package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.lep.api.LepKeyResolver;
import com.icthh.xm.lep.api.LepMethod;
import com.icthh.xm.ms.dashboard.service.dto.UiDataDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UiDataTypeKeyResolver implements LepKeyResolver {

    @Override
    public List<String> segments(LepMethod method) {
        return List.of(method.getParameter("uiDataDto", UiDataDto.class).getTypeKey());
    }
}
