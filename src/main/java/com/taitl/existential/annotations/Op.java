package com.taitl.existential.annotations;

import java.lang.annotation.*;

/**
 * Declares an existential transaction.
 * Starts one transaction for each annotated method execution. A normal return commits it;
 * a method or commit failure triggers an explicit rollback and preserves the original failure.
 *
 * Parameters: operation key (string)
 *
 * Implementation: AspectJ execution advice holds the transaction handle locally for that invocation.
 * Annotation processing requires AspectJ weaving to be enabled in the application.
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
