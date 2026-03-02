package com.taitl.ex.logic.stages.validation.output;

import com.taitl.existential.exceptions.*;

import java.util.*;

/**
 * Results of validation - a record of constraint violations.
 */
public class ValidationReport
{
    List<ExistentialException> exceptions = new ArrayList<>();

    public void addException(ExistentialException ex)
    {
        exceptions.add(ex);
    }

    public List<ExistentialException> exceptions()
    {
        return exceptions;
    }

    public boolean isEmpty()
    {
        return exceptions.isEmpty();
    }
}
