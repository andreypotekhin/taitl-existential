package com.taitl.ex.examples.night_city;

import com.taitl.ex.examples.night_city.app.*;
import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.existential.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class NightCityStoryTest
{
    @Test
    void demonstratesRejectionRecoveryCreationAndRelocation() throws Exception
    {
        assertEquals(List.of("Rejected a non-black cat and rolled back explicitly.",
                "Created a black cat with a dwelling.",
                "Relocated the cat to a dwelling while preserving its color."), NightCityStory.run());
    }

    @Test
    void validatesSharedWorkloadAcrossTwoHundredGenericDescriptors() throws Exception
    {
        int entities = 400;
        int rules = 3;
        CityWorkload workload = new CityWorkload(entities, 200);
        AtomicLong evaluations = new AtomicLong();
        assertEquals(200, workload.descriptors);
        assertEquals(200, workload.looks.stream().map(report -> report.type).distinct().count());
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                new ConfigureLooks().configureWorkload("/night-city/workload", rules, evaluations);
                Tr tr = ex.begin("/night-city/workload");
                try
                {
                    for (CityWorkload.Report report : workload.looks)
                    {
                        create(tr, report);
                    }
                    assertEquals(0, evaluations.get());
                    tr.commit();
                    assertEquals((long) entities * rules, evaluations.get());
                }
                finally
                {
                    tr.close();
                }
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }

    @Test
    void reusesSparseSelectionsAndFoldsRepeatedReports() throws Exception
    {
        CityWorkload workload = new CityWorkload(400, 200);
        AtomicLong evaluations = new AtomicLong();
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                new ConfigureLooks().configureSelectionWorkload("/night-city/sparse", workload, 100,
                        "sparse", evaluations);
                Tr tr = ex.begin("/night-city/sparse");
                try
                {
                    for (CityWorkload.Report report : workload.looks)
                    {
                        create(tr, report);
                        create(tr, report);
                    }
                    assertEquals(0, evaluations.get());
                    tr.commit();
                    assertEquals(200, evaluations.get());
                }
                finally
                {
                    tr.close();
                }
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void create(Tr tr, CityWorkload.Report report) throws Exception
    {
        tr.create(report.entity, (TypeKey) report.type);
    }
}
