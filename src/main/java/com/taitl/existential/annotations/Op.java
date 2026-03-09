package com.taitl.existential.annotations;

import java.lang.annotation.*;

/**
 * Declares an existential transaction.
 * Automatically calls Ex.begin(), Ex.commit(), and Ex.rollback()
 * around the method.
 *
 * Parameters: operation key (string)
 *
 * Implementation: AspectJ pointcut that matches methods with this annotation and wraps them in a transaction.
 * Around: Ex.begin(), Ex.commit()
 * Exception/throwable: Ex.rollback()
 *
 * Usage:
 * @Op("api/user/update")
 * public void updateUser(User user){}
 *
 * @see com.taitl.existential.keys.OpKey
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Op
{
    String opKey();
}
