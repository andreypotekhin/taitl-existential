package com.taitl.ex.core.existential;

import java.io.*;
import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.transactions.*;

public class ExistentialInit implements Closeable
{
    protected Existential ex;

    static
    {
        inject(Context.class, () -> new Context("undefined"));
        inject(Transaction.class, () -> new Transaction("undefined", "undefined"));
    }

    public ExistentialInit(Existential ex)
    {
        this.ex = ex;
    }

    public static <T> void inject(Class<T> cls, Supplier<? extends T> supplier)
    {
        Creator.inject(cls, supplier);
    }

    // Deinit library
    public void close()
    {
    }
}
