package com.icthh.xm.ms.dashboard.repository.impl;

import static java.util.stream.Collectors.toList;

import com.icthh.xm.ms.dashboard.domain.Dashboard;
import com.icthh.xm.ms.dashboard.mapper.DashboardMapper;
import com.icthh.xm.ms.dashboard.repository.DashboardRepository;
import com.icthh.xm.ms.dashboard.repository.IdRepository;
import com.icthh.xm.ms.dashboard.service.dto.DashboardDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j
public class ConfigDashboardRepository implements DashboardRepository {

    private final ConfigDashboardRefreshableRepository refreshableRepository;
    private final DashboardMapper dashboardMapper;
    private final IdRepository idRepository;

    @Override
    public Dashboard findOneById(Long id) {
        List<DashboardDto> dashboards = refreshableRepository.getDashboards();
        return dashboards.stream()
            .filter(dashboard -> dashboard.getId().equals(id))
            .map(dashboardMapper::toFullEntity)
            .findAny()
            .orElse(null);
    }

    @Override
    public Dashboard save(Dashboard dashboard) {
        validate(dashboard);

        Long oldDashboardId = dashboard.getId();

        if (oldDashboardId == null) {
            dashboard.setId(idRepository.getNextId());
        }

        dashboard.getWidgets().forEach(widget -> {
            if (widget.getId() == null) {
                widget.setId(idRepository.getNextId());
            }
        });

        if (oldDashboardId != null) {
            Dashboard oldDashboard = findOneById(oldDashboardId);
            if (oldDashboard != null) {
                if (CollectionUtils.isEmpty(dashboard.getWidgets())) {
                    dashboard.setWidgets(oldDashboard.getWidgets());
                } else {
                    dashboard.getWidgets().addAll(oldDashboard.getWidgets());
                }

                if (!oldDashboard.getTypeKey().equals(dashboard.getTypeKey())) {
                    deleteById(oldDashboardId);
                }
            }
        }

        return refreshableRepository.saveDashboard(dashboard);
    }

    @Override
    public Dashboard saveAndFlush(Dashboard dashboard) {
        return save(dashboard);
    }

    @Override
    public void deleteById(Long id) {
        Dashboard dashboard = findOneById(id);
        if (dashboard != null) {
            refreshableRepository.deleteDashboard(dashboard);
        }
    }

    @Override
    public List<Dashboard> findAll() {
        return refreshableRepository.getDashboards().stream()
            .map(dashboardMapper::toFullEntity)
            .collect(toList());
    }

    @Override
    public void deleteAll(Iterable<? extends Dashboard> toDelete) {
        toDelete.forEach(dashboard -> deleteById(dashboard.getId()));
    }

    @Override
    public List<Dashboard> findAllById(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return refreshableRepository.getDashboards()
            .stream()
            .filter(dashboard -> ids.contains(dashboard.getId()))
            .map(dashboardMapper::toFullEntity)
            .collect(toList());
    }

    @Override
    public List<Dashboard> saveAll(Iterable<Dashboard> dashboardEntities) {
        List<Dashboard> saved = new ArrayList<>();
        dashboardEntities.forEach(dashboard -> {
            saved.add(save(dashboard));
        });
        return saved;
    }

    @Override
    public Dashboard findResourceById(Long id) {
        return findOneById(id);
    }

    private void validate(Dashboard dashboard) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<Dashboard>> constraintViolations = validator.validate(dashboard);
            if (!constraintViolations.isEmpty()) {
                throw new ValidationException(constraintViolations.toString());
            }
        }
    }

    @Override
    public Page<Map<String, Object>> findAllAudits(Pageable pageable) {
        throw new NotImplementedException();
    }

    @Override
    public Page<Map<String, Object>> findAuditsById(Long id, Pageable pageable) {
        throw new NotImplementedException();
    }
}
