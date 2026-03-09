package com.taitl.existential.annotations.aspectj;

import com.taitl.existential.*;
import com.taitl.existential.annotations.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

/**
 * Powers @Op annotation
 * Calls transaction methods of Existential library:
 * Ex.begin()/Ex.commit()/Ex.rollback()
 *
 * Annotation:
 * @see com.taitl.existential.annotations.Op
 */
@Aspect
public class OpAspect
{
    String tranID;

    @Before("@annotation(transaction)")
    public void beginTransaction(JoinPoint joinPoint, Op transaction)
            throws ExistentialException
    {
        Tr tr = Ex.begin(transaction.opKey());
        tranID = tr.id();
    }

    @AfterReturning("@annotation(transaction)")
    public void commitTransaction(JoinPoint joinPoint, Op transaction)
            throws ExistentialException
    {
        Ex.commit(tranID);
    }

    @AfterThrowing(value = "@annotation(transaction)", throwing = "ex")
    public void rollbackTransaction(JoinPoint joinPoint, Op transaction, Throwable ex)
            throws ExistentialException
    {
        Ex.rollback(tranID);
    }
}