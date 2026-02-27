package com.taitl.ex.concrete;

import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

public class ConcreteAllBuilder<T>
{
    Predicate<? super T> condition;
    Predicate<? super T> predicate;
    String description;

    public ConcreteAll<T> build()
    {
        sane(predicate, "predicate");
        ConcreteAll<T> result = new ConcreteAll<>();
        result.condition = condition;
        result.predicate = predicate;
        result.description = description;
        return result;
    }

    public ConcreteAllBuilder<T> condition(Predicate<? super T> condition)
    {
        sane(condition, "condition");
        this.condition = condition;
        return this;
    }

    public ConcreteAllBuilder<T> predicate(Predicate<? super T> predicate)
    {
        sane(predicate, "predicate");
        this.predicate = predicate;
        return this;
    }

    public ConcreteAllBuilder<T> description(String description)
    {
        sane(description, "description");
        this.description = description;
        return this;
    }
}
