package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.transactions.*;

public class DwellingOperations
{
    public void report(Tr tr, DwellingWorkload workload, int repeats) throws Exception
    {
        if (repeats < 1)
        {
            throw new IllegalArgumentException("Report repetitions must be positive");
        }
        for (Dwelling<Cat, Brick> dwelling : workload.dwellings)
        {
            for (int i = 0; i < repeats; i++)
            {
                tr.create(dwelling, workload.type);
            }
        }
    }
}
