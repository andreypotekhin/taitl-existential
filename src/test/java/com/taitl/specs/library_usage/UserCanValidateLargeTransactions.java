package com.taitl.specs.library_usage;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.data.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanValidateLargeTransactions extends SpecBase
{
    private static final int ENTITIES = 64;
    private static final int RULES = 5;
    private final AtomicLong evaluations = new AtomicLong();

    {
        autoConfigure = false;
    }

    @BeforeEach
    void setUp()
    {
        super.setup();
        CityWorkload workload = new CityWorkload(ENTITIES);
        new ConfigureCats().configureWorkload(op, workload.dwellings, RULES, evaluations);
    }

    @AfterEach
    void tearDown()
    {
        super.cleanup();
    }

    @Test
    void evaluatesValidationRulesAtCommitAfterReportingAllEntities() throws Exception
    {
        CityWorkload workload = new CityWorkload(ENTITIES);
        Tr tr = ex.begin(op);
        try
        {
            assertFalse(tr.beginActive());
            assertFalse(tr.immediateActive());
            for (Cat cat : workload.cats)
            {
                tr.create(cat);
            }
            assertEquals(0, evaluations.get());
            tr.commit();
            assertEquals((long) ENTITIES * RULES, evaluations.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void bindsImmediateRulesForReportTimeExecution() throws Exception
    {
        AtomicInteger immediate = new AtomicInteger();
        // @formatter:off
        Ex.configure()
            .context(op)
                .immediate()
                .effect(Cat.class)
                    .create(cat -> immediate.incrementAndGet(), "Count immediate reports");
        // @formatter:on
        Tr tr = ex.begin(op);
        try
        {
            assertTrue(tr.immediateActive());
            tr.create(new Cat("Black", "Park"));
            assertEquals(1, immediate.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void validatesDistinctGenericDescriptorsAfterReporting() throws Exception
    {
        int descriptors = 200;
        int entities = 400;
        AtomicLong lookEvaluations = new AtomicLong();
        CityWorkload workload = new CityWorkload(entities, descriptors);
        assertEquals(descriptors, workload.descriptors);
        assertEquals(descriptors, workload.looks.stream().map(report -> report.type).distinct().count());
        new ConfigureLooks().configureWorkload(op, RULES, lookEvaluations);
        Tr tr = ex.begin(op);
        try
        {
            for (CityWorkload.Report report : workload.looks)
            {
                create(tr, report);
            }
            assertEquals(0, lookEvaluations.get());
            tr.commit();
            assertEquals((long) entities * RULES, lookEvaluations.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void bindsImmediateRulesFromSuppliedTransaction() throws Exception
    {
        AtomicInteger immediate = new AtomicInteger();
        Transaction custom = new Transaction(op, "custom-immediate");
        Effect<Cat> effect = new Effect<>(Cat.class);
        effect.create(cat -> immediate.incrementAndGet(), "Count custom immediate reports");
        custom.effect(effect, StageName.IMMEDIATE);
        Tr tr = ex.begin(op, custom);
        try
        {
            assertTrue(tr.immediateActive());
            tr.create(new Cat("Black", "Park"));
            assertEquals(1, immediate.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void foldsRepeatedReportsWhileCheckingDistinctEqualEntities() throws Exception
    {
        CityWorkload workload = new CityWorkload(ENTITIES);
        Cat first = workload.cats.get(0);
        Cat equal = workload.cats.get(1);
        assertEquals(first, equal);
        assertNotSame(first, equal);
        Tr tr = ex.begin(op);
        try
        {
            for (int i = 0; i < 16; i++)
            {
                tr.create(first);
                tr.create(equal);
            }
            assertEquals(0, evaluations.get());
            tr.commit();
            assertEquals(2L * RULES, evaluations.get());
        }
        finally
        {
            tr.close();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void create(Tr tr, CityWorkload.Report report) throws Exception
    {
        tr.create(report.entity, (TypeKey) report.type);
    }
}
