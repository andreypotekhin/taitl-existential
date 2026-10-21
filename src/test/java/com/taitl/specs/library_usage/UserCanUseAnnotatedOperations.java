package com.taitl.specs.library_usage;

import com.taitl.existential.annotations.*;
import com.taitl.specs.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class UserCanUseAnnotatedOperations extends SpecBase
{
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
    void overlappingExecutionsOwnIndependentTransactions() throws Exception
    {
        ex.begin("/api/cats").rollback();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch entered = new CountDownLatch(2);
        CountDownLatch proceed = new CountDownLatch(1);
        AnnotatedOperations operations = new AnnotatedOperations();
        try
        {
            Future<String> first = executor.submit(() -> operations.execute("first", entered, proceed));
            Future<String> second = executor.submit(() -> operations.execute("second", entered, proceed));
            boolean bothEntered = entered.await(5, TimeUnit.SECONDS);
            assertTrue(bothEntered, "both annotated methods should overlap: first=" + outcome(first)
                    + ", second=" + outcome(second));
            proceed.countDown();
            assertEquals("first", first.get(5, TimeUnit.SECONDS));
            assertEquals("second", second.get(5, TimeUnit.SECONDS));
        }
        finally
        {
            proceed.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private String outcome(Future<?> future)
    {
        if (!future.isDone())
        {
            return "still running";
        }
        try
        {
            return String.valueOf(future.get());
        }
        catch (Exception failure)
        {
            return failure.toString() + " / " + failure.getCause();
        }
    }

    @Test
    void methodFailureIsPreservedAfterRollback()
    {
        AnnotatedOperations operations = new AnnotatedOperations();
        RuntimeException failure = new RuntimeException("business failure");
        assertSame(failure, assertThrows(RuntimeException.class, () -> operations.fail(failure)));
    }

    public static class AnnotatedOperations
    {
        @Op(opKey = "/api/cats")
        public String execute(String value, CountDownLatch entered, CountDownLatch proceed) throws Exception
        {
            entered.countDown();
            if (!proceed.await(5, TimeUnit.SECONDS))
            {
                throw new TimeoutException("overlapping invocation did not proceed");
            }
            return value;
        }

        @Op(opKey = "/api/cats")
        public void fail(RuntimeException failure)
        {
            throw failure;
        }
    }
}
