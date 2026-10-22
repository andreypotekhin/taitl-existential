package com.taitl.existential;

import java.time.Duration;

import static com.taitl.ex.common.helper.Args.sane;
import static com.taitl.ex.common.helper.State.verify;

/**
 * Controls how long library shutdown waits for active transactions.
 * A zero timeout means that shutdown waits without a time limit.
 */
public final class ShutdownPolicy
{
    private final Duration timeout;

    private ShutdownPolicy(Duration timeout)
    {
        sane(timeout, "timeout");
        verify(!timeout.isNegative(), "Shutdown timeout must not be negative");
        this.timeout = timeout;
    }

    public static ShutdownPolicy graceful()
    {
        return new ShutdownPolicy(Duration.ZERO);
    }

    public static ShutdownPolicy graceful(Duration timeout)
    {
        return new ShutdownPolicy(timeout);
    }

    public Duration timeout()
    {
        return timeout;
    }
}
