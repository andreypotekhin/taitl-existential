package com.taitl.ex.common.helper.collections;

import com.taitl.ex.common.helper.Args;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Coll
{
    public static <T> T getFirst(Collection<T> coll)
    {
        Optional<T> result = coll.stream().findFirst();
        return result.isPresent() ? result.get() : null;
    }

    public static <T, C extends Collection<T>> C removeMatching(C values,
            Predicate<? super T> match,
            Supplier<C> removedFactory)
    {
        Args.sane(values, "values");
        Args.sane(match, "match");
        Args.sane(removedFactory, "removedFactory");
        C removed = removedFactory.get();
        Args.sane(removed, "removed");
        Iterator<T> iterator = values.iterator();
        while (iterator.hasNext())
        {
            T value = iterator.next();
            if (match.test(value))
            {
                removed.add(value);
                iterator.remove();
            }
        }
        return removed;
    }
}
