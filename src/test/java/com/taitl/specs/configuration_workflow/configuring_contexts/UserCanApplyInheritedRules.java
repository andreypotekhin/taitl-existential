package com.taitl.specs.configuration_workflow.configuring_contexts;

import com.taitl.existential.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.ex.examples.night_city.model.taxonomy.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanApplyInheritedRules extends SpecBase
{
    interface Named
    {
    }

    interface Left extends Named
    {
    }

    interface Right extends Named
    {
    }

    static class Diamond implements Left, Right
    {
    }

    static class Box<T>
    {
        T value;

        Box(T value)
        {
            this.value = value;
        }
    }

    static class GenericBox<T> extends Box<T>
    {
        GenericBox(T value)
        {
            super(value);
        }
    }

    static class Outer<T>
    {
        T value;

        Outer(T value)
        {
            this.value = value;
        }
    }

    static class CatBox extends GenericBox<com.taitl.ex.examples.night_city.model.Cat>
    {
        CatBox()
        {
            super(new com.taitl.ex.examples.night_city.model.Cat("Black", "Park"));
        }
    }

    {
        autoConfigure = false;
    }

    @BeforeEach
    void setUp()
    {
        super.setup();
    }

    @AfterEach
    void tearDown()
    {
        super.cleanup();
    }

    @Test
    void appliesGenericSuperclassRuleWithResolvedArgument() throws Exception
    {
        AtomicInteger cats = new AtomicInteger();
        AtomicInteger mice = new AtomicInteger();
        AtomicInteger bounded = new AtomicInteger();
        // @formatter:off
        Ex.configure()
            .context(op)
                .invariant(new TypeKey<Box<com.taitl.ex.examples.night_city.model.Cat>>() {})
                    .create(box -> cats.incrementAndGet() > 0, "Cat box")
                .invariant(new TypeKey<Box<com.taitl.ex.examples.night_city.model.Mouse>>() {})
                    .create(box -> mice.incrementAndGet() > 0, "Mouse box");
        Ex.configure()
            .context(op)
                .invariant(TypeKey.valueOf("Box<? extends Being>"))
                    .create(box -> bounded.incrementAndGet() > 0, "Being box");
        // @formatter:on
        Tr tr = ex.begin(op);
        try
        {
            tr.create(new CatBox());
            Box<com.taitl.ex.examples.night_city.model.Cat> explicit =
                    new Box<>(new com.taitl.ex.examples.night_city.model.Cat("Black", "Park"));
            tr.create(explicit, new TypeKey<Box<com.taitl.ex.examples.night_city.model.Cat>>() {
            });
            tr.commit();
            assertEquals(2, cats.get());
            assertEquals(0, mice.get());
            assertEquals(2, bounded.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void appliesDiamondInterfaceDeclarationOnce() throws Exception
    {
        AtomicInteger calls = new AtomicInteger();
        // @formatter:off
        Ex.configure()
            .context(op)
                .invariant(Named.class)
                    .create(value -> calls.incrementAndGet() > 0, "Named entities");
        // @formatter:on
        Tr tr = ex.begin(op);
        try
        {
            tr.create(new Diamond());
            tr.commit();
            assertEquals(1, calls.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void keepsNestedGenericArgumentsInvariant() throws Exception
    {
        AtomicInteger calls = new AtomicInteger();
        // @formatter:off
        Ex.configure()
            .context(op)
                .invariant(TypeKey.valueOf("Outer<Box<?>>"))
                    .create(value -> calls.incrementAndGet() > 0, "Nested wildcard");
        // @formatter:on
        Tr tr = ex.begin(op);
        try
        {
            Outer<Box<com.taitl.ex.examples.night_city.model.Cat>> value =
                    new Outer<>(new Box<>(new com.taitl.ex.examples.night_city.model.Cat("Black", "Park")));
            tr.create(value, TypeKey.valueOf("Outer<Box<Cat>>"));
            tr.commit();
            assertEquals(0, calls.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void warnsWhenRawGenericRuntimeTypeLosesItsArgument() throws Exception
    {
        AtomicInteger calls = new AtomicInteger();
        assertTrue(ex.get(com.taitl.existential.constants.Flags.TYPE_MATCHING_WARN_UNRESOLVED_GENERICS));
        // @formatter:off
        Ex.configure()
            .context(op)
                .invariant(GenericBox.class)
                    .create(value -> calls.incrementAndGet() > 0, "Generic box accepts reports");
        // @formatter:on
        Tr tr = ex.begin(op);
        try
        {
            tr.create(new GenericBox<>(new com.taitl.ex.examples.night_city.model.Cat("Black", "Park")));
            tr.commit();
            assertEquals(1, calls.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void allowsExplicitOptOutForExpectedRawGenericReports() throws Exception
    {
        ex.off(com.taitl.existential.constants.Flags.TYPE_MATCHING_WARN_UNRESOLVED_GENERICS);
        AtomicInteger calls = new AtomicInteger();
        // @formatter:off
        Ex.configure()
            .context(op)
                .invariant(GenericBox.class)
                    .create(value -> calls.incrementAndGet() > 0, "Generic box accepts reports");
        // @formatter:on
        Tr tr = ex.begin(op);
        try
        {
            tr.create(new GenericBox<>(new com.taitl.ex.examples.night_city.model.Cat("Black", "Park")));
            tr.commit();
            assertEquals(1, calls.get());
        }
        finally
        {
            tr.close();
        }
    }

    @Test
    void rejectsLowerBoundRuleDuringConfiguration()
    {
        // @formatter:off
        Ex.configure()
            .context(op)
                .invariant(TypeKey.valueOf("Box<? super Cat>"))
                    .create(value -> true, "Unsupported lower bound");
        // @formatter:on
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class, () -> ex.begin(op));
        assertTrue(failure.getMessage().contains("unsupported-lower-bounded-type-rules"));
    }

    @Test
    void appliesIndependentBoundsAndParameterizedBoundsWithoutVariantProducts() throws Exception
    {
        AtomicInteger dwellings = new AtomicInteger();
        AtomicInteger beings = new AtomicInteger();
        AtomicInteger wrong = new AtomicInteger();
        // @formatter:off
        Ex.configure().context(op)
            .invariant(new TypeKey<Dwelling<? extends Being<?>, ? extends Material>>() {})
                .create(value -> dwellings.incrementAndGet() > 0, "Bounded dwelling arguments")
            .invariant(new TypeKey<Box<? extends Being<Felidae>>>() {})
                .create(value -> beings.incrementAndGet() > 0, "Parameterized feline bound")
            .invariant(new TypeKey<Box<? extends Being<Muridae>>>() {})
                .create(value -> wrong.incrementAndGet() > 0, "Parameterized murid bound");
        // @formatter:on
        Tr tr = ex.begin(op);
        try
        {
            tr.create(new Dwelling<Cat, Rugs>("Grey", "Park"), new TypeKey<Dwelling<Cat, Rugs>>() {
            });
            tr.create(new Box<Cat>(new Cat("Black", "Park")), new TypeKey<Box<Cat>>() {
            });
            tr.create(new Box<Being<Felidae>>(new Cat("Black", "Garden")),
                    new TypeKey<Box<Being<Felidae>>>() {
                    });
            tr.commit();
            assertEquals(1, dwellings.get());
            assertEquals(2, beings.get());
            assertEquals(0, wrong.get());
        }
        finally
        {
            tr.close();
        }
    }

}
