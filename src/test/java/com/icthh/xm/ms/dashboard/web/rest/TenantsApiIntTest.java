package com.icthh.xm.ms.dashboard.web.rest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icthh.xm.commons.gen.api.TenantsApi;
import com.icthh.xm.commons.gen.api.TenantsApiController;
import com.icthh.xm.commons.gen.model.Tenant;
import com.icthh.xm.commons.tenant.TenantContextHolder;
import com.icthh.xm.commons.tenant.TenantContextUtils;
import com.icthh.xm.commons.tenantendpoint.TenantManager;
import com.icthh.xm.ms.dashboard.DashboardApp;
import com.icthh.xm.ms.dashboard.config.SecurityBeanOverrideConfiguration;
import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.transaction.Transactional;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DashboardApp.class, SecurityBeanOverrideConfiguration.class})
public class TenantsApiIntTest {

    private MockMvc mvc;

    @Autowired
    private TenantManager tenantManager;

    @Autowired
    private MultiTenantConnectionProvider connectionProvider;

    @Autowired
    private TenantContextHolder tenantContextHolder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        TenantContextUtils.setTenant(tenantContextHolder, "XM");

        TenantsApi controller = new TenantsApiController(new TenantResource(tenantManager));
        this.mvc = MockMvcBuilders
            .standaloneSetup(controller)
            .build();
    }

    @Test
    @Transactional
    public void testAddTenant() throws Exception {
        ObjectMapper om = new ObjectMapper();
        mvc.perform(post("/tenants").content(om.writeValueAsBytes(new Tenant().tenantKey("testadd"))).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        assertExistSchema("testadd");
    }

    private void assertExistSchema(String name) throws SQLException {
        connectionProvider.releaseAnyConnection(connectionProvider.getConnection(name));
    }

    private void assertNotExistSchema(String name) throws SQLException {
        try {
            connectionProvider.releaseAnyConnection(connectionProvider.getConnection(name));
            fail("Schema " + name + " exists");
        } catch (HibernateException e) {
            assertTrue(e.getMessage().contains("Could not alter JDBC connection to specified schema [" + name + "]"));
        }
    }


    @Test
    @Transactional
    public void testDeleteTenant() throws Exception {
        testAddTenant();
        mvc.perform(delete("/tenants/testadd")).andExpect(status().isOk());
        assertNotExistSchema("testadd");
    }

}
