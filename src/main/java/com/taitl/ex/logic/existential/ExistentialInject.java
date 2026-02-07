package com.taitl.ex.logic.existential;

import java.util.function.*;
import com.taitl.ex.logic.creator.*;

public class ExistentialInject
{
    public static <T> void inject(Class<T> cls, Supplier<? extends T> supplier)
    {
        Creator.inject(cls, supplier);
    }
}
