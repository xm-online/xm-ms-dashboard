package com.icthh.xm.ms.dashboard.domain.idresolver;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.commons.exceptions.ErrorConstants;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Dashboard object ID resolver.
 * Resolves Calendar object from database bi ID provided in JSON.
 * see https://stackoverflow.com/questions/41989906/jackson-referencing-an-object-as-a-property
 */
@Slf4j
@AllArgsConstructor
@Component
@Scope("prototype")
public class DashboardIdResolver extends SimpleObjectIdResolver {

    DashboardRepository repository;

    public DashboardIdResolver() {
        log.debug("Calendar object id resolver inited");
    }

    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey id) {
        Object entity = repository.findOneById((Long) id.key);

        if (entity == null) {
            throw new BusinessException(ErrorConstants.ERR_NOTFOUND, "Can not resolve Dashboard by ID: " + id.key);
        }

        return entity;
    }

    @Override
    public ObjectIdResolver newForDeserialization(final Object context) {
        return new DashboardIdResolver(repository);
    }

}
