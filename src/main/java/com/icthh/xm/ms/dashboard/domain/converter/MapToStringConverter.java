package com.icthh.xm.ms.dashboard.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> data) {
        String value = "";
        try {
            value = mapper.writeValueAsString(data != null ? data : new HashMap<>());
        } catch (JsonProcessingException e) {
            log.warn("Error during JSON to String converting", e);
        }
        return value;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String data) {
        Map<String, Object> mapValue = new HashMap<>();
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
        try {
            mapValue = mapper.readValue(StringUtils.isNoneBlank(data) ? data : "{}", typeRef);
        } catch (IOException e) {
            log.warn("Error during String to JSON converting", e);
        }
        return mapValue;
    }

}
