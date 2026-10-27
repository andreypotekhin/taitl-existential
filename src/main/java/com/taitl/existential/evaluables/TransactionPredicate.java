package com.taitl.existential.evaluables;

import com.taitl.existential.transactions.*;
import java.util.function.*;

/**
 * Predicate that resolves invocation-owned sources from an explicit transaction handle.
 * All composes these predicates without capturing a bound view in shared configuration.
 * Calling an unbound managed predicate directly has no implicit current transaction.
 *
 * @param <T> evaluated subject type
 */
public interface TransactionPredicate<T> extends Predicate<T>
{
    /**
     * Evaluates against this invocation's sources without storing the active handle.
     *
     * @param entity reported subject
     * @param tr active transaction whose sources are resolved
     * @return whether the subject satisfies the predicate
     */
    boolean test(T entity, Tr tr);
}
