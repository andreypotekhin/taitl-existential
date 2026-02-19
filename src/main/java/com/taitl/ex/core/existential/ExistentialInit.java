package com.taitl.ex.core.existential;

import java.io.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.library.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;

public class ExistentialInit implements Closeable
{
    protected Existential ex;
    protected ConfigureLibrary configureLibrary;

    static
    {
        inject(Context.class, () -> new Context("undefined"));
        inject(Transaction.class, () -> new Transaction("undefined", "undefined"));
    }

    public ExistentialInit(Existential ex)
    {
        this.ex = ex;
        this.configureLibrary = new ConfigureLibrary(ex);
    }

    /* Testing-only */
    public ExistentialInit(Existential ex, ConfigureLibrary configureLibrary)
    {
        this.ex = ex;
        this.configureLibrary = configureLibrary;
    }

    public static <T> void inject(Class<T> cls, Supplier<? extends T> supplier)
    {
        Creator.inject(cls, supplier);
    }

    public void startup()
    {
        configureLibrary.configure();
    }

    // Deinit library
    public void close()
    {
    }
}
