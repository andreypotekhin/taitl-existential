package com.taitl.ex.examples.night_city.data;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.keys.*;

import java.util.*;

public class DwellingWorkload
{
    public final TypeKey<Dwelling<Cat, Brick>> type = new TypeKey<Dwelling<Cat, Brick>>() {
    };
    public final List<Dwelling<Cat, Brick>> dwellings = new ArrayList<>();

    public DwellingWorkload(int size)
    {
        if (size < 1)
        {
            throw new IllegalArgumentException("Dwelling workload size must be positive");
        }
        for (int i = 0; i < size; i++)
        {
            dwellings.add(new Dwelling<>("Grey", "Park"));
        }
    }
}
