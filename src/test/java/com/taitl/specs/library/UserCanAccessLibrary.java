package com.taitl.specs.library;

import com.taitl.existential.transactions.Tr;
import com.taitl.existential.Ex;
import com.taitl.existential.Existential;
import com.taitl.existential.constants.Flags;
import com.taitl.existential.exceptions.ValidationStageExceptions;
import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.specs.SpecBase;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanAccessLibrary extends SpecBase
{
    @BeforeEach
    public void setup()
    {
        super.setup();
    }

    @AfterEach
    public void cleanup()
    {
        super.cleanup();
    }

    @Nested
    class Scenarios
    {
        @Test
        @DisplayName("User can access library using static facade")
        void accessWithStaticFacade() throws Exception
        {
            Tr tranTr = Ex.begin(op);
            String tran = tranTr.id();
            tranTr.commit();
        }

        @Test
        @DisplayName("User can access library using a singleton")
        void accessWithSingleton() throws Exception
        {
            Tr tranTr = ex.begin(op);
            String tran = tranTr.id();
            tranTr.commit();
        }

        @Test
        @DisplayName("User can independently configure and use library instances")
        void useIndependentInstances() throws Exception
        {
            try (Existential first = new Existential(); Existential second = new Existential())
            {
                first.configure()
                        .context(op)
                        .invariant(Cat.class)
                        .create(value -> "Black".equals(value.color), "first instance accepts black cats");
                second.configure()
                        .context(op)
                        .invariant(Cat.class)
                        .create(value -> "White".equals(value.color), "second instance accepts white cats");
                first.on(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS);

                Tr black = first.begin(op);
                first.create(new Cat("Black", "Park"), black.id());
                black.commit();

                Tr white = second.begin(op);
                second.create(new Cat("White", "Park"), white.id());
                white.commit();

                Tr rejected = first.begin(op);
                first.create(new Cat("White", "Park"), rejected.id());
                assertThrows(ValidationStageExceptions.class, rejected::commit);
                rejected.rollback();

                assertTrue(first.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS));
                assertFalse(second.get(Flags.BEHAVIOR_RULES_REQUIRE_DESCRIPTIONS));
            }
        }

        @Test
        @DisplayName("User can publish configured instance and start concurrent first transactions")
        void startConcurrentFirstTransactionsAfterHandoff() throws Exception
        {
            try (Existential configured = new Existential())
            {
                AtomicInteger factoryCalls = new AtomicInteger();
                configured.configure()
                        .contextFactory(() -> {
                            factoryCalls.incrementAndGet();
                            return new com.taitl.existential.configs.Context(op);
                        })
                        .context(op)
                        .invariant(Cat.class)
                        .create(value -> true, "accept cats");

                CountDownLatch start = new CountDownLatch(1);
                ExecutorService workers = Executors.newFixedThreadPool(2);
                try
                {
                    Future<?> first = workers.submit(() -> beginAndCommit(configured, start));
                    Future<?> second = workers.submit(() -> beginAndCommit(configured, start));
                    start.countDown();
                    first.get(5, TimeUnit.SECONDS);
                    second.get(5, TimeUnit.SECONDS);
                    assertEquals(1, factoryCalls.get());
                }
                finally
                {
                    workers.shutdownNow();
                }
            }
        }

        @Test
        @DisplayName("Independent instances can finalize configuration at the same time")
        void finalizeIndependentInstancesWithoutBlocking() throws Exception
        {
            try (Existential first = new Existential(); Existential second = new Existential())
            {
                CountDownLatch firstFactoryEntered = new CountDownLatch(1);
                CountDownLatch releaseFirstFactory = new CountDownLatch(1);
                first.configure()
                        .contextFactory(() -> {
                            firstFactoryEntered.countDown();
                            await(releaseFirstFactory);
                            return new com.taitl.existential.configs.Context(op);
                        })
                        .context(op)
                        .invariant(Cat.class)
                        .create(value -> true, "accept cats");
                second.configure().context(op).invariant(Cat.class).create(value -> true, "accept cats");

                ExecutorService workers = Executors.newFixedThreadPool(2);
                try
                {
                    Future<?> firstBegin = workers.submit(() -> {
                        first.begin(op).commit();
                        return null;
                    });
                    assertTrue(firstFactoryEntered.await(5, TimeUnit.SECONDS));
                    Future<?> secondBegin = workers.submit(() -> {
                        second.begin(op).commit();
                        return null;
                    });
                    secondBegin.get(5, TimeUnit.SECONDS);
                    releaseFirstFactory.countDown();
                    firstBegin.get(5, TimeUnit.SECONDS);
                }
                finally
                {
                    releaseFirstFactory.countDown();
                    workers.shutdownNow();
                }
            }
        }

        private void beginAndCommit(Existential instance, CountDownLatch start)
        {
            try
            {
                start.await();
                instance.begin(op).commit();
            }
            catch (Exception e)
            {
                throw new CompletionException(e);
            }
        }

        private void await(CountDownLatch latch)
        {
            try
            {
                latch.await();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new CompletionException(e);
            }
        }
    }
}
