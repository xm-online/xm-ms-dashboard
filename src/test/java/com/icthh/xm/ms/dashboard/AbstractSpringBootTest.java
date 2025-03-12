package com.icthh.xm.ms.dashboard;

import com.icthh.xm.ms.dashboard.config.IntegrationTestConfiguration;
import com.icthh.xm.ms.dashboard.config.SecurityBeanOverrideConfiguration;
import com.icthh.xm.ms.dashboard.config.TestLepConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {
        TestLepConfiguration.class,
        SecurityBeanOverrideConfiguration.class,
        DashboardApp.class,
        IntegrationTestConfiguration.class
})
@Tag("com.icthh.xm.ms.dashboard.AbstractSpringBootTest")
@ExtendWith(SpringExtension.class)
public abstract class AbstractSpringBootTest {

    // TODO: To speedup test:
    //      - find all cases which break Spring context like @MockBean and fix.
    //      - separate tests by categories: Unit, SpringBoot, WebMwc

}
