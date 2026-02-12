package com.taitl.ex.logic.validation.output;

import java.util.*;
import com.taitl.existential.exceptions.*;

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
