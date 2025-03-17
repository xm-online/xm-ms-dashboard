package com.icthh.xm.ms.dashboard.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.icthh.xm.commons.exceptions.BusinessException;
import com.icthh.xm.commons.permission.annotation.PrivilegeDescription;
import com.icthh.xm.ms.dashboard.service.UiDataService;
import com.icthh.xm.ms.dashboard.service.dto.UiDataDto;
import com.icthh.xm.ms.dashboard.web.rest.util.PaginationUtil;
import com.icthh.xm.ms.dashboard.web.rest.util.RespContentUtil;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ui")
@RequiredArgsConstructor
public class UiDataResource {

    public static final String UI_DATA_PATH = "/api/ui/data";

    private final UiDataService uiDataService;

    @PostMapping("/data")
    @Timed
    @PreAuthorize("hasPermission({'uiData': #uiData}, 'UI_DATA.CREATE')")
    @PrivilegeDescription("Privilege to create a new uiData")
    public ResponseEntity<UiDataDto> createUiData(@RequestBody UiDataDto uiData) throws URISyntaxException {
        if (uiData.getId() != null) {
            throw new BusinessException("error.post.with.id", "A new uiData cannot already have an ID");
        }
        UiDataDto result = uiDataService.save(uiData);
        return ResponseEntity.created(new URI("/api/ui/data/" + result.getId())).body(result);
    }

    @PutMapping("/data")
    @Timed
    @PreAuthorize("hasPermission({'uiData': #uiData}, 'uiData', 'UI_DATA.UPDATE')")
    @PrivilegeDescription("Privilege to updates an existing uiData")
    public ResponseEntity<UiDataDto> updateUiData(@RequestBody UiDataDto uiData) {
        UiDataDto result = uiDataService.save(uiData);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/data")
    @Timed
    public ResponseEntity<List<UiDataDto>> getAllUiData(@ApiParam Pageable pageable,
                                                        @RequestParam(required = false) String typeKey,
                                                        @RequestParam(required = false) String key,
                                                        @RequestParam(required = false) String owner) {
        Page<UiDataDto> page = uiDataService.findAll(typeKey, key, owner, null, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, UI_DATA_PATH);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/data/own")
    @Timed
    @PreAuthorize("hasPermission({'typeKey': #typeKey}, 'data', 'UI_DATA.GET_LIST.OWNED')")
    @PrivilegeDescription("Privilege to get the uiData by typeKey then where user owner")
    public ResponseEntity<List<UiDataDto>> getOwnUiData(@ApiParam Pageable pageable,
                                                        @RequestParam(required = false) String typeKey,
                                                        @RequestParam(required = false) String key) {
        Page<UiDataDto> page = uiDataService.findAll(typeKey, key, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, UI_DATA_PATH);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/data/{id}")
    @Timed
    @PostAuthorize("hasPermission({'returnObject': returnObject.body}, 'UI_DATA.GET_LIST.ITEM')")
    @PrivilegeDescription("Privilege to get the uiData by id")
    public ResponseEntity<UiDataDto> getUiData(@PathVariable Long id) {
        UiDataDto uiData = uiDataService.findOne(id);
        return RespContentUtil.wrapOrNotFound(Optional.ofNullable(uiData));
    }

    @DeleteMapping("/data/{id}")
    @Timed
    @PreAuthorize("hasPermission({'id': #id}, 'data', 'UI_DATA.DELETE')")
    @PrivilegeDescription("Privilege to delete the uiData by id")
    public ResponseEntity<Void> deleteUiData(@PathVariable Long id) {
        uiDataService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/data/{typeKey}/{key}")
    @Timed
    @PreAuthorize("hasPermission({'typeKey': #typeKey, 'key': #key}, 'data', 'UI_DATA.GET_LIST.ITEM_BY_KEY')")
    public ResponseEntity<List<UiDataDto>> getUiDataByKey(@ApiParam Pageable pageable,
                                                          @PathVariable String typeKey,
                                                          @PathVariable String key) {
        Page<UiDataDto> page = uiDataService.findByKey(typeKey, key, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, UI_DATA_PATH);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
