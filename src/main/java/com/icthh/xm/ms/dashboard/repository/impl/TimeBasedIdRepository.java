package com.icthh.xm.ms.dashboard.repository.impl;

import com.icthh.xm.ms.dashboard.repository.IdRepository;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Dashboard and widget ids for the MSCONFG mode with updates through the config Kafka queue: the queue gives no
 * synchronous answer, so the dashboards-id.yml counter cannot detect two instances reserving the same range.
 * The id is {@code currentTimeMillis * 1000 + random(0..999)} (below 2^53, safe for JavaScript numbers); within one
 * instance it only grows, so ids created in the same millisecond do not repeat.
 */
@Service
@ConditionalOnProperty(name = "application.storage.ms-config.update-via-kafka", havingValue = "true")
public class TimeBasedIdRepository implements IdRepository {

    private static final long RANDOM_BOUND = 1000;

    private final AtomicLong lastId = new AtomicLong();
    private final LongSupplier clock;

    public TimeBasedIdRepository() {
        this(System::currentTimeMillis);
    }

    TimeBasedIdRepository(LongSupplier clock) {
        this.clock = clock;
    }

    @Override
    public Long getNextId() {
        long candidate = clock.getAsLong() * RANDOM_BOUND + ThreadLocalRandom.current().nextLong(RANDOM_BOUND);
        return lastId.accumulateAndGet(candidate, (last, next) -> Math.max(last + 1, next));
    }
}
