package com.taitl.existential;

import com.taitl.existential.constants.Strings;

/**
 * A string representing a class, along with its generics, for example "Set<Car>".
 * 
 * For types without generics, it corresponds to the short name of class: Class: Car, Type: "Car" For types with
 * generics, it corresponds to short name of with generic qualifier: Class: Set<House>, Type: "Set<House>"
 * 
 * @author Andrey Potekhin
 *
 */
public class Type
{
    String typeid;

    public Type(Object t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException(Strings.ARG_T);
        }
        // Warning: will loose generic qualifier, if any
        typeid = t.getClass().getSimpleName();
    }

    public Type(Class<?> clz)
    {
        if (clz == null)
        {
            throw new IllegalArgumentException(Strings.ARG_CLZ);
        }
        // Warning: will loose generic qualifier, if any
        typeid = clz.getSimpleName();
    }

    public Type(Object t, String classNameQualifiedWithGenerics)
    {
        // Most suitable for classes with generic qualifiers
        typeid = classNameQualifiedWithGenerics;
    }

    public String getTypeid()
    {
        return typeid;
    }
}
