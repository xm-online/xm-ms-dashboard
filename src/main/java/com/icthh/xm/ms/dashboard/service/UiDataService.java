package com.icthh.xm.ms.dashboard.service;

import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.commons.exceptions.EntityNotFoundException;
import com.icthh.xm.commons.lep.LogicExtensionPoint;
import com.icthh.xm.commons.lep.spring.LepService;
import com.icthh.xm.commons.security.XmAuthenticationContext;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.commons.utils.JsonValidationUtils;
import com.icthh.xm.ms.dashboard.domain.UiData;
import com.icthh.xm.ms.dashboard.domain.spec.UiDataSpec;
import com.icthh.xm.ms.dashboard.repository.UiDataRepository;
import com.icthh.xm.ms.dashboard.repository.impl.permitted.UiDataPermittedRepository;
import com.icthh.xm.ms.dashboard.service.dto.UiDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@LepService(group = "service.uidata")
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class UiDataService {

    private final UiDataPermittedRepository permittedRepository;
    private final UiDataRepository uiDataRepository;
    private final XmAuthenticationContextHolder authContextHolder;
    private final UiDataSpecService uiDataSpecService;
    private final UiDataService self;

    @LogicExtensionPoint(value = "Save", resolver = UiDataTypeKeyResolver.class)
    public UiDataDto save(UiDataDto uiDataDto) {
        UiDataSpec uiDataSpec = uiDataSpecService.getSpecByKeyAndTenant(uiDataDto.getTypeKey())
            .orElseThrow(() -> new BusinessException("UiData spec not found by type key " + uiDataDto.getTypeKey()));
        if (!Boolean.TRUE.equals(uiDataSpec.getDisableJsonSchemaValidation())) {
            JsonValidationUtils.assertJson(uiDataDto.getData(), uiDataSpec.getDataSpec());
        }

        if (uiDataDto.getId() == null) {
            UiData uiData = uiDataDto.toEntity();
            uiData.setKey(isBlank(uiData.getKey()) ? randomUUID().toString() : uiData.getKey());
            uiData.setOwner(self.getOwner(uiData.getTypeKey()));
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
    @LogicExtensionPoint(value = "FindAllOwn", resolver = TypeKeyResolver.class)
    public Page<UiDataDto> findAll(String typeKey, String key, Pageable pageable) {
        String owner = self.getOwner(typeKey);
        return uiDataRepository.findAllByTypeKeyAndKeyAndOwner(typeKey, key, owner, pageable).map(UiDataDto::new);
    }

    @LogicExtensionPoint(value = "GetOwner", resolver = TypeKeyResolver.class)
    public String getOwner(String typeKey) {
        XmAuthenticationContext context = authContextHolder.getContext();
        return context.getUserKey().orElse(context.getRequiredClientId());
    }

    @Transactional(readOnly = true)
    @LogicExtensionPoint(value = "FindAll", resolver = TypeKeyResolver.class)
    public Page<UiDataDto> findAll(String typeKey, String key, String owner, String privilegeKey, Pageable pageable) {
        return permittedRepository.findAllByTypeKeyAndOwner(typeKey, key, owner, pageable, privilegeKey).map(UiDataDto::new);
    }

    @Transactional(readOnly = true)
    @LogicExtensionPoint(value = "FindOne")
    public UiDataDto findOne(Long id) {
        return uiDataRepository.findById(id).map(UiDataDto::new).orElse(null);
    }

    @LogicExtensionPoint("Delete")
    public void delete(Long id) {
        uiDataRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @LogicExtensionPoint(value = "FindByKey", resolver = TypeKeyResolver.class)
    public Page<UiDataDto> findByKey(String typeKey, String key, Pageable pageable) {
        return uiDataRepository.findByTypeKeyAndKey(typeKey, key, pageable).map(UiDataDto::new);
    }

}
