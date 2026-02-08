package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Behavior flags for configuring Existential library.
 */
public class ExistentialFlags implements Closeable
{
    private static final String ARG_FLAG_MIN = "Argument 'flag' must be greater than zero";
    private static final String ARG_FLAG_MAX = "Argument 'flag' must be no greater than max flag";

    private int flags;

    protected Existential ex;

    public ExistentialFlags(Existential ex)
    {
        this.ex = ex;
    }

    public void on(int flag)
    {
        check(flag > 0, ARG_FLAG_MIN);
        check(flag <= Flags.MAX_FLAG, ARG_FLAG_MAX);
        flags |= flag;
    }

    public void off(int flag)
    {
        check(flag > 0, ARG_FLAG_MIN);
        check(flag <= Flags.MAX_FLAG, ARG_FLAG_MAX);
        flags &= ~flag;
    }

    public void toggle(int flag)
    {
        check(flag > 0, ARG_FLAG_MIN);
        check(flag <= Flags.MAX_FLAG, ARG_FLAG_MAX);
        if ((flags & flag) != 0)
        {
            off(flag);
        }
        else
        {
            on(flag);
        }
    }

    public boolean get(int flag)
    {
        return (flags & flag) != 0;
    }

    public void close()
    {
    }
}
