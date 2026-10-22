package com.taitl.existential.benchmarks;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.*;
import com.taitl.existential.quantifiers.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RelationshipBenchmark
{
    @Test
    @DisplayName("Caller-supplied relationships and explicit evolution at stated source cardinalities")
    void relationships() throws Exception
    {
        CustomTransactionBenchmark harness = new CustomTransactionBenchmark();
        for (int size : new int[] { 16, 256, 4096 })
        {
            for (Source source : Source.values())
            {
                CustomTransactionBenchmark.BenchmarkResult result = harness.measure(source + "/" + size,
                        () -> new RelationshipScenario(source, size, 1));
                System.out.println(result);
                assertTrue(result.opsPerSecond() > 0);
            }
        }
        CustomTransactionBenchmark.BenchmarkResult repeated = harness.measure("MAP/256/repeated16",
                () -> new RelationshipScenario(Source.MAP, 256, 16));
        System.out.println(repeated);
        assertTrue(repeated.opsPerSecond() > 0);
    }

    enum Source
    {
        COLLECTION, SET, MAP, RELATIONSHIP
    }

    static class RelationshipScenario implements CustomTransactionBenchmark.Scenario
    {
        static final String OP = "/bench/cats/relocate";
        Existential ex = new Existential();
        Cat before;
        Cat after;
        int reports;

        RelationshipScenario(Source source, int size, int reports)
        {
            this.reports = reports;
            List<Cat> cats = new ArrayList<>();
            Map<Cat, Dwelling<Cat, ?>> dwellings = new LinkedHashMap<>();
            for (int i = 0; i < size; i++)
            {
                Cat cat = new Cat("Black-" + i, "Place-" + i);
                cats.add(cat);
                dwellings.put(cat, new Dwelling<Cat, Rugs>("Grey", cat.location()));
            }
            Cat target = cats.get(cats.size() - 1);
            before = new Cat(target.color, "Previous");
            after = new Cat(target.color, target.location());
            Exists<Cat> exists;
            switch (source)
            {
            case COLLECTION:
                exists = new Exists<>(cats);
                break;
            case SET:
                exists = new Exists<>(new LinkedHashSet<>(cats));
                break;
            case MAP:
                exists = new Exists<>(dwellings);
                break;
            default:
                exists = new Exists<>(dwellings, (cat, dwelling) -> cat.location().equals(dwelling.location()));
                break;
            }
            // @formatter:off
            ex.configure().context(OP)
                .invariant(Cat.class)
                    .all(exists, "Destination cat must belong to the supplied source")
                    .transit((oldValue, newValue) -> oldValue.color.equals(newValue.color),
                            "Relocation preserves color");
            // @formatter:on
        }

        public void run(int iterations) throws Exception
        {
            for (int i = 0; i < iterations; i++)
            {
                Tr tr = ex.begin(OP);
                for (int report = 0; report < reports; report++)
                {
                    tr.transit(before, after);
                }
                tr.commit();
            }
        }

        public void close()
        {
            ex.close();
        }
    }
}
