package com.taitl.existential.interfaces;

import com.taitl.existential.exceptions.FailureException;

public interface Expression<T>
{
    Object evaluate(T t) throws FailureException;
}
