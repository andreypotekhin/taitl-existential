package com.taitl.existential.interfaces;

import com.taitl.existential.exceptions.FailureException;

public interface ExpressionWithSideEffects<T>
{
    Object execute(T t) throws FailureException;
}
