package com.taitl.existential.exceptions;

import static com.taitl.existential.constants.Strings.*;

public class NotFoundException extends ExistentialException
{
    public NotFoundException()
    {
        super(NOT_FOUND);
    }

    public NotFoundException(String message)
    {
        super(message != null ? message : NOT_FOUND);
    }

    public NotFoundException(Throwable cause)
    {
        super(NOT_FOUND, cause);
    }

    public NotFoundException(String message, Throwable cause)
    {
        super(message != null ? message : NOT_FOUND, cause);
    }
}
