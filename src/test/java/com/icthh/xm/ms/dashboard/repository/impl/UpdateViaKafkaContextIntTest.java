package com.icthh.xm.ms.dashboard.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.icthh.xm.ms.dashboard.AbstractSpringBootTest;
import com.icthh.xm.ms.dashboard.repository.IdRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

/**
 * With {@code application.storage.ms-config.update-via-kafka=true} ids come from time, not from the
 * dashboards-id.yml counter.
 */
@TestPropertySource(properties = "application.storage.ms-config.update-via-kafka=true")
public class UpdateViaKafkaContextIntTest extends AbstractSpringBootTest {

    @Autowired
    private IdRepository idRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void usesTimeBasedIds() {
        assertThat(idRepository).isInstanceOf(TimeBasedIdRepository.class);
        assertThat(applicationContext.getBeansOfType(IdRefreshableRepository.class)).isEmpty();
    }
}
