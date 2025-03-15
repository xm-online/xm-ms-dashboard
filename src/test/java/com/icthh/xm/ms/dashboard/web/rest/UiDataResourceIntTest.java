package com.icthh.xm.ms.dashboard.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.icthh.xm.commons.i18n.error.web.ExceptionTranslator;
import com.icthh.xm.commons.security.XmAuthenticationContext;
import com.icthh.xm.commons.security.XmAuthenticationContextHolder;
import com.icthh.xm.ms.dashboard.AbstractSpringBootTest;
import com.icthh.xm.ms.dashboard.domain.UiData;
import com.icthh.xm.ms.dashboard.domain.spec.UiDataSpec;
import com.icthh.xm.ms.dashboard.domain.spec.UiDataSpecs;
import com.icthh.xm.ms.dashboard.repository.UiDataRepository;
import com.icthh.xm.ms.dashboard.service.UiDataSpecService;
import com.icthh.xm.ms.dashboard.service.dto.UiDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(authorities = "SUPER-ADMIN")
public class UiDataResourceIntTest extends AbstractSpringBootTest {

    private static final String DEFAULT_OWNER = "testOwner";
    private static final String UPDATED_OWNER = "updatedOwner";

    private static final String DEFAULT_TYPE_KEY = "testType";

    private static final Map<String, Object> DEFAULT_DATA = Map.of("key", "value");
    private static final Map<String, Object> UPDATED_DATA = Map.of("key", "updatedValue");

    @Autowired
    private UiDataRepository uiDataRepository;

    @Autowired
    private UiDataResource uiDataResource;

    @Autowired
    private UiDataSpecService uiDataSpecService;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private org.springframework.http.converter.json.MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @MockBean
    private XmAuthenticationContextHolder xmAuthenticationContextHolder;

    private MockMvc restUiDataMockMvc;

    private UiData uiData;

    /**
     * Create an entity for testing.
     */
    public static UiData createEntity() {
        UiData uiData = new UiData();
        uiData.setOwner(DEFAULT_OWNER);
        uiData.setTypeKey(DEFAULT_TYPE_KEY);
        uiData.setData(new HashMap<>(DEFAULT_DATA));
        return uiData;
    }

    @BeforeEach
    public void setup() {
        this.restUiDataMockMvc = MockMvcBuilders.standaloneSetup(uiDataResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter)
            .build();
        var authContext = mock(XmAuthenticationContext.class);
        when(authContext.getUserKey()).thenReturn(Optional.of(DEFAULT_OWNER));
        when(xmAuthenticationContextHolder.getContext()).thenReturn(authContext);
    }

    @BeforeEach
    public void initTest() {
        uiData = createEntity();
    }

    @Test
    @Transactional
    public void createUiData() throws Exception {
        addSpec();

        int databaseSizeBeforeCreate = uiDataRepository.findAll().size();

        UiDataDto uiDataDto = new UiDataDto(uiData);
        restUiDataMockMvc.perform(post("/api/ui/data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uiDataDto)))
            .andExpect(status().isCreated());

        List<UiData> uiDataList = uiDataRepository.findAll();
        assertThat(uiDataList).hasSize(databaseSizeBeforeCreate + 1);
        UiData testUiData = uiDataList.getLast();
        assertThat(testUiData.getOwner()).isEqualTo(DEFAULT_OWNER);
        assertThat(testUiData.getTypeKey()).isEqualTo(DEFAULT_TYPE_KEY);
        assertThat(testUiData.getData()).isEqualTo(DEFAULT_DATA);

        uiDataSpecService.onRefresh("/config/tenants/XM/dashboard/ui-data-spec.yml", null);
    }

    @Test
    @Transactional
    public void createUiDataWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = uiDataRepository.findAll().size();

        uiData.setId(1L);
        UiDataDto uiDataDto = new UiDataDto(uiData);

        restUiDataMockMvc.perform(post("/api/ui/data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uiDataDto)))
            .andExpect(status().isBadRequest());

        List<UiData> uiDataList = uiDataRepository.findAll();
        assertThat(uiDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUiData() throws Exception {
        uiDataRepository.saveAndFlush(uiData);

        restUiDataMockMvc.perform(get("/api/ui/data?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uiData.getId().intValue())))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].typeKey").value(hasItem(DEFAULT_TYPE_KEY)))
            .andExpect(jsonPath("$.[*].data.key").value(hasItem("value")));
    }

    @Test
    @Transactional
    public void getUiData() throws Exception {
        uiDataRepository.saveAndFlush(uiData);

        restUiDataMockMvc.perform(get("/api/ui/data/{id}", uiData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(uiData.getId().intValue()))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER))
            .andExpect(jsonPath("$.typeKey").value(DEFAULT_TYPE_KEY))
            .andExpect(jsonPath("$.data.key").value("value"));
    }

    @Test
    @Transactional
    public void getNonExistingUiData() throws Exception {
        restUiDataMockMvc.perform(get("/api/ui/data/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUiData() throws Exception {
        uiDataRepository.saveAndFlush(uiData);
        int databaseSizeBeforeUpdate = uiDataRepository.findAll().size();

        UiData updatedUiData = uiDataRepository.findById(uiData.getId()).get();
        updatedUiData.setOwner(UPDATED_OWNER);
        updatedUiData.setTypeKey(DEFAULT_TYPE_KEY);
        updatedUiData.setData(new HashMap<>(UPDATED_DATA));
        UiDataDto uiDataDto = new UiDataDto(updatedUiData);

        restUiDataMockMvc.perform(put("/api/ui/data")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(uiDataDto)))
            .andExpect(status().isBadRequest());

        addSpec();

        restUiDataMockMvc.perform(put("/api/ui/data")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(uiDataDto)))
            .andExpect(status().isOk());

        List<UiData> uiDataList = uiDataRepository.findAll();
        assertThat(uiDataList).hasSize(databaseSizeBeforeUpdate);
        UiData testUiData = uiDataRepository.findById(uiData.getId()).get();
        assertThat(testUiData.getOwner()).isEqualTo(DEFAULT_OWNER); //check that owner not updated
        assertThat(testUiData.getTypeKey()).isEqualTo(DEFAULT_TYPE_KEY);
        assertThat(testUiData.getData()).isEqualTo(UPDATED_DATA);


        uiDataSpecService.onRefresh("/config/tenants/XM/dashboard/ui-data-spec.yml", null);
    }

    private void addSpec() throws JsonProcessingException {
        UiDataSpecs uiDataSpecs = new UiDataSpecs();
        UiDataSpec uiDataSpec = new UiDataSpec();
        uiDataSpec.setKey(DEFAULT_TYPE_KEY);
        // language=json
        String dataSpec = """
        {
          "$schema":  "https://json-schema.org/draft-07/schema",
          "type": "object",
          "properties": {
            "key": {"type": "string"}
          }
        }
        """;
        uiDataSpec.setDataSpec(dataSpec);
        uiDataSpecs.setItems(List.of(uiDataSpec));
        var spec = new ObjectMapper(new YAMLFactory()).writeValueAsString(uiDataSpecs);
        uiDataSpecService.onRefresh("/config/tenants/XM/dashboard/ui-data-spec.yml", spec);
    }

    @Test
    @Transactional
    public void deleteUiData() throws Exception {
        uiDataRepository.saveAndFlush(uiData);
        int databaseSizeBeforeDelete = uiDataRepository.findAll().size();

        restUiDataMockMvc.perform(delete("/api/ui/data/{id}", uiData.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        List<UiData> uiDataList = uiDataRepository.findAll();
        assertThat(uiDataList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void getAllOwnedUiData() throws Exception {
        uiDataRepository.saveAndFlush(uiData);

        restUiDataMockMvc.perform(get("/api/ui/data/owned?typeKey=" + DEFAULT_TYPE_KEY))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].typeKey").value(hasItem(DEFAULT_TYPE_KEY)))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)));
    }
}
