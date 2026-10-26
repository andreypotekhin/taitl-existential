package com.taitl.ex.logic.transactions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.stages.begin.*;
import com.taitl.ex.logic.stages.checkpoint.*;
import com.taitl.ex.logic.stages.commit.*;
import com.taitl.ex.logic.stages.immediate.*;
import com.taitl.ex.logic.stages.rollback.*;
import com.taitl.ex.logic.stages.validation.*;
import com.taitl.ex.logic.transactions.actions.*;
import com.taitl.ex.logic.transactions.data.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.io.*;
import java.time.*;
import java.util.concurrent.*;

import static com.taitl.ex.common.helper.Args.*;

public class TransactionLogic implements Closeable
{
    private enum State
    {
        OPEN, DRAINING, CLOSED
    }

    private final Object lifecycle = new Object();
    private final ThreadLocal<Integer> callbackDepth = ThreadLocal.withInitial(() -> 0);
    private final ThreadLocal<Integer> handlerDepth = ThreadLocal.withInitial(() -> 0);
    private State state = State.OPEN;
    private int inFlight;
    @Up
    protected ExistentialTransactions ee;

    @Logic
    protected TrRegistry registry;

    @Logic
    protected CreateTran createTran;

    @Logic
    protected BeginTran beginTran;

    @Logic
    protected CommitTran commitTran;

    @Logic
    protected CheckpointTran checkpointTran;

    @Logic
    protected RollbackTran rollbackTran;

    @Logic
    protected DisposeTran disposeTran;

    @Logic
    public EvaluationLogic evaluationLogic;

    @Logic
    public BeginLogic beginLogic;

    @Logic
    public ImmediateLogic immediateLogic;

    @Logic
    public CommitLogic commitLogic;

    @Logic
    public CheckpointLogic checkpointLogic;

    @Logic
    public RollbackLogic rollbackLogic;

    @Logic
    public ValidationLogic validationLogic;

    public TransactionLogic(ExistentialTransactions ee)
    {
        this.ee = ee;
        this.createTran = Creator.create(CreateTran.class, new Class[] { TransactionLogic.class }, this);
        this.registry = Creator.create(TrRegistry.class, new Class[] { TransactionLogic.class, CreateTran.class },
                this, createTran);
        this.beginTran = Creator.create(BeginTran.class, new Class[] { TransactionLogic.class }, this);
        this.commitTran = Creator.create(CommitTran.class, new Class[] { TransactionLogic.class }, this);
        this.checkpointTran = Creator.create(CheckpointTran.class, new Class[] { TransactionLogic.class }, this);
        this.rollbackTran = Creator.create(RollbackTran.class, new Class[] { TransactionLogic.class }, this);
        this.disposeTran = Creator.create(DisposeTran.class, new Class[] { TransactionLogic.class }, this);
        this.evaluationLogic = Creator.create(EvaluationLogic.class, new Class[] { TransactionLogic.class }, this);
        this.beginLogic = Creator.create(BeginLogic.class, new Class[] { TransactionLogic.class }, this);
        this.immediateLogic = Creator.create(ImmediateLogic.class, new Class[] { TransactionLogic.class }, this);
        this.commitLogic = Creator.create(CommitLogic.class, new Class[] { TransactionLogic.class }, this);
        this.checkpointLogic = Creator.create(CheckpointLogic.class, new Class[] { TransactionLogic.class }, this);
        this.rollbackLogic = Creator.create(RollbackLogic.class, new Class[] { TransactionLogic.class }, this);
        this.validationLogic = Creator.create(ValidationLogic.class, new Class[] { TransactionLogic.class }, this);
    }

    public Tr begin(String op)
    {
        sane(op, "op");
        OpKey.validate(op);
        enter(true);
        Tr tr = null;
        try
        {
            ee.ex().configs().done();
            tr = registry.create(op, null);
            beginTran.call(tr);
            return tr;
        }
        catch (RuntimeException | Error failure)
        {
            if (tr != null)
            {
                registry.removeIfPresent(tr.id.toString());
                closeAfterFailure(tr, failure);
            }
            throw failure;
        }
        finally
        {
            leave();
        }
    }

    public Tr begin(String op, Transaction custom)
    {
        sane(op, "op");
        OpKey.validate(op);
        enter(true);
        Tr tr = null;
        try
        {
            ee.ex().configs().done();
            tr = registry.create(op, custom);
            beginTran.call(tr);
            return tr;
        }
        catch (RuntimeException | Error failure)
        {
            if (tr != null)
            {
                registry.removeIfPresent(tr.id.toString());
                closeAfterFailure(tr, failure);
            }
            throw failure;
        }
        finally
        {
            leave();
        }
    }

    public void checkpoint(Tr tr)
    {
        sane(tr, "tr");
        enter(false);
        try
        {
            checkpointTran.call(tr);
        }
        catch (RuntimeException | Error failure)
        {
            registry.removeIfPresent(tr.id.toString());
            throw failure;
        }
        finally
        {
            leave();
        }
    }

    public void commit(Tr tr)
    {
        sane(tr, "tr");
        enter(false);
        try
        {
            commitTran.call(tr);
            disposeTran.call(tr);
        }
        catch (RuntimeException | Error failure)
        {
            registry.removeIfPresent(tr.id.toString());
            throw failure;
        }
        finally
        {
            leave();
        }
    }

    public void rollback(Tr tr)
    {
        sane(tr, "tr");
        synchronized (lifecycle)
        {
            if (state == State.CLOSED)
            {
                tr.close();
                throw new IllegalStateException("Cannot run rollback callbacks after library shutdown. "
                        + "Complete rollback before closing the library. See "
                        + "/Troubleshooting.md#library-shutdown-waits-for-an-active-transaction");
            }
            inFlight++;
            callbackDepth.set(callbackDepth.get() + 1);
        }
        Throwable failure = null;
        try
        {
            rollbackTran.call(tr);
        }
        catch (Throwable caught)
        {
            failure = caught;
        }
        try
        {
            disposeTran.call(tr);
        }
        catch (Throwable cleanupFailure)
        {
            if (failure == null)
            {
                failure = cleanupFailure;
            }
            else if (failure != cleanupFailure)
            {
                failure.addSuppressed(cleanupFailure);
            }
        }
        finally
        {
            leave();
        }
        rethrow(failure);
    }

    public Existential ex()
    {
        return ee.ex();
    }

    public TrRegistry registry()
    {
        return registry;
    }

    public Tr tr(String tranID)
    {
        sane(tranID, "tranID");
        return registry.get(tranID);
    }

    /** Close after active transactions and transaction calls have completed. */
    public void close(ShutdownPolicy policy)
    {
        sane(policy, "policy");
        ensureShutdownAllowed();
        long timeoutNanos = durationNanos(policy.timeout());
        long start = System.nanoTime();
        synchronized (lifecycle)
        {
            if (state == State.CLOSED)
            {
                return;
            }
            state = State.DRAINING;
            while (registry.size() > 0 || inFlight > 0)
            {
                try
                {
                    if (timeoutNanos == 0)
                    {
                        lifecycle.wait();
                    }
                    else
                    {
                        long remaining = timeoutNanos - (System.nanoTime() - start);
                        if (remaining <= 0)
                        {
                            throw shutdownTimeout();
                        }
                        long millis = TimeUnit.NANOSECONDS.toMillis(remaining);
                        int nanos = (int) (remaining - TimeUnit.MILLISECONDS.toNanos(millis));
                        lifecycle.wait(millis, nanos);
                    }
                }
                catch (InterruptedException interrupted)
                {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting for active transactions to finish. "
                            + "The library remains in shutdown; retry close() after transactions complete. See "
                            + "/Troubleshooting.md#library-shutdown-waits-for-an-active-transaction",
                            interrupted);
                }
            }
            registry.clear();
            evaluationLogic.close();
            state = State.CLOSED;
            lifecycle.notifyAll();
        }
    }

    public void close()
    {
        close(ShutdownPolicy.graceful());
    }

    /** Reject shutdown before a caller acquires any shutdown-coordination lock. */
    public void ensureShutdownAllowed()
    {
        if (callbackDepth.get() > 0)
        {
            throw new IllegalStateException("Cannot close the library from a transaction callback. "
                    + "Close it after the transaction call returns. See "
                    + "/Troubleshooting.md#library-shutdown-waits-for-an-active-transaction");
        }
    }

    public boolean transactionCloseStarted(Tr tr)
    {
        synchronized (lifecycle)
        {
            if (handlerDepth.get() > 0)
            {
                throw new IllegalStateException("Cannot close a transaction from one of its callbacks. "
                        + "Close it after the callback returns. See "
                        + "/Troubleshooting.md#library-shutdown-waits-for-an-active-transaction");
            }
            registry.removeIfPresent(tr.id.toString());
            if (state == State.CLOSED)
            {
                lifecycle.notifyAll();
                return false;
            }
            inFlight++;
            lifecycle.notifyAll();
            return true;
        }
    }

    public void transactionCloseFinished()
    {
        synchronized (lifecycle)
        {
            inFlight--;
            lifecycle.notifyAll();
        }
    }

    public void callbackStarted()
    {
        synchronized (lifecycle)
        {
            if (state == State.CLOSED)
            {
                throw new IllegalStateException("Cannot execute callbacks after library shutdown.");
            }
            inFlight++;
            callbackDepth.set(callbackDepth.get() + 1);
            handlerDepth.set(handlerDepth.get() + 1);
        }
    }

    public void callbackFinished()
    {
        synchronized (lifecycle)
        {
            int depth = handlerDepth.get() - 1;
            if (depth == 0)
            {
                handlerDepth.remove();
            }
            else
            {
                handlerDepth.set(depth);
            }
            leave();
        }
    }

    private void enter(boolean beginning)
    {
        synchronized (lifecycle)
        {
            if (state != State.OPEN && beginning)
            {
                throw new IllegalStateException("Cannot begin a transaction because library shutdown has started. "
                        + "See /Troubleshooting.md#library-shutdown-waits-for-an-active-transaction");
            }
            if (state == State.CLOSED)
            {
                throw new IllegalStateException("Cannot use a transaction after library shutdown.");
            }
            inFlight++;
            callbackDepth.set(callbackDepth.get() + 1);
        }
    }

    private void leave()
    {
        synchronized (lifecycle)
        {
            inFlight--;
            int depth = callbackDepth.get() - 1;
            if (depth == 0)
            {
                callbackDepth.remove();
            }
            else
            {
                callbackDepth.set(depth);
            }
            lifecycle.notifyAll();
        }
    }

    private long durationNanos(Duration duration)
    {
        try
        {
            return duration.toNanos();
        }
        catch (ArithmeticException overflow)
        {
            return Long.MAX_VALUE;
        }
    }

    private IllegalStateException shutdownTimeout()
    {
        return new IllegalStateException("Timed out waiting for active transactions to finish. "
                + "The library remains in shutdown; finish or close active transactions, then retry close(). See "
                + "/Troubleshooting.md#library-shutdown-waits-for-an-active-transaction");
    }

    private void closeAfterFailure(Tr tr, Throwable failure)
    {
        try
        {
            tr.close();
        }
        catch (RuntimeException | Error cleanupFailure)
        {
            if (failure != cleanupFailure)
            {
                failure.addSuppressed(cleanupFailure);
            }
        }
    }

    private void rethrow(Throwable failure)
    {
        if (failure instanceof ExistentialException)
        {
            throw (ExistentialException) failure;
        }
        if (failure instanceof RuntimeException)
        {
            throw (RuntimeException) failure;
        }
        if (failure instanceof Error)
        {
            throw (Error) failure;
        }
        if (failure != null)
        {
            throw new ExistentialException("Transaction cleanup failed", failure);
        }
    }
}
