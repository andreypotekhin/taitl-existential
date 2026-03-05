package com.taitl.existential.builders;

import com.taitl.existential.evaluables.*;

/**
 * Builder interface for {@link Evs}.
 *
 * @param <T> Subject type for Evs rules
 */
public interface EvsBuilder<T>
{
    /**
     * Builds the configured rules into an {@link Evs} instance.
     *
     * @return Configured rule set
     */
    public Evs<T> build();
}
