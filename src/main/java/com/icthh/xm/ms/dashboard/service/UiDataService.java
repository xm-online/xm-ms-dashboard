package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.commons.exceptions.EntityNotFoundException;
import com.icthh.xm.commons.permission.annotation.FindWithPermission;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.commons.security.XmAuthenticationContext;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.utils.JsonValidationUtils;
import com.icthh.xm.ms.dashboard.domain.UiData;
import com.icthh.xm.ms.dashboard.domain.spec.UiDataSpec;
import com.icthh.xm.ms.dashboard.repository.UiDataRepository;
import com.icthh.xm.ms.dashboard.repository.impl.permitted.UiDataPermittedRepository;
import com.icthh.xm.ms.dashboard.service.dto.UiDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UiDataService {

    private final UiDataPermittedRepository permittedRepository;
    private final UiDataRepository uiDataRepository;
    private final XmAuthenticationContextHolder authContextHolder;
    private final UiDataSpecService uiDataSpecService;

    public UiDataDto save(UiDataDto uiDataDto) {
        UiDataSpec uiDataSpec = uiDataSpecService.getSpecByKeyAndTenant(uiDataDto.getTypeKey())
            .orElseThrow(() -> new BusinessException("UiData spec not found by type key " + uiDataDto.getTypeKey()));
        if (!Boolean.TRUE.equals(uiDataSpec.getDisableJsonSchemaValidation())) {
            JsonValidationUtils.assertJson(uiDataDto.getData(), uiDataSpec.getDataSpec());
        }

        if (uiDataDto.getId() == null) {
            UiData uiData = uiDataDto.toEntity();
            uiData.setOwner(getOwner());
            return new UiDataDto(uiDataRepository.save(uiData));
        }  else {
            UiData existingUiData = getUiData(uiDataDto);
            uiDataDto.updateEntity(existingUiData);
            return new UiDataDto(uiDataRepository.save(existingUiData));
        }
    }

    private UiData getUiData(UiDataDto uiDataDto) {
        return uiDataRepository.findById(uiDataDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("UiData for id " + uiDataDto.getId() + " not found"));
    }

    @Transactional(readOnly = true)
    @FindWithPermission("UI_DATA.GET_LIST")
    @PrivilegeDescription("Privilege to get all the UiData-s")
    public Page<UiDataDto> findAll(String typeKey, Pageable pageable) {
        String owner = getOwner();
        return uiDataRepository.findAllByTypeKeyAndOwner(typeKey, owner, pageable).map(UiDataDto::new);
    }

    private String getOwner() {
        XmAuthenticationContext context = authContextHolder.getContext();
        return context.getUserKey().orElse(context.getRequiredClientId());
    }

    @Transactional(readOnly = true)
    public Page<UiDataDto> findAll(String typeKey, String owner, String privilegeKey, Pageable pageable) {
        return permittedRepository.findAllByTypeKeyAndOwner(typeKey, owner, pageable, privilegeKey).map(UiDataDto::new);
    }

    @Transactional(readOnly = true)
    public UiDataDto findOne(Long id) {
        return uiDataRepository.findById(id).map(UiDataDto::new).orElse(null);
    }

    public void delete(Long id) {
        uiDataRepository.deleteById(id);
    }

}
