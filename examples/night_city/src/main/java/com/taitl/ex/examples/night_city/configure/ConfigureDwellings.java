package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

public class ConfigureDwellings
{
    public void configure(String context, int rules, int rows, boolean grouped, IntConsumer evaluations)
    {
        if ((rows < 1 || rows > 32) || rules < rows)
        {
            throw new IllegalArgumentException("Use 1 to 32 rows and at least one declaration per row");
        }
        Objects.requireNonNull(evaluations, "evaluations");
        List<TypeKey<? extends Dwelling<?, ?>>> subjects = subjects();
        for (int i = 0; i < rules; i++)
        {
            int row = grouped ? (int) ((long) i * rows / rules) : i % rows;
            declare(context, subjects.get(row / 4), row % 4, i, evaluations);
        }
    }

    protected List<TypeKey<? extends Dwelling<?, ?>>> subjects()
    {
        return List.of(new TypeKey<Dwelling<Cat, Brick>>() {
        },
                new TypeKey<Dwelling<Cat, ?>>() {
                },
                new TypeKey<Dwelling<?, Brick>>() {
                },
                new TypeKey<Dwelling<?, ?>>() {
                },
                TypeKey.<Dwelling<?, ?>> valueOf("Dwelling"),
                new TypeKey<Dwelling<? extends Being<?>, Brick>>() {
                },
                new TypeKey<Dwelling<Cat, ? extends Material>>() {
                },
                new TypeKey<Dwelling<? extends Being<?>, ? extends Material>>() {
                });
    }

    protected <T extends Dwelling<?, ?>> void declare(String context, TypeKey<T> type, int family,
            int declaration, IntConsumer evaluations)
    {
        Predicate<T> valid = dwelling -> {
            evaluations.accept(declaration);
            return dwelling.color != null && dwelling.location != null;
        };
        String description = "Dwelling declaration " + declaration + " requires color and location";
        // @formatter:off
        InvariantBuilder<T> invariant = Ex.configure().context(context).invariant(type);
        // @formatter:on
        switch (family)
        {
        case 0:
            invariant.create(valid, description);
            break;
        case 1:
            invariant.cu(valid, description);
            break;
        case 2:
            invariant.cud(valid, description);
            break;
        default:
            invariant.all(valid, description);
        }
    }
}
