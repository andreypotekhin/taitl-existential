package com.taitl.ex.core.existential;

import java.util.function.*;
import com.taitl.ex.common.creator.*;

public class ExistentialInject
{
    public static <T> void inject(Class<T> cls, Supplier<? extends T> supplier)
    {
        Creator.inject(cls, supplier);
    }
}
