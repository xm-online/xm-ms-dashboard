package com.icthh.xm.ms.dashboard.domain.converter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Converter
public class MapToStringConverter implements AttributeConverter<Map<String, Object>, String> {

    ObjectMapper mapper = JsonMapper.builder().build();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> data) {
        String value = "";
        try {
            value = mapper.writeValueAsString(data != null ? data : new HashMap<>());
        } catch (JacksonException e) {
            log.warn("Error during JSON to String converting", e);
        }
        return value;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String data) {
        Map<String, Object> mapValue = new HashMap<>();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
        try {
            mapValue = mapper.readValue(StringUtils.isNoneBlank(data) ? data : "{}", typeRef);
        } catch (JacksonException e) {
            log.warn("Error during String to JSON converting", e);
        }
        return mapValue;
    }

}
