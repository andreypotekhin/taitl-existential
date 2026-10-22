package com.taitl.specs.library_usage;

import com.taitl.existential.*;
import com.taitl.existential.transactions.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanShutdownLibrary
{
    private static final String OP = "/api/shutdown";

    private Existential ex;

    @BeforeEach
    void setUp()
    {
        ex = new Existential();
        ex.shutdownPolicy(ShutdownPolicy.graceful(Duration.ofSeconds(2)));
    }

    @AfterEach
    void tearDown()
    {
        ex.close();
    }

    @Test
    void timeoutLeavesLibraryDrainingAndRetryCompletesAfterTransaction()
            throws Exception
    {
        ex.configure().context(OP).invariant(String.class).all(value -> true, "shutdown test context");
        ex.shutdownPolicy(ShutdownPolicy.graceful(Duration.ofMillis(1)));
        Tr tr = ex.begin(OP);

        assertThrows(IllegalStateException.class, ex::close);
        assertThrows(IllegalStateException.class, () -> ex.begin(OP));

        tr.rollback();
        assertDoesNotThrow(ex::close);
        assertThrows(IllegalStateException.class, () -> ex.begin(OP));
    }

    @Test
    void interruptionPreservesStatusAndAllowsRetry() throws Exception
    {
        ex.configure().context(OP).invariant(String.class).all(value -> true, "shutdown test context");
        Tr tr = ex.begin(OP);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try
        {
            Future<Boolean> interrupted = executor.submit(() -> {
                Thread.currentThread().interrupt();
                try
                {
                    ex.close();
                    return false;
                }
                catch (IllegalStateException expected)
                {
                    return Thread.currentThread().isInterrupted();
                }
            });

            assertTrue(interrupted.get(2, TimeUnit.SECONDS), "close should restore the interrupt flag");
            tr.rollback();
            assertDoesNotThrow(ex::close);
        }
        finally
        {
            tr.close();
            executor.shutdownNow();
        }
    }

    @Test
    void callbackCanReturnWhileConcurrentShutdownWaits() throws Exception
    {
        CountDownLatch callbackEntered = new CountDownLatch(1);
        CountDownLatch releaseCallback = new CountDownLatch(1);
        AtomicReference<Throwable> callbackCloseFailure = new AtomicReference<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Tr tr = null;

        try
        {
            // @formatter:off
            ex.configure()
                    .context(OP)
                    .commit()
                        .effect(String.class)
                            .create(value -> {
                                callbackEntered.countDown();
                                await(releaseCallback);
                                try
                                {
                                    ex.close();
                                }
                                catch (Throwable failure)
                                {
                                    callbackCloseFailure.set(failure);
                                }
                            }, "attempt library close from a callback");
            // @formatter:on

            tr = ex.begin(OP);
            Tr active = tr;
            Future<?> commit = executor.submit(() -> {
                active.create("value");
                active.commit();
                return null;
            });
            assertTrue(callbackEntered.await(2, TimeUnit.SECONDS), "commit callback should start");
            Future<?> shutdown = executor.submit(ex::close);
            awaitDraining();
            releaseCallback.countDown();

            commit.get(2, TimeUnit.SECONDS);
            shutdown.get(2, TimeUnit.SECONDS);
            assertInstanceOf(IllegalStateException.class, callbackCloseFailure.get());
            assertTrue(callbackCloseFailure.get().getMessage().contains("transaction callback"));
        }
        finally
        {
            releaseCallback.countDown();
            if (tr != null)
            {
                tr.close();
            }
            executor.shutdownNow();
        }
    }

    @Test
    void transactionCannotCloseItselfFromLifecycleCallback() throws Exception
    {
        AtomicReference<Tr> current = new AtomicReference<>();
        // @formatter:off
        ex.configure()
                .context(OP)
                .commit()
                    .effect(String.class)
                        .create(value -> current.get().close(), "attempt transaction close from callback");
        // @formatter:on

        Tr tr = ex.begin(OP);
        current.set(tr);
        tr.create("value");
        Exception failure = assertThrows(Exception.class, tr::commit);
        assertTrue(hasCauseMessage(failure, "Cannot close a transaction from one of its callbacks"));

        assertDoesNotThrow(tr::rollback, "caller can still explicitly recover the failed commit");
        assertDoesNotThrow(ex::close);
    }

    @Test
    void libraryCannotCloseFromEventCallback() throws Exception
    {
        // @formatter:off
        ex.configure()
                .context(OP)
                    .immediate()
                    .effect(String.class)
                        .create(value -> ex.close(), "attempt library close from event callback");
        // @formatter:on

        Tr tr = ex.begin(OP);
        Exception failure = assertThrows(Exception.class, () -> tr.create("value"));
        assertTrue(hasCauseMessage(failure, "Cannot close the library from a transaction callback"));
        assertDoesNotThrow(tr::rollback);
    }

    @Test
    void libraryCannotCloseFromIntentCallback() throws Exception
    {
        // @formatter:off
        ex.configure()
                .context(OP)
                    .invariant(String.class)
                        .all(value -> {
                            ex.close();
                            return true;
                        }, "attempt library close from intent callback");
        // @formatter:on

        Tr tr = ex.begin(OP);
        tr.create("value");
        Exception failure = assertThrows(Exception.class, tr::commit);
        assertTrue(hasCauseMessage(failure, "Cannot close the library from a transaction callback"));
        assertDoesNotThrow(tr::rollback);
    }

    private void awaitDraining() throws Exception
    {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        while (System.nanoTime() < deadline)
        {
            try
            {
                Tr probe = ex.begin(OP);
                probe.close();
            }
            catch (IllegalStateException draining)
            {
                return;
            }
            Thread.yield();
        }
        fail("shutdown should reject new transactions while waiting for the active callback");
    }

    private static void await(CountDownLatch latch)
    {
        try
        {
            if (!latch.await(2, TimeUnit.SECONDS))
            {
                throw new IllegalStateException("test callback release timed out");
            }
        }
        catch (InterruptedException interrupted)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("test callback was interrupted", interrupted);
        }
    }

    private static boolean hasCauseMessage(Throwable failure, String expected)
    {
        for (Throwable cause = failure; cause != null; cause = cause.getCause())
        {
            if (cause.getMessage() != null && cause.getMessage().contains(expected))
            {
                return true;
            }
        }
        return false;
    }
}
