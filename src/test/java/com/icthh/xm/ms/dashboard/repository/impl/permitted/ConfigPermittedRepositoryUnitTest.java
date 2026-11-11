package com.icthh.xm.ms.dashboard.repository.impl.permitted;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.icthh.xm.commons.permission.service.PermissionCheckService;
import com.icthh.xm.ms.dashboard.AbstractUnitTest;
import com.icthh.xm.ms.dashboard.config.ApplicationProperties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class ConfigPermittedRepositoryUnitTest extends AbstractUnitTest {

    private String longCondition() {
        String condition = IntStream.range(0, 400)
            .mapToObj(i -> "(#returnObject == 'TYPE_KEY_NUMBER_" + i + "')")
            .collect(Collectors.joining(" OR "));
        assertThat(condition.length()).isBetween(10_001, 19_999);
        return condition;
    }

    private ConfigPermittedRepository repository(int spelMaxExpressionLength) {
        PermissionCheckService permissionService = mock(PermissionCheckService.class);
        when(permissionService.createCondition(any(), eq("DASHBOARD.GET_LIST"), any())).thenReturn(longCondition());
        ApplicationProperties properties = new ApplicationProperties();
        properties.setSpelMaxExpressionLength(spelMaxExpressionLength);
        return new ConfigPermittedRepository(permissionService, properties) {
        };
    }

    @Test
    public void failsOnLongConditionByDefault() {
        ConfigPermittedRepository repository = repository(10_000);

        assertThatThrownBy(() -> repository.matchSpelExpression("DASHBOARD.GET_LIST", "TYPE_KEY_NUMBER_399"))
            .hasMessageContaining("EL1079E");
    }

    @Test
    public void matchesLongConditionWhenLimitIsOverridden() {
        ConfigPermittedRepository repository = repository(20_000);

        assertThat(repository.matchSpelExpression("DASHBOARD.GET_LIST", "TYPE_KEY_NUMBER_399")).isTrue();
    }
}
