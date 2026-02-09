package com.taitl.existential.interfaces;

import java.util.*;
import com.taitl.existential.evaluables.*;

public interface Evaluatable
{
    List<Evs<?>> evs();

    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }
}
