package com.taitl.existential.builders;

import com.taitl.existential.evaluables.*;

/**
 * Builder contract for assembling a concrete {@link Evs} instance.
 *
 * @param <T> Subject type the rule set targets
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
