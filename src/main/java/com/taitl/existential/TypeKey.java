package com.taitl.existential;

import com.taitl.existential.constants.Strings;

/**
 * A string representing a type along with its generics, for example "Set<Car>".
 * 
 * This class is to offset the effects of Java type erasure, in scenarios where we need to know the generic qualifier. 
 * 
 * For types without generics, it corresponds to the short name of class (like "String"). If class is qualified with
 * generics, it corresponds to class short name with generic qualifier, like "Set<House>".
 * 
 * Examples: Class without generics: Class: Car, Type: "Car" Class with generics: Class: Set<House>, Type: "Set<House>"
 * 
 * @author Andrey Potekhin
 * @see EventHandlers
 */
public class TypeKey<T>
{
    protected String typeid;

    // public TypeKey(T t, String genericQualifier)
    // {
    // if (t == null)
    // {
    // throw new IllegalArgumentException(Strings.ARG_T);
    // }
    // setTypeid(t.getClass(), genericQualifier);
    // }

    public TypeKey(Class<?> clz, String genericQualifier)
    {
        if (clz == null)
        {
            throw new IllegalArgumentException(Strings.ARG_CLZ);
        }
        setTypeid(clz, genericQualifier);
    }

    public TypeKey(Class<?> clz)
    {
        this(clz, null);
    }

    public TypeKey(String classNameQualifiedWithGenerics)
    {
        typeid = classNameQualifiedWithGenerics;
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz, String genericQualifier)
    {
        return new TypeKey<T>(clz, genericQualifier);
    }

    public static <T> TypeKey<T> valueOf(Class<?> clz)
    {
        return new TypeKey<T>(clz, null);
    }

    public static <T> TypeKey<T> valueOf(String classNameQualifiedWithGenerics)
    {
        return new TypeKey<T>(classNameQualifiedWithGenerics);
    }

    public int hashCode()
    {
        return typeid.hashCode();
    }

    public boolean equals(TypeKey<T> other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (other.typeid == null)
        {
            return (this.typeid == null);
        }
        return other.typeid.equals(this.typeid);
    }

    public String toString()
    {
        return typeid;
    }

    protected void setTypeid(Class<?> clz, String genericQualifier)
    {
        if (clz == null)
        {
            throw new IllegalArgumentException(Strings.ARG_CLZ);
        }
        if (genericQualifier == null || genericQualifier.isEmpty())
        {
            typeid = clz.getSimpleName();
        }
        else if (genericQualifier.startsWith("<") && genericQualifier.endsWith(">"))
        {
            typeid = clz.getSimpleName() + genericQualifier;
        }
        else
        {
            typeid = clz.getSimpleName() + "<" + genericQualifier + ">";
        }
    }
}
