package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.ex.common.annotations.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Behavior flags for configuring Existential library.
 */
public class ExistentialFlags implements Closeable
{
    @Up
    protected Existential ex;

    private volatile int flags;

    public ExistentialFlags(Existential ex)
    {
        this.ex = ex;
    }

    public void on(int flag)
    {
        synchronized (ex)
        {
            check(flag > 0, "Argument 'flag' must be greater than zero");
            check(flag <= Flags.MAX_FLAG, "Argument 'flag' must be no greater than max flag");
            verify(!ex.configured(), "Cannot change flags after configuration has been finalized. See "
                    + "/Troubleshooting.md#library-configuration-after-finalization");
            flags |= flag;
        }
    }

    public void off(int flag)
    {
        synchronized (ex)
        {
            check(flag > 0, "Argument 'flag' must be greater than zero");
            check(flag <= Flags.MAX_FLAG, "Argument 'flag' must be no greater than max flag");
            verify(!ex.configured(), "Cannot change flags after configuration has been finalized. See "
                    + "/Troubleshooting.md#library-configuration-after-finalization");
            flags &= ~flag;
        }
    }

    public boolean toggle(int flag)
    {
        synchronized (ex)
        {
            check(flag > 0, "Argument 'flag' must be greater than zero");
            check(flag <= Flags.MAX_FLAG, "Argument 'flag' must be no greater than max flag");
            verify(!ex.configured(), "Cannot change flags after configuration has been finalized. See "
                    + "/Troubleshooting.md#library-configuration-after-finalization");
            if ((flags & flag) != 0)
            {
                flags &= ~flag;
            }
            else
            {
                flags |= flag;
            }
            return (flags & flag) != 0;
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
