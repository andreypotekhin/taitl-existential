package com.taitl.ex.core.existential;

import java.io.*;
import com.taitl.ex.common.annotations.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Behavior flags for configuring Existential library.
 */
public class ExistentialFlags implements Closeable
{
    @Up
    protected Existential ex;

    private int flags;

    public ExistentialFlags(Existential ex)
    {
        this.ex = ex;
    }

    public void on(int flag)
    {
        check(flag > 0, "Argument 'flag' must be greater than zero");
        check(flag <= Flags.MAX_FLAG, "Argument 'flag' must be no greater than max flag");
        flags |= flag;
    }

    public void off(int flag)
    {
        check(flag > 0, "Argument 'flag' must be greater than zero");
        check(flag <= Flags.MAX_FLAG, "Argument 'flag' must be no greater than max flag");
        flags &= ~flag;
    }

    public boolean toggle(int flag)
    {
        check(flag > 0, "Argument 'flag' must be greater than zero");
        check(flag <= Flags.MAX_FLAG, "Argument 'flag' must be no greater than max flag");
        if ((flags & flag) != 0)
        {
            off(flag);
        }
        else
        {
            on(flag);
        }
        return (flags & flag) != 0;
    }

    public boolean get(int flag)
    {
        return (flags & flag) != 0;
    }

    public void close()
    {
    }
}
