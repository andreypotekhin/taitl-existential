package com.taitl.ex.common.creator;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class CreatorDeviceTest
{
    CreatorDevice device;

    @BeforeEach
    void setup()
    {
        device = new CreatorDevice();
    }

    @Nested
    class Inject
    {
        @Test
        @DisplayName("Uses binary name for local classes")
        void usesBinaryNameForLocalClasses()
        {
            class LocalA
            {
            }
            class LocalB
            {
            }

            assertNull(LocalA.class.getCanonicalName());
            assertNull(LocalB.class.getCanonicalName());

            device.inject(LocalA.class, LocalA::new);
            device.inject(LocalB.class, LocalB::new);

            Supplier<? extends LocalA> supplierA = device.getSupplier(LocalA.class);
            Supplier<? extends LocalB> supplierB = device.getSupplier(LocalB.class);

            assertNotNull(supplierA);
            assertNotNull(supplierB);
            assertThat(supplierA.get(), instanceOf(LocalA.class));
            assertThat(supplierB.get(), instanceOf(LocalB.class));
        }
    }

    @Nested
    class Singleton
    {
        @Test
        @DisplayName("Uses single instance across threads")
        void singleInstanceAcrossThreads() throws Exception
        {
            class One
            {
            }

            AtomicInteger created = new AtomicInteger();
            device.inject(One.class, () -> {
                created.incrementAndGet();
                try
                {
                    Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
                return new One();
            });

            ExecutorService exec = Executors.newFixedThreadPool(2);
            CountDownLatch start = new CountDownLatch(1);
            Future<One> first = exec.submit(() -> {
                start.await();
                return device.singleton(One.class);
            });
            Future<One> second = exec.submit(() -> {
                start.await();
                return device.singleton(One.class);
            });

            start.countDown();

            One a = first.get(2, TimeUnit.SECONDS);
            One b = second.get(2, TimeUnit.SECONDS);

            exec.shutdownNow();

            assertSame(a, b);
            assertEquals(1, created.get());
        }
    }
}
