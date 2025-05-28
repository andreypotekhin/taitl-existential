package com.taitl.exlogic.existential;

import java.util.function.*;
import com.taitl.existential.creator.*;

public class ExistentialInject
{
    public static <T> void inject(Class<T> cls, Supplier<? extends T> supplier)
    {
        Creator.inject(cls, supplier);
    }
}
