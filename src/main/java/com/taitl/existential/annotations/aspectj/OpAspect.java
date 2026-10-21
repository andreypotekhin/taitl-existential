package com.taitl.existential.annotations.aspectj;

import com.taitl.existential.*;
import com.taitl.existential.annotations.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

/**
 * Starts and completes one transaction for each annotated operation invocation.
 */
@Aspect
public class OpAspect
{
    @Around(value = "execution(@com.taitl.existential.annotations.Op * *(..)) && @annotation(operation)", argNames = "joinPoint,operation")
    public Object invoke(ProceedingJoinPoint joinPoint, Op operation) throws Throwable
    {
        Tr tr = Ex.begin(operation.opKey());
        try
        {
            Object result = joinPoint.proceed();
            tr.commit();
            return result;
        }
        catch (Throwable failure)
        {
            try
            {
                tr.rollback();
            }
            catch (Throwable rollbackFailure)
            {
                if (rollbackFailure != failure)
                {
                    failure.addSuppressed(rollbackFailure);
                }
            }
            throw failure;
        }
    }
}
