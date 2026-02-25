package com.taitl.ex.common.helper;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Reflection helpers for generic type metadata extraction and formatting.
 */
public class Generics
{
    protected Generics()
    {
    }

    public static Type anonymousSuperclassTypeArgument(Class<?> subclass, Class<?> expectedRawSuperclass)
    {
        sane(subclass, "subclass", expectedRawSuperclass, "expectedRawSuperclass");
        Type genericSuperclass = subclass.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType))
        {
            throw new IllegalArgumentException("Expected an anonymous subclass parameterized with a type for "
                    + expectedRawSuperclass.getSimpleName());
        }
        ParameterizedType parameterized = (ParameterizedType) genericSuperclass;
        if (!(parameterized.getRawType() instanceof Class<?>)
                || !expectedRawSuperclass.equals((Class<?>) parameterized.getRawType()))
        {
            throw new IllegalArgumentException("Expected anonymous subclass of "
                    + expectedRawSuperclass.getSimpleName() + ", got: " + genericSuperclass);
        }
        return parameterized.getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeKey<T> inferTypeKeyFromAnonymousSubclass(Class<?> subclass, Class<?> expectedRawSuperclass,
            String owner)
    {
        sane(owner, "owner");
        try
        {
            return (TypeKey<T>) (TypeKey<?>) TypeKey
                    .valueOf(anonymousSuperclassTypeArgument(subclass, expectedRawSuperclass));
        }
        catch (IllegalArgumentException e)
        {
            throw new IllegalStateException(owner + " type key is required. Use " + owner + "(Class<T>), "
                    + owner + "(TypeKey<T>), or anonymous subclass syntax " + owner + "<T>() { ... }", e);
        }
    }

    /**
     * Recursively constructs string representation of a type, including its generic parameters.
     * Example: for Document<JSON>, returns "Document<JSON>" or "com.package.Document<com.other.package.JSON>".
     */
    public static String typeName(Type type, boolean useFullName)
    {
        sane(type, "type");

        if (type instanceof Class<?>)
        {
            Class<?> clz = (Class<?>) type;
            return useFullName ? clz.getCanonicalName() : clz.getSimpleName();
        }

        if (type instanceof ParameterizedType)
        {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> clz = (Class<?>) pType.getRawType();
            String rawType = useFullName ? clz.getCanonicalName() : clz.getSimpleName();
            String arguments = Arrays.stream(pType.getActualTypeArguments())
                    .map(t -> typeName(t, useFullName))
                    .collect(Collectors.joining(", "));
            return rawType + "<" + arguments + ">";
        }

        return type.toString();
    }
}
