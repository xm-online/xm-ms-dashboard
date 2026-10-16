package com.icthh.xm.ms.dashboard.repository.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.icthh.xm.ms.dashboard.AbstractUnitTest;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class TimeBasedIdRepositoryUnitTest extends AbstractUnitTest {

    private static final long NOW = 1_792_000_000_000L;

    @Test
    public void getNextId_isTimeTimesThousandPlusRandom() {
        long id = new TimeBasedIdRepository(() -> NOW).getNextId();

        assertThat(id).isBetween(NOW * 1000, NOW * 1000 + 999);
        assertThat(id).isLessThan(1L << 53);
    }

    @Test
    public void getNextId_doesNotRepeatWithinOneMillisecond() {
        TimeBasedIdRepository repository = new TimeBasedIdRepository(() -> NOW);
        Set<Long> ids = new HashSet<>();

        for (int i = 0; i < 5000; i++) {
            ids.add(repository.getNextId());
        }

        assertThat(ids).hasSize(5000);
    }
}
